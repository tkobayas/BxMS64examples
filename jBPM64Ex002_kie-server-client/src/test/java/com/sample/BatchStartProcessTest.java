package com.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class BatchStartProcessTest extends TestCase {

    public static final String CONTAINER_ID = "org.kie.example:project1:1.0.0-SNAPSHOT";

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        RuleServicesClient ruleServicesClient = KieServerRestUtils.getRuleServicesClient("bpmsAdmin", "password1!");

        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(commandsFactory.newStartProcess("project1.helloProcess", params));
        commands.add(commandsFactory.newStartProcess("project1.helloProcess", params));
        commands.add(commandsFactory.newStartProcess("project1.helloProcess", params));
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands, CONTAINER_ID);

        ServiceResponse<ExecutionResults> results = ruleServicesClient.executeCommandsWithResults(CONTAINER_ID, executionCommand);

        System.out.println("batch startProcess() : results = " + results);

        UserTaskServicesClient taskClient = KieServerRestUtils.getUserTaskServicesClient("bpmsAdmin", "password1!");

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