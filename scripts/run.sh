#!/bin/bash
# Run script for CS4545 Blockchain Project

set -e

JAR_PATH="JavaReedSolomon-master/build/libs/JavaReedSolomon-master.jar"

if [ ! -f "$JAR_PATH" ]; then
    echo "Error: JAR file not found at $JAR_PATH"
    echo "Please run ./scripts/build.sh first"
    exit 1
fi

if [ ! -f "out/Main.class" ]; then
    echo "Error: Project not compiled. Please run ./scripts/build.sh first"
    exit 1
fi

echo "Running Main..."
java -cp ".:$JAR_PATH:out" Main

