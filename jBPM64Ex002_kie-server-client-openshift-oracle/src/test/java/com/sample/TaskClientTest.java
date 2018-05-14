package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.List;

import junit.framework.TestCase;
import org.junit.Test;
import org.kie.server.client.UserTaskServicesClient;

public class TaskClientTest extends TestCase {

    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JAXB;

    @Test
    public void testProcess() {

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