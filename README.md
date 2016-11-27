Fonoster CI/CD
==============

                            
Integration Tests
-----------------

Run `verify` phase of maven with 'ti' profile to run integration-test an generate docker images for fonoster app, 
mongodb and asterisk.

`mvn clean verify -Pit `

In order to run in "developer mode" yo must run start the services and then run jetty. To do so, go to root of the project
and run this:

```
mvn docker:start -Pit
cd webui
mvn jetty:run
```

You can now make changes in webui and see those changes at http://localhost:8080

All of the containers are loosely couple. They connect to each other via exposed services. The following environment 
variables can be use to replace the defaults (at /etc/fonoster.conf):

- ASTIVED_HOST
- ASTIVED_PORT
- MANAGER_HOST
- MANAGER_PORT
- DB_HOST
- DB_PORT

### Requirements

* Java 1.8+
* Docker 1.10+

### Authors

* Pedro Sanders
* Eudris Cabrera
