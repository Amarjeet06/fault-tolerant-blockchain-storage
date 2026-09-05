#!/bin/bash
set -e
cd "$(dirname "$0")/.."
./scripts/build.sh
echo ""
echo "Running test suite..."
java -cp build/classes RunAllTests
