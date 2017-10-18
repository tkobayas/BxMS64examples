package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import junit.framework.TestCase;

public class StartProcessTest extends TestCase {

    public static final String CONTAINER_ID = "org.kie.example:project1:1.0.0-SNAPSHOT";

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient("kieserver", "kieserver1!");
        long processInstanceId = processClient.startProcess(CONTAINER_ID, "project1.helloProcess", params);

        System.out.println("startProcess() : processInstanceId = " + processInstanceId);

        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient("MyAdmin", "password1!");

        List<org.kie.server.api.model.instance.TaskSummary> taskList;
        taskList = taskClient.findTasksAssignedAsBusinessAdministrator("MyAdmin", 0, 100);
        for (org.kie.server.api.model.instance.TaskSummary taskSummary : taskList) {
            System.out.println("taskSummary.getId() = " + taskSummary.getId());
            long taskId = taskSummary.getId();
            taskClient.startTask(CONTAINER_ID, taskId, "MyAdmin");
            taskClient.completeTask(CONTAINER_ID, taskId, "MyAdmin", null);
        }

    }
}