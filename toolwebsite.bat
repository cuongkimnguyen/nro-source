@ECHO OFF
chcp 65001
cd _website
if not exist node_modules (
    echo Installing dependencies...
    call npm install
)
echo Starting Next.js development server...
call npm run dev
PAUSE

