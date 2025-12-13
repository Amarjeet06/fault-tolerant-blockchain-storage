@echo off
REM Build script for CS4545 Blockchain Project
REM This script builds the Reed-Solomon library and compiles the project

echo Building JavaReedSolomon library...
cd JavaReedSolomon-master
if exist gradlew.bat (
    call gradlew.bat build
) else (
    echo Error: gradlew.bat not found in JavaReedSolomon-master directory
    exit /b 1
)
cd ..

echo.
echo Compiling project...
if not exist out mkdir out

REM Check if JAR exists
set JAR_PATH=JavaReedSolomon-master\build\libs\JavaReedSolomon-master.jar
if not exist "%JAR_PATH%" (
    echo Error: JAR file not found at %JAR_PATH%
    echo Please ensure the Reed-Solomon library was built successfully
    exit /b 1
)

javac -cp ".;%JAR_PATH%" src\*.java -d out

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful!
    echo.
    echo To run the project:
    echo   java -cp ".;%JAR_PATH%;out" Main
) else (
    echo.
    echo Build failed!
    exit /b 1
)

