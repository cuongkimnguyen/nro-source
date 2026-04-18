#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-hunr_2026}"
BACKUP_PATH="${BACKUP_PATH:-./backup_sql}"
mkdir -p "$BACKUP_PATH"

TS="$(date +%Y%m%d_%H%M%S)"
OUT="$BACKUP_PATH/${DB_NAME}_${TS}.sql"

echo "Backing up database $DB_NAME to $OUT"
mysqldump \
  --host="$DB_HOST" \
  --port="$DB_PORT" \
  --user="$DB_USER" \
  --password="$DB_PASSWORD" \
  --default-character-set=utf8mb4 \
  --skip-lock-tables \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --databases "$DB_NAME" > "$OUT"

echo "Backup completed: $OUT"
