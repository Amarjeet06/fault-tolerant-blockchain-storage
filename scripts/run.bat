@echo off
REM Run script for CS4545 Blockchain Project

set JAR_PATH=JavaReedSolomon-master\build\libs\JavaReedSolomon-master.jar

if not exist "%JAR_PATH%" (
    echo Error: JAR file not found at %JAR_PATH%
    echo Please run scripts\build.bat first
    exit /b 1
)

if not exist "out\Main.class" (
    echo Error: Project not compiled. Please run scripts\build.bat first
    exit /b 1
)

echo Running Main...
java -cp ".;%JAR_PATH%;out" Main

