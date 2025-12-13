#!/bin/bash
# Build script for CS4545 Blockchain Project
# This script builds the Reed-Solomon library and compiles the project

set -e  # Exit on error

echo "Building JavaReedSolomon library..."
cd JavaReedSolomon-master
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew build
else
    echo "Error: gradlew not found in JavaReedSolomon-master directory"
    exit 1
fi
cd ..

echo ""
echo "Compiling project..."
mkdir -p out

# Check if JAR exists
JAR_PATH="JavaReedSolomon-master/build/libs/JavaReedSolomon-master.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: JAR file not found at $JAR_PATH"
    echo "Please ensure the Reed-Solomon library was built successfully"
    exit 1
fi

javac -cp ".:$JAR_PATH" src/*.java -d out

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo ""
    echo "To run the project:"
    echo "  java -cp ".:$JAR_PATH:out" Main"
else
    echo ""
    echo "Build failed!"
    exit 1
fi

