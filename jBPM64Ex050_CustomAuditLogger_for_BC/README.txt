## Custom AuditLogger example for Business Central

This example is to change from JPAWorkingMemoryDbLogger to a custom one.

1. Customize MyJPAWorkingMemoryDbLogger methods inherited from JPAWorkingMemoryDbLogger

2. Build this jar and place my-audit-logger-1.0.0.jar under business-central.war/WEB-INF/lib/

3. For kjar, disable audit-mode and add MyJPAWorkingMemoryDbLogger

  kie-deployment-descriptor.xml would be like this:
  
~~~
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<deployment-descriptor xsi:schemaLocation="http://www.jboss.org/jbpm deployment-descriptor.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    ...
    <audit-mode>NONE</audit-mode>
    ...
    <event-listeners>
        <event-listener>
            <resolver>mvel</resolver>
            <identifier>new com.sample.MyJPAWorkingMemoryDbLogger(ksession)</identifier>
            <parameters/>
        </event-listener>
    </event-listeners>
    ...
</deployment-descriptor>
~~~

Note: This is tested only with PER_REQUEST.