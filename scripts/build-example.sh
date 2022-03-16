#!/usr/bin/env bash
set -ex
SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$SCRIPTDIR/.."

if [ ! -d ".build" ]; then
  ./scripts/build-uplink.sh
fi

if ! command -v storj-up &> /dev/null
then
  go install storj.io/storj-up@latest
fi

trap "docker-compose down -v" EXIT
docker-compose down -v
docker-compose up -d

#TODO: use smarter way to check if it's up and running
sleep 30

eval $(storj-up credentials -e)
cd "$SCRIPTDIR/../example"
mvn compile exec:java -Dexec.mainClass=io.storj.uplink.example.Example
