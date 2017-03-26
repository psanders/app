## Running Fonoster App

In order to run Fonoster App you must lunch infrastructure that supports the system.
That is: the Telephony System(Sip I/O, Asterisk, Astive Server), Mongo Database etc.

To lunch the infrastructre go to Fonoster Operations (operations) and run the command `docker-compose up`.
Once the system is up, you can run Jetty using the following command line instructions at Fonoster Apps folder(fonoster-app):

```
cd fonoster-app/webui
mvn jetty:run
```
