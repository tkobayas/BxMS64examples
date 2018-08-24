package com.sample;

import java.util.List;

import org.jbpm.kie.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

public class MyJPAWorkingMemoryDbLogger extends JPAWorkingMemoryDbLogger {

    private boolean initialized = false;

    public MyJPAWorkingMemoryDbLogger(KieSession ksession) {
        super(ksession.getEnvironment()); // super(ksession) will cause duplicate registering
    }

    private void initialize(KieRuntime kieRuntime) {
        System.out.println("  initializing...");
        ServicesAwareAuditEventBuilder auditBuilder = new ServicesAwareAuditEventBuilder();
        InternalRuntimeManager runtimeManager = (InternalRuntimeManager) kieRuntime.getEnvironment().get("RuntimeManager");
        auditBuilder.setDeploymentUnitId(runtimeManager.getIdentifier());
        IdentityProvider identityProvider = (IdentityProvider) kieRuntime.getEnvironment().get("IdentityProvider");
        auditBuilder.setIdentityProvider(identityProvider);
        this.setBuilder(auditBuilder);
        initialized = true;
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {
        System.out.println("beforeProcessStarted : " + event);

        synchronized (this) {
            if (!initialized) {
                initialize(event.getKieRuntime());
            }
        }

        super.beforeProcessStarted(event);
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("beforeNodeTriggered : " + event);

        if (event.getProcessInstance().getProcessId().equals("project1.bp1")) {
            // I don't log NodeInstanceLog for this process
            return;
        }

        super.beforeNodeTriggered(event);
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        System.out.println("afterVariableChanged : " + event);

        // This may be called earlier than beforeProcessStarted()
        synchronized (this) {
            if (!initialized) {
                initialize(event.getKieRuntime());
            }
        }

        super.afterVariableChanged(event);
    }
}
