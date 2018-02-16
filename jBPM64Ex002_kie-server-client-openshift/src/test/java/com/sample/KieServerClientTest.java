package com.sample;

import static com.sample.Constants.BASE_URL;
import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import org.junit.Test;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class KieServerClientTest extends TestCase {

    private static final String USERNAME = "kieserver";
    private static final String PASSWORD = "kieserver1!";

    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JAXB;

    @Test
    public void testProcess() {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, USERNAME, PASSWORD);
        //        config.setMarshallingFormat(FORMAT);
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(MyPojo.class);
        config.addJaxbClasses(classes);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient();
        long processInstanceId = processClient.startProcess(CONTAINER_ID, "project1.helloProcess", params);

        System.out.println("startProcess() : processInstanceId = " + processInstanceId);

        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient(USERNAME, PASSWORD);

        List<org.kie.server.api.model.instance.TaskSummary> taskList;
        taskList = taskClient.findTasksAssignedAsPotentialOwner("bpmsAdmin", 0, 100);
        for (org.kie.server.api.model.instance.TaskSummary taskSummary : taskList) {
            System.out.println("taskSummary.getId() = " + taskSummary.getId());
            long taskId = taskSummary.getId();
            taskClient.startTask(CONTAINER_ID, taskId, "bpmsAdmin");
            taskClient.completeTask(CONTAINER_ID, taskId, "bpmsAdmin", null);
        }
    }


}