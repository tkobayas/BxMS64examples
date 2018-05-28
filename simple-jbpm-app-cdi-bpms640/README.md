simple-jbpm-app-cdi
=============

This is a simple example web application for jBPM 6, which is a fork of 'rewards-basic' example app.

This simple example aims to provide an example usage of:
- CDI jbpm-services
- Human Tasks
- Persistence
- Transactions
- PerProcessInstance RuntimeManager
- Maven build

### Steps to run
- Make sure you have at least Java 7 and Maven 3 installed
- Download somewhere JBoss EAP 6.4
- Start the application server (default datasource is ExampleDS, the same as in EAP, so the example works out of the box):
 - cd jboss-eap-6.4/bin
 - ./standalone.sh
- Build and deploy the example application:
 - cd simple-jbpm-app-cdi-bpmsXXX
 - mvn clean package
 - copy to JBoss EAP
- Visit http://localhost:8080/simple-jbpm-app/ with a web browser
 - [Start Reward Process] is to start a new process
 - [John's Task] is to list John's tasks and approve them
 - [Mary's Task] is to list Mary's tasks and approve them
