package com.sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;

public class GetUnsecureTaskTest2 extends TestCase {


    public void testRest() throws Exception {

        URL deploymentUrl = new URL("http://localhost:8080/business-central/");

        RuntimeEngine runtimeEngine = RemoteRuntimeEngineFactory.newRestBuilder()
                .addUrl(deploymentUrl).addUserName("MBAdmin").addPassword("password1!").addDeploymentId("org.kie.example:project1:1.0.0-SNAPSHOT")
                .disableTaskSecurity().build();

        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        List status = new ArrayList<Status>();
        status.add(Status.Ready);
        status.add(Status.Reserved);

//        List<TaskSummary> tasklist = taskService.getTasksAssignedAsPotentialOwnerByStatus("john", status, "en-UK");
        List<TaskSummary> tasklist = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");

        System.out.println("Task list=" + tasklist.size());

    }
}