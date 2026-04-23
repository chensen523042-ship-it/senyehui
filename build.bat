@echo off
chcp 65001 >nul
D:\apache-maven-3.6.3\bin\mvn.cmd clean compile -DskipTests -B -pl syh-common -e 2>&1 > d:\senyehui\build_detail.txt
