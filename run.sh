#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p logs log

export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk}"
export PATH="$JAVA_HOME/bin:$PATH"

JAR="target/ngocrongonline-0.0.1-SNAPSHOT.jar"
if [[ ! -f "$JAR" ]]; then
  echo "Jar not found: $JAR"
  echo "Run ./build.sh first"
  exit 1
fi

XMS="${XMS:-1G}"
XMX="${XMX:-4G}"
JAVA_OPTS=(
  "-server"
  "-Xms${XMS}"
  "-Xmx${XMX}"
  "-XX:+UseG1GC"
  "-XX:+HeapDumpOnOutOfMemoryError"
  "-XX:HeapDumpPath=logs"
  "-Xlog:gc*:file=logs/gc.log:time,uptime,level,tags"
  "-Dfile.encoding=UTF-8"
)

exec java "${JAVA_OPTS[@]}" -jar "$JAR" >> logs/server.out 2>> logs/error.log
