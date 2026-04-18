@ECHO OFF
chcp 65001
:: Thiết lập thư mục và tên file log
set LOG_DIR=logs
set ERROR_LOG=%LOG_DIR%\oom_error.log
set HEAP_DUMP=%LOG_DIR%\heapdump_%date:~-4,4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%%time:~6,2%.hprof
set HEAP_DUMP=%HEAP_DUMP: =0%
:: Đảm bảo thư mục logs tồn tại
if not exist %LOG_DIR% mkdir %LOG_DIR%
:: Thiết lập sử dụng tối đa 24GB RAM
set MEMORY_OPTS=-Xms1G -Xmx24G -XX:+UseG1GC
:: Thiết lập ghi log GC và xử lý OutOfMemoryError với cú pháp tương thích Java 9+
set DEBUG_OPTS=-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%HEAP_DUMP% -Xlog:gc*:file=%LOG_DIR%\gc.log:time,uptime,level,tags
set ERROR_OPTS=-XX:OnOutOfMemoryError="echo ===== OutOfMemoryError occurred at %%date%% %%time%% ===== >> %ERROR_LOG% && jcmd %%p GC.heap_info >> %ERROR_LOG% && jcmd %%p Thread.print >> %ERROR_LOG%"
echo Starting application with 24GB maximum memory...
echo Logs will be saved to %LOG_DIR% directory
java -server %MEMORY_OPTS% %DEBUG_OPTS% %ERROR_OPTS% ^
-Dfile.encoding=UTF-8 ^
-Dfile.client.encoding=UTF-8 ^
-Dconsole.encoding=UTF-8 ^
-jar target/ngocrongonline-0.0.1-SNAPSHOT.jar 2>> %LOG_DIR%\error.log
echo Application stopped, check logs for details.
PAUSE