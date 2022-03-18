# LVDB - LLOOGG Memory DB
# Copyriht (C) 2009 Salvatore Sanfilippo <antirez@gmail.com>
# All Rights Reserved

# TODO
# - cron with cleanup of timedout clients, automatic dump
# - the dump should use array startsearch to write it line by line
#   and may just use gets to read element by element and load the whole state.
# - 'help','stopserver','saveandstopserver','save','load','reset','keys' commands.
# - ttl with milliseconds resolution 'ttl a 1000'. Check ttl in dump!
# - cluster. Act as master, send write ops to all servers, get from one at random. Auto-serialization.
# - 'hold' and 'continue' command, for sync in cluster mode
# - auto-sync, consider lazy copy or log of operations to re-read at start
# - client timeout
# - save dump in temp file.[clock ticks] than rename it

package require Tclx ;# For [fork]

array set ::clients {}
array set ::state {}
array set ::readlen {}
array set ::readbuf {}
array set ::db {}
array set ::ttl {}
set ::dirty 0
set ::lastsaved 0
set ::listensocket {}

signal -restart block SIGCHLD

# the K combinator is using for Tcl object refcount hacking
# in order to avoid useless object copy.
proc K {x y} {
    set x
}

proc headappend {var e} {
    upvar 1 $var l
    set l [lreplace [K $l [set l {}]] -1 -1 $e]
}

proc log msg {
    puts stderr "[clock format [clock seconds]]\] $msg "
}

proc warning msg {
    log "*** WARNING: $msg"
}

proc writemsg {fd msg} {
    puts -nonewline $fd $msg
    puts -nonewline $fd "\r\n"
}

proc resetclient {fd} {
    set ::clients($fd) [clock seconds]
    set ::state($fd) {}
    set ::readlen($fd) 0
    set ::readbuf($fd) {}
}

proc accept {fd addr port} {
    resetclient $fd
    fconfigure $fd -blocking 0 -translation binary -encoding binary
    fileevent $fd readable [list readrequest $fd]
}

proc readrequest fd {
    if [eof $fd] {
        closeclient $fd
        return
    }

    # Handle bulk read
    if {$::state($fd) ne {}} {
        set buf [read $fd [expr {$::readlen($fd)-[string length $::readbuf($fd)]}]]
        append ::readbuf($fd) $buf
        if {[string length $::readbuf($fd)] >= $::readlen($fd)} {
            set ::readbuf($fd) [string range $::readbuf($fd) 0 end-2]
            lappend ::state($fd) $::readbuf($fd)
            cmd_[lindex $::state($fd) 0] $fd $::state($fd)
        }
        return
    }

    # Handle first line request
    set req [string trim [gets $fd] "\r\n "]
    if {$req eq {}} return

    # Process command
    set args [split $req]
    set cmd [string tolower [lindex $args 0]]
    foreach ct $::cmdtable {
        if {$cmd eq [lindex $ct 0] && [llength $args] == [lindex $ct 1]} {
            if {[lindex $ct 2] eq {inline}} {
                cmd_$cmd $fd $args
            } else {
                set readlen [lindex $args end]
                if {$readlen < 0 || $readlen > 1024*1024} {
                    writemsg $fd "protocol error: invalid bulk read length"
                    closeclient $fd
                    return
                }
                bulkread $fd [lrange $args 0 end-1] $readlen
            }
            return
        }
    }
    writemsg $fd "protocol error: invalid command '$cmd'"
    closeclient $fd
}

proc bulkread {fd argv len} {
    set ::state($fd) $argv
    set ::readlen($fd) [expr {$len+2}]  ;# Add two bytes for CRLF
}

proc closeclient fd {
    unset ::clients($fd)
    unset ::state($fd)
    unset ::readlen($fd)
    unset ::readbuf($fd)
    close $fd
}

proc cron {} {
    # Todo timeout clients timeout
    puts "lmdb: [array size ::db] keys, [array size ::clients] clients, dirty: $::dirty, lastsaved: $::lastsaved"
    after 1000 cron
}

set ::cmdtable {
    {ping 1 inline}
    {quit 1 inline}
    {set 3 bulk}
    {get 2 inline}
    {exists 2 inline}
    {delete 2 inline}
    {incr 2 inline}
    {decr 2 inline}
    {lpush 3 bulk}
    {rpush 3 bulk}
    {save 1 inline}
    {bgsave 1 inline}
}

