#!/bin/bash
set -ueo pipefail

SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$SCRIPTDIR"/..

export LD_LIBRARY_PATH=$SCRIPTDIR/../.build/
./gradlew clean test
