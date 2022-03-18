#!/usr/bin/env sh
# SP, config parser
# Can be sourced or run as standalone

function sp_parse(){

    # Vars
    SP_ParseFile="";
    SP_IDKey="";

    # arg parser
    for i in "$@"; do
        case $i in
            -f=*|--file=*)
                SP_ParseFile="${i#*=}";
                shift;
                ;;
            -k=*|--key=*)
                SP_IDKey="${i#*=}";
                shift;
                ;;
            *)
                ;;
        esac;
    done;

    # read file
    SP_Str=$(cat "$SP_ParseFile");

    # text parser
    while IFS= read -r line; do 
        # check if the key is defined
        if test -z "$SP_IDKey"; then
            break;
        fi

        # check if the line is empty
        if test -z "$line"; then
            continue;
        fi

        # line parser
        while IFS=$'[ \t]*=[ \t]*' read -r k v; do
            if test "$k" = "$SP_IDKey"; then
                echo "$v";
            fi
        done <<< "$line";
    done <<< "$SP_Str";


    SP_ParseFile=
    SP_IDKey=

    return 0;
}

test "$_" = "$0"  && sp_parse $@