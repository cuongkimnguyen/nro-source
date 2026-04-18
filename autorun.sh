#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
LOCKFILE="autorun_lock.txt"
PORT="${PORT_GAME:-14445}"

cleanup() {
  rm -f "$LOCKFILE"
}
trap cleanup EXIT INT TERM

if [[ -f "$LOCKFILE" ]]; then
  echo "Autorun is already running."
  exit 0
fi

echo $$ > "$LOCKFILE"

while [[ -f "$LOCKFILE" ]]; do
  if ! ss -ltn | awk '{print $4}' | grep -qE ":${PORT}$"; then
    echo "Port ${PORT} is not listening. Building and starting server..."
    if ./build.sh; then
      nohup ./run.sh >/dev/null 2>&1 &
      sleep 15
    else
      echo "Build failed. Retrying in 60 seconds..."
      sleep 60
    fi
  else
    echo "Port ${PORT} is listening."
  fi
  sleep 30
done
