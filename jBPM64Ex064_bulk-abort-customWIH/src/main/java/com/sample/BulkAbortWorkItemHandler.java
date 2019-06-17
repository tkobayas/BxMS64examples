package com.sample;

import java.util.List;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkAbortWorkItemHandler implements WorkItemHandler {

    private static Logger logger = LoggerFactory.getLogger(BulkAbortWorkItemHandler.class);

    private KieSession ksession;

    public BulkAbortWorkItemHandler(KieSession ksession) {
        this.ksession = ksession;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

        logger.info("enter executeWorkItem()");

        List<Long> processInstanceIds = (List<Long>) workItem.getParameter("ProcessInstanceIds");

        logger.info("processInstanceIds = " + processInstanceIds);

        // this.ksession is only used for LogService.
        JPAAuditLogService auditLogService = new JPAAuditLogService(this.ksession.getEnvironment());

        for (Long processInstanceId : processInstanceIds) {
            ProcessInstanceLog processInstanceLog = auditLogService.findProcessInstance(processInstanceId);
            logger.info("processInstanceLog = " + processInstanceLog);
            String deploymentId = processInstanceLog.getExternalId();
            RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);

            // Need to use runtimeEngine/kieSession per processInstanceId
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            KieSession kieSession = runtimeEngine.getKieSession();
            kieSession.abortProcessInstance(processInstanceId);
        }

        workItemManager.completeWorkItem(workItem.getId(), null);

        logger.info("exit executeWorkItem()");

        return;

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

}
