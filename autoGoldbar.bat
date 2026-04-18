@echo off
:loop
REM Lấy giờ và phút hiện tại
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set CURRENT_HOUR=%datetime:~8,2%
set CURRENT_MINUTE=%datetime:~10,2%
set CURRENT_SECOND=%datetime:~12,2%

REM Tính thời gian còn lại đến lần chạy tiếp theo
if %CURRENT_MINUTE% LSS 30 (
    REM Đếm ngược đến phút 30
    set /a "SECONDS_LEFT=(30 - %CURRENT_MINUTE% - 1) * 60 + (60 - %CURRENT_SECOND%)"
) else (
    REM Đếm ngược đến phút 00 của giờ tiếp
    set /a "SECONDS_LEFT=(60 - %CURRENT_MINUTE% - 1) * 60 + (60 - %CURRENT_SECOND%)"
)

REM Kiểm tra và chạy script nếu là phút 00 hoặc 30
if "%CURRENT_MINUTE%"=="00" (
    call check_gold.bat
) else if "%CURRENT_MINUTE%"=="30" (
    call check_gold.bat
)

REM Hiển thị thời gian đếm ngược
cls
echo Thoi gian con lai den lan chay tiep theo: %SECONDS_LEFT% giay
echo Thoi gian hien tai: %CURRENT_HOUR%:%CURRENT_MINUTE%:%CURRENT_SECOND%
echo Dang cho den phut 00 hoac 30 tiep theo...

REM Chờ 1 giây và lặp lại
timeout /t 1 /nobreak > nul
goto loop