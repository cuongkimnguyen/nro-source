@echo off
set "lockfile=autorun_lock.txt"

rem Kiem tra xem script da chay chua
if exist "%lockfile%" (
    echo Autorun da dang chay. Dang thoat...
    timeout /t 5
    exit
)

rem Tao file lock
echo %random% > "%lockfile%"

:loop
rem Kiem tra xem file lock con ton tai khong
if not exist "%lockfile%" (
    echo File lock da bi xoa. Dang thoat...
    exit
)

rem Kiem tra port 14445 co dang LISTENING khong (chi kiem tra TCP)
netstat -an | find "TCP" | find ":14445 " | find "LISTENING" > nul
if errorlevel 1 (
    echo Port 14445 khong dang LISTENING. Dang build va khoi dong game server...
    
    rem Chay build.bat va doi cho den khi hoan thanh
    echo Dang chay build.bat...
    call build.bat
    if errorlevel 1 (
        echo Build that bai! Dang thu lai sau 60 giay...
        timeout /t 60 /nobreak
        goto loop
    )
    
    echo Build thanh cong! Dang khoi dong server...
    start run.bat
    
    rem Doi 15 giay truoc khi kiem tra lai (du thoi gian cho server khoi dong)
    timeout /t 15 /nobreak
) else (
    echo Port 14445 dang LISTENING.
)

timeout /t 30 /nobreak
goto loop

rem Cleanup khi script bi dong
:cleanup
if exist "%lockfile%" del "%lockfile%"
exit