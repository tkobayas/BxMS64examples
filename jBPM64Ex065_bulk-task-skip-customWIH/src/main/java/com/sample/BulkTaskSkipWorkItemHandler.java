package com.sample;

import java.util.List;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkTaskSkipWorkItemHandler implements WorkItemHandler {

    private static Logger logger = LoggerFactory.getLogger(BulkTaskSkipWorkItemHandler.class);

    private static String BUSINESS_ADMINISTRATOR_PROP = "jbpm.bulk.skip.administrator";
    private static String DEFAULT_BUSINESS_ADMINISTRATOR = "Administrator";
    private String businessAdministrator;

    private TaskService readOnlyTaskService;

    public BulkTaskSkipWorkItemHandler(TaskService taskService) {
        this.readOnlyTaskService = taskService;
        this.businessAdministrator = System.getProperty(BUSINESS_ADMINISTRATOR_PROP, DEFAULT_BUSINESS_ADMINISTRATOR);
        logger.info("businessAdministrator = " + businessAdministrator);
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

        logger.info("enter executeWorkItem()");

        List<Long> taskIds = (List<Long>) workItem.getParameter("TaskIds");

        logger.info("taskIds = " + taskIds);

        for (Long taskId : taskIds) {
            Task task = readOnlyTaskService.getTaskById(taskId);
            String deploymentId = task.getTaskData().getDeploymentId();
            long processInstanceId = task.getTaskData().getProcessInstanceId();

            RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);

            // Need to use runtimeEngine/taskService per processInstanceId
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            TaskService taskService = runtimeEngine.getTaskService();
            taskService.skip(taskId, businessAdministrator);
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
