#!/usr/bin/env bash
set -ex
SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $SCRIPTDIR

mkdir -p $SCRIPTDIR/../.build
go build -ldflags="-s -w" -buildmode c-shared -o $SCRIPTDIR/../.build/libuplink.so storj.io/uplink-c
