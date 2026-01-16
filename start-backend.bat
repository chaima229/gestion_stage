@echo off
echo ==========================================
echo    Demarrage du serveur Spring Boot
echo    Port: 8082
echo ==========================================
cd /d "%~dp0"
call mvnw.cmd spring-boot:run
pause
