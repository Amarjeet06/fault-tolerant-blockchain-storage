#!/bin/bash
# Build script: compiles the vendored JavaReedSolomon sources directly with
# javac (no Gradle/network access required) and then the main project.
set -e
cd "$(dirname "$0")/.."

mkdir -p build/classes

echo "Compiling vendored JavaReedSolomon library..."
find JavaReedSolomon-master/src/main/java -name "*.java" > /tmp/rs_sources_$$.txt
javac -d build/classes @/tmp/rs_sources_$$.txt
rm -f /tmp/rs_sources_$$.txt

echo "Compiling project sources..."
javac -cp build/classes -d build/classes src/*.java

echo "Compiling tests..."
javac -cp build/classes -d build/classes test/*.java

echo "Build successful. Run ./scripts/run_tests.sh or ./scripts/run.sh next."
