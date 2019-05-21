## abort-tool-example

This example contains the following directories:

- bulk-abort-customWIH : Maven project for a custom WorkItemHandler (BulkAbortWorkItemHandler) which aborts process instances in one transaction

- tool-process-repo : Git repository for business-central, which contains 2 projects

  - tool-process : Project which contains "abort-tool" process, which has "BulkAbort" Task so the BulkAbortWorkItemHandler is triggered.

  - test-process : Test project to confirm the usage. It contains a simple "test-process" with HumanTask.

- test-process-kie-server-client : kie-server-client project which you can start a "test-process"

- tool-process-kie-server-client : kie-server-client project which you can start a "tool-process" in order to abort specified process instances.

====

The test steps are:

- cd bulk-abort-customWIH

- mvn clean install

- Copy target/target/bulk-abort-workitemhandler-1.0.0-SNAPSHOT.jar to business-central.war/WEB-INF/lib and kie-server.war/WEB-INF/lib

- Start BPM Suite

- Clone tool-process-repo via business-central

- Build&Deploy "tool-process" and "test-process"

- Deploy "tool-process" and "test-process" to kie-server

- Import "test-process-kie-server-client" to JBDS/Eclipse.
  - Run StartProcessTest multiple times. Confirm the process instance IDs which are generated.

- Import "tool-process-kie-server-client" to JBDS/Eclipse
  - Edit StartProcessTest.java to specify the process instance IDs to abort

~~~
        ArrayList<Long> processInstanceIds = new ArrayList<Long>();
        processInstanceIds.add(1L);
        processInstanceIds.add(2L);
        processInstanceIds.add(3L);
~~~

  - Run StartProcessTest. Confirm the log and database.

~~~
16:16:54,392 INFO  [com.sample.BulkAbortWorkItemHandler] (http-127.0.0.1:8080-7) enter executeWorkItem()
16:16:54,392 INFO  [com.sample.BulkAbortWorkItemHandler] (http-127.0.0.1:8080-7) processInstanceIds = [1, 2, 3]
16:16:54,399 INFO  [com.sample.BulkAbortWorkItemHandler] (http-127.0.0.1:8080-7) processInstanceLog = Process 'test-process.test-process' [1]
16:16:54,635 INFO  [com.sample.BulkAbortWorkItemHandler] (http-127.0.0.1:8080-7) processInstanceLog = Process 'test-process.test-process' [2]
16:16:54,710 INFO  [com.sample.BulkAbortWorkItemHandler] (http-127.0.0.1:8080-7) processInstanceLog = Process 'test-process.test-process' [3]
16:16:54,792 INFO  [com.sample.BulkAbortWorkItemHandler] (http-127.0.0.1:8080-7) exit executeWorkItem()
~~~
