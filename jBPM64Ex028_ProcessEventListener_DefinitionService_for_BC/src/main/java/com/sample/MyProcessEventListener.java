package com.sample;

import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.jbpm.services.api.DefinitionService;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.manager.RuntimeManager;

public class MyProcessEventListener implements ProcessEventListener {

    public void beforeProcessStarted(ProcessStartedEvent event) {
        // TODO Auto-generated method stub
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        // TODO Auto-generated method stub
        System.out.println("afterProcessStarted() : event = " + event);
        
        BeanManager bm = null;
        DefinitionService definitionService = null;

        try {
            InitialContext context = new InitialContext();
            bm = (BeanManager) context.lookup("java:comp/BeanManager");
            
            Bean<DefinitionService> bean = (Bean<DefinitionService>) bm.getBeans(DefinitionService.class).iterator().next();
            CreationalContext<DefinitionService> ctx = bm.createCreationalContext(bean);

            definitionService = (DefinitionService) bm.getReference(bean, DefinitionService.class, ctx);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String identifier = ((RuntimeManager)event.getKieRuntime().getEnvironment().get("RuntimeManager")).getIdentifier();
        String processId = event.getProcessInstance().getProcessId();
        
        System.out.println("identifier = " + identifier + ", processId = " + processId);

        Map<String, String>  vars = definitionService.getProcessVariables(identifier, processId);
        System.out.println(vars);
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
