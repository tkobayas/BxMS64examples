package com.sample;

import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.runtime.Environment;


public class MyJPAWorkingMemoryDbLogger extends JPAWorkingMemoryDbLogger {

    public MyJPAWorkingMemoryDbLogger(Environment environment) {
        super(environment);
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("AHHH");
    }
}
