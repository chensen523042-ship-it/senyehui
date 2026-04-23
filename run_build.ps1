Set-Location d:\senyehui
Write-Host "=== Phase 7: Maven Build ===" -ForegroundColor Cyan
mvn clean install -DskipTests -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "=== BUILD SUCCESS ===" -ForegroundColor Green
} else {
    Write-Host "=== BUILD FAILED ===" -ForegroundColor Red
}
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
