#!/usr/bin/env bash

timestamp() {
  date +"%Y%m%d%H%M"
}

#
cd webui/src/main/webapp/app && npm i && webpack -p

# Build without assembly first
mvn package -Dmaven.test.skip=true

# Now assemble
mvn package -Dmaven.test.skip=true -P assemble

docker build -t gcr.io/fonoster-app/fnapp:latest .
docker tag gcr.io/fonoster-app/fnapp:latest gcr.io/fonoster-app/fnapp:1.0.$(timestamp)