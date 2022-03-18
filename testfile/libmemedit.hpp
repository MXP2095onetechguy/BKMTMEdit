#include <iostream>

// A memory editing library for C++

#ifndef MXPSQL_MemEdit_HPP
#define MXPSQL_MemEdit_HPP

#if defined(_WIN32) || defined(WIN32) || defined(__CYGWIN__) || defined(__MINGW32__) || defined(__BORLANDC__)
    #define OS_WIN
#elif defined(__unix__) || defined(__unix) || defined(unix) || defined(__linux__) || defined(__linux) || defined(linux) || defined(__gnu_linux__)
    #define OS_POSIX
#endif

#ifdef OS_WIN
    #include <windows.h>
#elif defined(OS_POSIX)
    #include <unistd.h>
#endif

namespace MXPSQL::MemEdit{

    // Abstract PROCess object
    // abstract the process
    struct AProc{
        #ifdef OS_WIN
            HANDLE hProcess;
        #elif defined(OS_POSIX)
            pid_t pid;
        #endif
    };

    // set AProc to current
    AProc set_proc(){
        AProc p;
        #ifdef OS_WIN
            p.hProcess = GetCurrentProcess();
        #elif defined(OS_POSIX)
            p.pid = getpid();
        #endif
        return p;
    }

    // set AProc to a native object
    AProc set_proc_native(void* p){
        AProc proc;
        #ifdef OS_WIN
            proc.hProcess = (HANDLE)p;
        #elif defined(OS_POSIX)
            proc.pid = (pid_t)p;
        #endif
        return proc;
    }

    // get native object from AProc
    auto get_proc(AProc p){
        #ifdef OS_WIN
            return p.hProcess;
        #elif defined(OS_POSIX)
            return p.pid;
        #endif
    }

    // set memory of a process (AProc)
    bool set_memory(AProc& p, void* addr, void* data, size_t size){
        #ifdef OS_WIN
            if(!WriteProcessMemory(p.hProcess, addr, data, size, NULL)){
                return false;
            }
        #elif defined(OS_POSIX)
            if(!(pwrite(p.pid, data, size, addr) == size)){
                return false;
            }
        #endif
    }

    // get memory of a process (AProc)
    bool get_memory(AProc& p, void& result, void* addr size_t size){
        #ifdef OS_WIN
            if(!ReadProcessMemory(p.hProcess, addr, result, size, NULL)){
                return false;
            }
        #elif defined(OS_POSIX)
            if(!(pread(p.pid, result, size, addr) == size)){
                return false;
            }
        #endif
    }

    // get the physical size of the ram
    size_t get_ram_size(){
        #ifdef OS_WIN
            MEMORYSTATUSEX mem;
            mem.dwLength = sizeof(MEMORYSTATUSEX);
            GlobalMemoryStatusEx(&mem);
            return mem.ullTotalPhys;
        #elif defined(OS_POSIX)
            return sysconf(_SC_PHYS_PAGES) * sysconf(_SC_PAGE_SIZE);
        #endif
    }
};

#endif //MXPSQL_MemEdit_HPP