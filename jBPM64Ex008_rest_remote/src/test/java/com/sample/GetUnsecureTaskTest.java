package com.sample;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;

public class GetUnsecureTaskTest extends TestCase {

    private static final String USER = "bpmsAdmin";
    

    public void testRest() throws Exception {

        URL deploymentUrl = new URL("http://localhost:8080/business-central/");

        // NOTE: You need to set <property name="org.kie.task.insecure" value="true"/> in server side as well
        RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder()
                .addUrl(deploymentUrl).addUserName("bpmsAdmin").addPassword("password1!").addDeploymentId("org.kie.example:project1:1.0.0-SNAPSHOT")
                .disableTaskSecurity()
                .build();

        TaskService taskService = engine.getTaskService();

        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("user4", "en-UK");
        
        for (TaskSummary taskSummary : list) {
            System.out.println("taskId = " + taskSummary.getId() + ", " + taskSummary.getPotentialOwners() );
        }

    }
}