@echo off
chcp 65001
setlocal enabledelayedexpansion

:: C?u hình MySQL
set MYSQL_PATH="C:\xampp\mysql\bin"
set DB_HOST=localhost
set DB_USER=root
set DB_PASSWORD=
set DB_NAME=nrobaby
set BACKUP_PATH=C:\Users\Administrator\Downloads\nr_Bb\NROKAME\backup_sql\

:: T?o thu m?c backup n?u chua t?n t?i
if not exist "%BACKUP_PATH%" mkdir "%BACKUP_PATH%"

echo ========================================
echo    MYSQL BACKUP SCRIPT
echo    Database: %DB_NAME%
echo    Backup Path: %BACKUP_PATH%
echo ========================================
echo.

:: L?y th?i gian chi ti?t
for /f "tokens=1-4 delims=:., " %%a in ("%time%") do (
    set hour=%%a
    set minute=%%b
    set second=%%c
)

:: X? lý tru?ng h?p gi? có 1 ch? s? (thêm s? 0 vào tru?c)
if "!hour:~0,1!" == " " set hour=0!hour:~1,1!
if "!minute:~0,1!" == " " set minute=0!minute:~1,1!
if "!second:~0,1!" == " " set second=0!second:~1,1!

:: L?y ngày tháng nam
for /f "tokens=1-3 delims=/" %%a in ("%date%") do (
    set day=%%a
    set month=%%b
    set year=%%c
)

:: T?o tên file theo d?nh d?ng: hunr_2025_YYYYMMDD_HHMMSS.sql
set BACKUP_FILE=%BACKUP_PATH%%DB_NAME%_!year!!month!!day!_!hour!!minute!!second!.sql

echo [%date% %time%] B?t d?u backup database...

:: Th?c hi?n backup v?i du?ng d?n d?y d? d?n mysqldump
%MYSQL_PATH%\mysqldump.exe --host=%DB_HOST% --user=%DB_USER% ^
--default-character-set=utf8mb4 ^
--skip-lock-tables ^
--single-transaction ^
--routines ^
--triggers ^
--events ^
--add-drop-database ^
--databases %DB_NAME% > "!BACKUP_FILE!"

if !ERRORLEVEL! EQU 0 (
    echo [%date% %time%] Backup thành công: !BACKUP_FILE!
    echo.
    echo Backup hoàn t?t!
) else (
    echo [%date% %time%] L?I: Không th? backup database
    echo.
    echo Backup th?t b?i!
)

echo.
endlocal