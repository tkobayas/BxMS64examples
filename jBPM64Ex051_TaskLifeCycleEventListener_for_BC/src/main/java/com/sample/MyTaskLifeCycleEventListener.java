package com.sample;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.jbpm.services.api.DefinitionService;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTaskLifeCycleEventListener implements TaskLifeCycleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MyTaskLifeCycleEventListener.class);

    public void afterTaskActivatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskAddedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskClaimedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskCompletedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskDelegatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskExitedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskFailedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskForwardedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskNominatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskReleasedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskResumedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskSkippedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskStartedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskStoppedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskSuspendedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskActivatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskAddedEvent(TaskEvent event) {
        logger.info("beforeTaskAddedEvent : event = " + event);

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
        ctxCMD.setData("myData", "hello");
        Long requestId = executorService.scheduleRequest(MyCommand.class.getCanonicalName(), ctxCMD);

        logger.info("MyCommand is scheduled : requestId = " + requestId);

    }

    public void beforeTaskClaimedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskCompletedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskDelegatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskExitedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskFailedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskForwardedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskNominatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskReleasedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskResumedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskSkippedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskStartedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskStoppedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskSuspendedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

}
