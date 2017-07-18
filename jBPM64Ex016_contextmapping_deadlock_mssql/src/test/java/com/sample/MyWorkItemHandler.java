package com.sample;

import org.jbpm.runtime.manager.impl.PerProcessInstanceRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWorkItemHandler implements WorkItemHandler {
    
    private static Logger logger = LoggerFactory.getLogger( MyWorkItemHandler.class );

    private KieSession ksession;

    public MyWorkItemHandler() {
    }

    public MyWorkItemHandler(KieSession ksession) {
        this.ksession = ksession;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        
        logger.info( "enter executeWorkItem()" );

        long processInstanceId = workItem.getProcessInstanceId();

        logger.info( "processInstanceId = " + processInstanceId );
        
        try {
            Thread.sleep( 1000 );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        
        long otherProcessInstanceId = -1;
        if (processInstanceId == 1) {
            otherProcessInstanceId = 2;
        } else {
            otherProcessInstanceId = 1;
        }
        
        

//        RuntimeManager runtimeManager = (RuntimeManager)ksession.getEnvironment().get("RuntimeManager");
//        Mapper mapper = ((PerProcessInstanceRuntimeManager)runtimeManager).getMapper();
//        Long findMapping = mapper.findMapping( ProcessInstanceIdContext.get(otherProcessInstanceId), runtimeManager.getIdentifier() );
//        
//        logger.info("findMapping = " + findMapping);
//        
//        try {
//            Thread.sleep( 1000 );
//        } catch ( InterruptedException e ) {
//            e.printStackTrace();
//        }
        
//        
//        RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(otherProcessInstanceId));
//        KieSession currentKsession = runtimeEngine.getKieSession();
//
//        logger.info("getRuntimeEngine() runtimeEngine = " + runtimeEngine);
 

        manager.completeWorkItem(workItem.getId(), null);

        return;

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

}

