@echo off
chcp 65001 >nul
cd /d d:\senyehui
call mvn compile -DskipTests -pl syh-wechat -am > d:\senyehui\build_output.txt 2>&1
echo EXIT_CODE=%ERRORLEVEL% >> d:\senyehui\build_output.txt
