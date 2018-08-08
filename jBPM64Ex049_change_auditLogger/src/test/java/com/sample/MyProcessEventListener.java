package com.sample;

import java.util.Collection;

import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.runtime.manager.impl.ManagedAuditEventBuilderImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

public class MyProcessEventListener implements ProcessEventListener {

    public void beforeProcessStarted(ProcessStartedEvent event) {
        System.out.println("beforeProcessStarted");
        
        System.out.println("ProcessId = " + event.getProcessInstance().getProcessId());
        
        KieRuntime kieRuntime = event.getKieRuntime();
        Collection<ProcessEventListener> processEventListeners = kieRuntime.getProcessEventListeners();
        ProcessEventListener toRemove = null;
        System.out.println(processEventListeners);
        for (ProcessEventListener processEventListener : processEventListeners) {
            if ((processEventListener instanceof JPAWorkingMemoryDbLogger) && !(processEventListener instanceof MyJPAWorkingMemoryDbLogger) ) {
                toRemove = processEventListener;
            }
        }
        if (toRemove != null) {
            kieRuntime.removeEventListener(toRemove);
            MyJPAWorkingMemoryDbLogger myJPAWorkingMemoryDbLogger = new MyJPAWorkingMemoryDbLogger(kieRuntime.getEnvironment());
            ManagedAuditEventBuilderImpl auditBuilder = new ManagedAuditEventBuilderImpl();
            InternalRuntimeManager runtimeManager = (InternalRuntimeManager) kieRuntime.getEnvironment().get("RuntimeManager");
            auditBuilder.setOwnerId(runtimeManager.getIdentifier());
            myJPAWorkingMemoryDbLogger.setBuilder(auditBuilder);
            kieRuntime.addEventListener(myJPAWorkingMemoryDbLogger);
        }
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // TODO Auto-generated method stub
        
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        // TODO Auto-generated method stub
        
    }

}
