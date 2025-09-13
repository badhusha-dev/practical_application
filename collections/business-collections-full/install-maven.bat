@echo off
echo Installing Maven for Windows...

REM Create Maven directory
if not exist "C:\Program Files\Apache\maven" mkdir "C:\Program Files\Apache\maven"

REM Download Maven
echo Downloading Maven...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip'}"

REM Extract Maven
echo Extracting Maven...
powershell -Command "& {Expand-Archive -Path 'maven.zip' -DestinationPath 'C:\Program Files\Apache\' -Force}"

REM Add to PATH
echo Adding Maven to PATH...
setx PATH "%PATH%;C:\Program Files\Apache\apache-maven-3.9.6\bin" /M

REM Clean up
del maven.zip

echo Maven installation complete!
echo Please restart your terminal and run: mvn spring-boot:run
pause
