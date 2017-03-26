

## Developer Mode

In order to run in "developer mode" yo must follow next two steps:

1. Go to Fonoster Operations folder (operations) and run the command:

```
docker-compose up
```

2. In the Fonoster Apps folder (fonoster-app) run Jetty by running the next command:

```
cd fonoster-app/webui
mvn jetty:run
```
