#!/usr/bin/env bash

if [[ $(id -u) -ne 0 ]] ; then echo "Please run as root or sudo" ; exit 1 ; fi

BASE_DOCKER_IMAGE=gcr.io/fonoster-app/fnapp_base:latest
PACK=''
SKIP_TEST=true

gcloud docker -- pull $BASE_DOCKER_IMAGE

timestamp() {
  date +"%Y%m%d%H%M"
}

confirm() {
    # call with a prompt string or use a default
    read -r -p "${1:-Is finall build? [y/N]} " response
    case "$response" in
        [yY][eE][sS]|[yY])
            true
            ;;
        *)
            false
            ;;
    esac
}

confirm && SKIP_TEST=false && PACK='-p'

$(cd webui/src/main/webapp/app && npm i --verbose && webpack --verbose $PACK)

gradle
gradle goJf
gradle build

docker build -t gcr.io/fonoster-app/fnapp:latest .
docker tag gcr.io/fonoster-app/fnapp:latest gcr.io/fonoster-app/fnapp:1.0.$(timestamp)
