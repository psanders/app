# Running the App

In order to run Fonoster App you must launch infrastructure that supports the system, including the database server,
telephony network, and other elements. 

Assuming that both `ops` and `app` are in the same folder:

```bash
cd ops
docker-compose up &
cd ..
cd app/webui
gradle farmRun 
```

> Then the web console will be available at http://localhost:8082
> Astive Toolkit and JsonPath must be installed manually in maven local :(