package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.junit.Test;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class KieServerClientTest extends TestCase {

    private static final String USERNAME = "kieserver";
    private static final String PASSWORD = "kieserver1!";

    @Test
    public void testProcess() {

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient(USERNAME, PASSWORD);
        long processInstanceId = processClient.startProcess(CONTAINER_ID, "project1.helloProcess", params);

        System.out.println("startProcess() : processInstanceId = " + processInstanceId);

//        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient(USERNAME, PASSWORD);
        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient("john", "password1!");

        List<org.kie.server.api.model.instance.TaskSummary> taskList;
        taskList = taskClient.findTasksAssignedAsPotentialOwner("john", 0, 100);
        for (org.kie.server.api.model.instance.TaskSummary taskSummary : taskList) {
            System.out.println("taskSummary.getId() = " + taskSummary.getId());
            long taskId = taskSummary.getId();
            taskClient.startTask(CONTAINER_ID, taskId, "john");
            taskClient.completeTask(CONTAINER_ID, taskId, "john", null);
        }
    }


}