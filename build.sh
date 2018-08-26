#!/usr/bin/env bash

#set -e

PACK=''

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

WORKDIR=$PWD
cd webui/src/main/webapp/app && npm update && webpack $PACK
cd $WORKDIR
gradle clean goJF jar war shadowJar
