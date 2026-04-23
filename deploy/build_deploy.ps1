# ===================================
# SenYeHui SaaS - Local Build & Package
# Generates deploy/senyehui-deploy.tar.gz
# ===================================

$ErrorActionPreference = "Stop"
$ROOT = "d:\senyehui"
$DEPLOY_DIR = "$ROOT\deploy"
$STAGE = "$DEPLOY_DIR\_stage"

Write-Host ""
Write-Host "========== [1/4] Maven Build ==========" -ForegroundColor Cyan
Set-Location $ROOT
mvn clean install -DskipTests -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven build FAILED!" -ForegroundColor Red
    exit 1
}
Write-Host "Backend build OK" -ForegroundColor Green

Write-Host ""
Write-Host "========== [2/4] Build H5 ==========" -ForegroundColor Cyan
Set-Location "$ROOT\syh-h5"
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "H5 build FAILED!" -ForegroundColor Red
    exit 1
}
Write-Host "H5 build OK" -ForegroundColor Green

Write-Host ""
Write-Host "========== [3/4] Build Admin UI ==========" -ForegroundColor Cyan
Set-Location "$ROOT\syh-admin-ui"
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "Admin UI build FAILED!" -ForegroundColor Red
    exit 1
}
Write-Host "Admin UI build OK" -ForegroundColor Green

Write-Host ""
Write-Host "========== [4/4] Package Deploy Files ==========" -ForegroundColor Cyan

if (Test-Path $STAGE) { Remove-Item -Recurse -Force $STAGE }
New-Item -ItemType Directory -Force -Path $STAGE | Out-Null

Copy-Item "$ROOT\docker-compose.yml" "$STAGE\"
Copy-Item "$ROOT\Dockerfile" "$STAGE\"

New-Item -ItemType Directory -Force -Path "$STAGE\syh-api\target" | Out-Null
Copy-Item "$ROOT\syh-api\target\syh-api-1.0.0-SNAPSHOT.jar" "$STAGE\syh-api\target\"

New-Item -ItemType Directory -Force -Path "$STAGE\docs\database" | Out-Null
Copy-Item "$ROOT\docs\database\init.sql" "$STAGE\docs\database\"
Copy-Item "$ROOT\docs\database\pay_order.sql" "$STAGE\docs\database\"

New-Item -ItemType Directory -Force -Path "$STAGE\syh-h5" | Out-Null
Copy-Item -Recurse "$ROOT\syh-h5\dist" "$STAGE\syh-h5\dist"

New-Item -ItemType Directory -Force -Path "$STAGE\syh-admin-ui" | Out-Null
Copy-Item -Recurse "$ROOT\syh-admin-ui\dist" "$STAGE\syh-admin-ui\dist"

Copy-Item "$DEPLOY_DIR\server_setup.sh" "$STAGE\"
Copy-Item "$DEPLOY_DIR\nginx_baota.conf" "$STAGE\"

Set-Location $DEPLOY_DIR
tar -czf senyehui-deploy.tar.gz -C _stage .

Remove-Item -Recurse -Force $STAGE

$fileSize = [math]::Round(((Get-Item "$DEPLOY_DIR\senyehui-deploy.tar.gz").Length / 1MB), 2)
Write-Host ""
Write-Host "BUILD & PACKAGE COMPLETE!" -ForegroundColor Green
Write-Host "File: deploy\senyehui-deploy.tar.gz ($fileSize MB)" -ForegroundColor Yellow
Write-Host "Next: Upload this file to server /opt/senyehui/" -ForegroundColor Cyan
