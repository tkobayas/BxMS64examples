package com.sample;

import java.util.List;

import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTaskLifeCycleEventListener implements TaskLifeCycleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MyTaskLifeCycleEventListener.class);
    
    private ThreadLocal<User> previousActualOwnerThreadLocal = new ThreadLocal<User>();

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
        User actualOwner = arg0.getTask().getTaskData().getActualOwner();
        logger.info("afterTaskDelegatedEvent : actualOwner = " + actualOwner);
        List<OrganizationalEntity> potentialOwners = arg0.getTask().getPeopleAssignments().getPotentialOwners();
        logger.info("afterTaskDelegatedEvent : potentialOwners = " + potentialOwners);
        
        User previousActualOwner = previousActualOwnerThreadLocal.get();
        if (previousActualOwner != null && potentialOwners.contains(previousActualOwner)) {
            logger.info("afterTaskDelegatedEvent : removing " + previousActualOwner + " from potentialOwners" );
            potentialOwners.remove(previousActualOwner);
        }
        
        previousActualOwnerThreadLocal.remove();
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

    }

    public void beforeTaskClaimedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskCompletedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskDelegatedEvent(TaskEvent arg0) {
        User actualOwner = arg0.getTask().getTaskData().getActualOwner();
        logger.info("beforeTaskDelegatedEvent : actualOwner = " + actualOwner);
        List<OrganizationalEntity> potentialOwners = arg0.getTask().getPeopleAssignments().getPotentialOwners();
        logger.info("beforeTaskDelegatedEvent : potentialOwners = " + potentialOwners);
        
        previousActualOwnerThreadLocal.set(actualOwner);
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
