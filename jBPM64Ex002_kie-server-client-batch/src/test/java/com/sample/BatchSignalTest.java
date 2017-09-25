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
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class BatchSignalTest extends TestCase {

    public static final String CONTAINER_ID = "org.kie.example:project1:1.0.0-SNAPSHOT";

    public void testRest() throws Exception {

        // This example is not recommended because:
        // - jBPM kie-server extension doesn't support batch command
        // - RuleServicesClient is not aware of RuntimeManager (for example, it will fail with PerProcessInstance)
        
        Map<String, Object> params = new HashMap<String, Object>();
        RuleServicesClient ruleServicesClient = KieServerRestUtils.getRuleServicesClient("bpmsAdmin", "password1!");

        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        
        {
            List<Command<?>> commands = new ArrayList<Command<?>>();
            commands.add(commandsFactory.newStartProcess("project1.helloSignal", params));
            commands.add(commandsFactory.newStartProcess("project1.helloSignal", params));
            commands.add(commandsFactory.newStartProcess("project1.helloSignal", params));
            BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands, CONTAINER_ID);
    
            ServiceResponse<ExecutionResults> results = ruleServicesClient.executeCommandsWithResults(CONTAINER_ID, executionCommand);
    
            System.out.println("batch startProcess() : results = " + results);
        }

        QueryServicesClient queryServicesClient = KieServerRestUtils.getQueryServicesClient();
        List<Integer> status = new ArrayList<Integer>();
        status.add(ProcessInstance.STATE_ACTIVE);
        List<org.kie.server.api.model.instance.ProcessInstance> findProcessInstancesByProcessId = queryServicesClient.findProcessInstancesByProcessId("project1.helloSignal", status, 0, 100);
        
        {
            List<Command<?>> commands = new ArrayList<Command<?>>();
            for (org.kie.server.api.model.instance.ProcessInstance pi : findProcessInstancesByProcessId) {
                commands.add(commandsFactory.newSignalEvent(pi.getId(), "MySignal", null));
            }
            BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands, CONTAINER_ID);
    
            ServiceResponse<ExecutionResults> results = ruleServicesClient.executeCommandsWithResults(CONTAINER_ID, executionCommand);
    
            System.out.println("batch SignalEvent : results = " + results);
        }
    }
}