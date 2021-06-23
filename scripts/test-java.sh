#!/bin/bash
set -ueo pipefail
set +x

SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $SCRIPTDIR

VERSION=$(go list -m -f "{{.Version}}" storj.io/storj)

go install storj.io/storj/cmd/certificates@$VERSION
go install storj.io/storj/cmd/identity@$VERSION
go install storj.io/storj/cmd/satellite@$VERSION
go install storj.io/storj/cmd/storagenode@$VERSION
go install storj.io/storj/cmd/versioncontrol@$VERSION
go install storj.io/storj/cmd/multinode@$VERSION
go install storj.io/storj/cmd/storj-sim@$VERSION
go install storj.io/gateway@$(go list -m -f "{{.Version}}" storj.io/gateway)

# setup tmpdir for testfiles and cleanup
TMP=$(mktemp -d -t tmp.XXXXXXXXXX)
cleanup(){
      rm -rf "$TMP"
}
trap cleanup EXIT

mkdir -p $SCRIPTDIR/../.build
go build -ldflags="-s -w" -buildmode c-shared -o $SCRIPTDIR/../.build/libuplink.so storj.io/uplink-c

export STORJ_NETWORK_DIR=$TMP

STORJ_NETWORK_HOST4=${STORJ_NETWORK_HOST4:-127.0.0.1}

# setup the network
storj-sim -x --host $STORJ_NETWORK_HOST4 network setup

# run tests
storj-sim -x --host $STORJ_NETWORK_HOST4 network test bash "$SCRIPTDIR/test-libuplink.sh"
storj-sim -x --host $STORJ_NETWORK_HOST4 network destroy
