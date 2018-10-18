package com.sample;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessEventListener implements ProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MyProcessEventListener.class);

    public void afterNodeLeft(ProcessNodeLeftEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        if (null != event && event.getNodeInstance() instanceof HumanTaskNodeInstance) {
            afterHumanTaskNodeTriggered(event);
        }
    }

    private void afterHumanTaskNodeTriggered(ProcessNodeTriggeredEvent event) {
        logger.info("afterHumanTaskNodeTriggered : event = " + event);

        BeanManager bm = null;
        ExecutorService executorService = null;

        try {
            InitialContext context = new InitialContext();
            bm = (BeanManager) context.lookup("java:comp/BeanManager");

            Bean<DefinitionService> bean = (Bean<DefinitionService>) bm.getBeans(ExecutorService.class).iterator().next();
            CreationalContext<DefinitionService> ctx = bm.createCreationalContext(bean);

            executorService = (ExecutorService) bm.getReference(bean, ExecutorService.class, ctx);

        } catch (Exception e) {
            logger.error("error during getBeans", e);
        }

        if (executorService == null || !executorService.isActive()) {
            throw new IllegalStateException("Executor is not set or is not active");
        }
        
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("myData", "hello"); // you can pass data like this
        Long requestId = executorService.scheduleRequest(MyCommand.class.getCanonicalName(), ctxCMD);

        logger.info("MyCommand is scheduled : requestId = " + requestId);

    }

    public void afterProcessCompleted(ProcessCompletedEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterProcessStarted(ProcessStartedEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterVariableChanged(ProcessVariableChangedEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeNodeLeft(ProcessNodeLeftEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeProcessCompleted(ProcessCompletedEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeProcessStarted(ProcessStartedEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeVariableChanged(ProcessVariableChangedEvent arg0) {
        // TODO Auto-generated method stub

    }

}
