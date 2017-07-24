package com.sample;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class MyWorkItemHandler implements WorkItemHandler {

    @Override
    public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
            
            throw new MyException();
            
    }
    
 
}