proc okreset {fd {msg OK}} {
    writemsg $fd $msg
    flush $fd
    resetclient $fd
}

proc cmd_ping {fd argv} {
    writemsg $fd "PONG"
    flush $fd
    resetclient $fd
}

proc cmd_quit {fd argv} {
    okreset $fd
    closeclient $fd
}

proc cmd_set {fd argv} {
    set ::db([lindex $argv 1]) [lindex $argv 2]
    incr ::dirty
    okreset $fd
}

proc cmd_get {fd argv} {
    if {[info exists ::db([lindex $argv 1])]} {
        set val $::db([lindex $argv 1])
    } else {
        set val {}
    }
    writemsg $fd [string length $val]
    writemsg $fd $val
    flush $fd
    resetclient $fd
}

proc cmd_exists {fd argv} {
    if {[info exists ::db([lindex $argv 1])]} {
        set res 1
    } else {
        set res 0
    }
    writemsg $fd $res
    flush $fd
    resetclient $fd
}

proc cmd_delete {fd argv} {
    unset -nocomplain -- ::db([lindex $argv 1])
    incr ::dirty
    writemsg $fd "OK"
    flush $fd
    resetclient $fd
}

proc cmd_incr {fd argv} {
    cmd_incrdecr $fd $argv 1
}

proc cmd_decr {fd argv} {
    cmd_incrdecr $fd $argv -1
}

proc cmd_incrdecr {fd argv n} {
    if {[catch {
        incr ::db([lindex $argv 1]) $n
    }]} {
        set ::db([lindex $argv 1]) $n
    }
    incr ::dirty
    writemsg $fd $::db([lindex $argv 1])
    flush $fd
    resetclient $fd
}

proc cmd_lpush {fd argv} {
    cmd_push $fd $argv -1
}

proc cmd_rpush {fd argv} {
    cmd_push $fd $argv 1
}

proc cmd_push {fd argv dir} {
    if {[catch {
        llength $::db([lindex $argv 1])
    }]} {
        if {![info exists ::db([lindex $argv 1])]} {
            set ::db([lindex $argv 1]) {}
        } else {
            set ::db([lindex $argv 1]) [split $::db([lindex $argv 1])]
        }
    }
    if {$dir == 1} {
        lappend ::db([lindex $argv 1]) [lindex $argv 2]
    } else {
        headappend ::db([lindex $argv 1]) [lindex $argv 2]
    }
    incr ::dirty
    okreset $fd
}

proc savedb {} {
    set err [catch {
        set fp [open "saved.lmdb" w]
        fconfigure $fp -encoding binary -translation binary
        set search [array startsearch ::db]
        set elements [array size ::db]
        for {set i 0} {$i < $elements} {incr i} {
            set key [array nextelement ::db $search]
            set val $::db($key)
            puts $fp "[string length $key] [string length $val]"
            puts -nonewline $fp $key
            puts -nonewline $fp $val
        }
        close $fp
        set ::dirty 0
        set ::lastsaved [clock seconds]
    } errmsg]
    if {$err} {return $errmsg}
    return {}
}

proc backgroundsave {} {
    unset -nocomplain ::dbcopy
    array set ::dbcopy [array get ::db]
}

proc cmd_bgsave {fd argv} {
    backgroundsave
    okreset $fd
}

proc cmd_save {fd argv} {
    set errmsg [savedb]
    if {$errmsg ne {}} {
        okreset $fd "ER"
        warning "Error trying to save: $errmsg"
    } else {
        okreset $fd
        log "State saved"
    }
}

proc loaddb {} {
    set err [catch {
        set fp [open "saved.lmdb"]
        fconfigure $fp -encoding binary -translation binary
        set count 0
        while {[gets $fp len] != -1} {
            set key [read $fp [lindex $len 0]]
            set val [read $fp [lindex $len 1]]
            set ::db($key) $val
            incr count
        }
        log "$count keys loaded"
        close $fp
    } errmsg]
    if {$err} {
        warning "Loading DB from file: $errmsg"
    }
    return $err
}

proc main {} {
    log "Server started"
    if {[file exists saved.lmdb]} loaddb
    set ::dirty 0
    set ::listensocket [socket -server accept 6379]
    cron
}

main
vwait forever