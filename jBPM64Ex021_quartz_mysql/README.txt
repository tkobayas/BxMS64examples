Quartz scheduler example which demonstrates Timer surviving JVM reboot.

1. Make sure that jbpm-db.h2.db is deleted (e.g. ~/jbpm-db.h2.db)
2. mvn clean test -Dtest=ProcessMainJPATest
3. mvn test -Dtest=ProcessMainJPAContinueTest

Note: this example uses org.jbpm.test.timer.quartz.NonTransactionalConnectionProvider for notManagedDS. You may need to acquire a non-managed datasource from an application server for real use cases.