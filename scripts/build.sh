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
docker-compose build
docker-compose up -d

storj-up health -d 60

eval $(storj-up credentials -e)
mvn clean install

