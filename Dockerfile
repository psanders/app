FROM gcr.io/fonoster-app/fnapp_base:latest
MAINTAINER Pedro Sanders <fonosterteam@fonoster.com>

ADD target/build-files/app-base/webapps /opt/app-base/webapps
ADD target/build-files/astive/apps /opt/astive/apps
