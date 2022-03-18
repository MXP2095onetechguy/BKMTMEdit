#!/usr/bin/env pwsh

# MXPSQL HashCheck
# Free to use

function hashCheck(){
    param(
        [Parameter(mandatory=$true)][String]$file,
        [Parameter(mandatory=$true)][String]$file2
    )

    # Get the file hashes
    $hashSrc = Get-FileHash $file -Algorithm "SHA256"
    $hashDest = Get-FileHash $file2 -Algorithm "SHA256"

    Write-Host "checking hash of $file and $file2";

    # Compare the hashes & note this in the log
    If ($hashSrc.Hash -ne $hashDest.Hash)
    {
        Write-Error "File hash of $file and $file2 does not match.";
    }
    else{
        Write-Host "File hash of $file and $file2 match";
    }
}

if($MXPSQL_HASHCHECK_NO_RUN -eq $null){
    hashCheck;
}