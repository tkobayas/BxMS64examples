package com.sample;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessEventListener implements ProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MyProcessEventListener.class);

    public void beforeProcessStarted(ProcessStartedEvent event) {
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("beforeNodeTriggered : " + event + ", processInstanceId = " + event.getProcessInstance().getId());
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("afterNodeTriggered : " + event + ", processInstanceId = " + event.getProcessInstance().getId());
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        System.out.println("beforeNodeLeft : " + event + ", processInstanceId = " + event.getProcessInstance().getId());
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        System.out.println("afterNodeLeft : " + event + ", processInstanceId = " + event.getProcessInstance().getId());
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
    }

}
