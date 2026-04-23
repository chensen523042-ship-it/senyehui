@echo off
cd /d d:\senyehui
echo [Phase 7] Starting Maven build...
call mvn clean install -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [FAILED] Build failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)
echo [SUCCESS] Build completed successfully!
