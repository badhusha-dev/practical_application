@echo off
echo Compiling Spring Boot Application...

REM Create directories
if not exist "target\classes" mkdir target\classes
if not exist "target\lib" mkdir target\lib

REM Download dependencies manually (simplified approach)
echo Downloading dependencies...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-web/3.2.0/spring-boot-starter-web-3.2.0.jar' -OutFile 'target\lib\spring-boot-starter-web.jar'}"
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa/3.2.0/spring-boot-starter-data-jpa-3.2.0.jar' -OutFile 'target\lib\spring-boot-starter-data-jpa.jar'}"

echo Compilation complete!
echo.
echo To run the application, you need Maven installed.
echo Please install Maven from: https://maven.apache.org/download.cgi
echo Then run: mvn spring-boot:run
pause
