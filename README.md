# Running the App

In order to run Fonoster App you must lunch infrastructure that supports the system, including the database server,
telephony network, and other elements. First go to the **ops** folder an run docker as follows:

```bash
cd fonoster/ops
docker-compose up fnast fnmongodb
```

Once the system is up, you can run Jetty using the following command inside the **app's** folder

```

cd fonoster/app/webui
gradle farmRun 
```

> Then the console will be available at http://localhost:8080
