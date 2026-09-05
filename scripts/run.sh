#!/bin/bash
set -e
cd "$(dirname "$0")/.."
if [ ! -f build/classes/Main.class ]; then
    ./scripts/build.sh
fi
java -cp build/classes Main
