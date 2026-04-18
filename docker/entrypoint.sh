#!/usr/bin/env sh
set -eu

mkdir -p /app/logs /app/log /app/backup /app/runtime-data

JAVA_OPTS="${JAVA_OPTS:-} -server -Xms${XMS:-1G} -Xmx${XMX:-4G} -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs -Dfile.encoding=UTF-8"

# Java 11 GC logging syntax
JAVA_OPTS="$JAVA_OPTS -Xlog:gc*:file=/app/logs/gc.log:time,uptime,level,tags"

exec sh -c "java $JAVA_OPTS -jar /app/server.jar" >> /app/logs/server.out 2>> /app/logs/error.log
