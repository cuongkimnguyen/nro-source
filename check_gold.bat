@echo off
setlocal EnableDelayedExpansion

REM MySQL path và thông tin kết nối
set MYSQL_PATH="C:\xampp\mysql\bin\mysql.exe"
set MYSQL_HOST=localhost
set MYSQL_USER=root
set MYSQL_DB=nro7sao

REM Lấy ngày giờ đầy đủ
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set CURRENT_TIME=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%_%datetime:~8,2%:%datetime:~10,2%:%datetime:~12,2%

REM Thực hiện truy vấn SQL và lưu kết quả
%MYSQL_PATH% -h %MYSQL_HOST% -u %MYSQL_USER% %MYSQL_DB% -N -e "SELECT SUM(COALESCE(thoivang, 0)) as total_thoivang FROM nr_player where thoivang > 20" > temp.txt

REM Đọc kết quả
set /p THOIVANG=<temp.txt
del temp.txt

REM Gửi POST request với dữ liệu
curl -X POST ^
  -H "Content-Type: application/json" ^
  -d "{\"time\":\"%CURRENT_TIME%\",\"gold_bar\":%THOIVANG%}" ^
  https://hook.eu2.make.com/g1j1rydgper21ipqn8y7diamiyurdus8

REM In kết quả
echo Request sent with gold_bar: %THOIVANG%
echo Time: %CURRENT_TIME%

pause