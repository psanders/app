FROM gcr.io/fonoster-app/fnapp_base:latest
MAINTAINER Pedro Sanders <fonosterteam@fonoster.com>

ADD resources/src/main/resources/META-INF/resources/ /opt/app-base/webapps/static
ADD rest/src/main/resources/rest.xml                 /opt/app-base/webapps
ADD webui/src/main/resources/web.xml                 /opt/app-base/webapps
ADD webui/src/main/resources/webui.xml               /opt/app-base/webapps
ADD rest/build/libs/rest.war                         /opt/app-base/webapps
ADD webui/build/libs/webui.war                       /opt/app-base/webapps
ADD voice/build/libs/voice.jar                       /opt/astive/apps
