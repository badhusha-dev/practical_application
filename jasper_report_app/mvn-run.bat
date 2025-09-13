@echo off
REM Maven Wrapper for Windows
REM This script downloads Maven if not available and runs the Spring Boot application

echo Checking for Maven...

REM Try to find Maven in common locations
set MAVEN_HOME=
if exist "C:\Program Files\Apache\maven" set MAVEN_HOME=C:\Program Files\Apache\maven
if exist "C:\apache-maven" set MAVEN_HOME=C:\apache-maven
if exist "%USERPROFILE%\apache-maven" set MAVEN_HOME=%USERPROFILE%\apache-maven

if "%MAVEN_HOME%"=="" (
    echo Maven not found. Please install Maven or use an IDE to run the application.
    echo.
    echo To install Maven:
    echo 1. Download from https://maven.apache.org/download.cgi
    echo 2. Extract to C:\apache-maven
    echo 3. Add C:\apache-maven\bin to your PATH
    echo.
    echo Alternatively, open this project in IntelliJ IDEA or Eclipse.
    pause
    exit /b 1
)

echo Found Maven at: %MAVEN_HOME%
set PATH=%MAVEN_HOME%\bin;%PATH%

echo Running Spring Boot application...
mvn spring-boot:run

pause
