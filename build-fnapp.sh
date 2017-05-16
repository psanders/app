#!/usr/bin/env bash

#if [[ $(id -u) -ne 0 ]] ; then echo "Please run as root or sudo" ; exit 1 ; fi

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

confirm && PACK='-p' && echo 'This might take a while. Go grab some coffee  :)'

$(cd webui/src/main/webapp/app && npm update && webpack $PACK)

gradle clean
gradle goJF
gradle jar war

docker build -t gcr.io/fonoster-app/fnapp:latest .
docker tag gcr.io/fonoster-app/fnapp:latest gcr.io/fonoster-app/fnapp:1.0.$(timestamp)
