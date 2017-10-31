package com.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

public class KieServerClientTest extends TestCase {

    private static final String BASE_URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String USERNAME = "kieserver";
    private static final String PASSWORD = "kieserver1!";

    private static final String CONTAINER_ID = "org.kie.example:project3:1.0.0-SNAPSHOT";

    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JAXB;

    @Test
    public void testProcess() {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, USERNAME, PASSWORD);
        //        config.setMarshallingFormat(FORMAT);
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Person.class);

        config.addExtraClasses(classes);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        RuleServicesClient ruleClient = client.getServicesClient(RuleServicesClient.class);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        KieCommands commandsFactory = KieServices.Factory.get().getCommands();

        Person john = new Person("John", 20);

        commands.add(commandsFactory.newInsert(john, "fact-1"));
        commands.add(commandsFactory.newFireAllRules("fire-result"));

        BatchExecutionCommand batchExecution = commandsFactory.newBatchExecution(commands);

        ServiceResponse<ExecutionResults> response = ruleClient.executeCommandsWithResults(CONTAINER_ID, batchExecution);

        System.out.println("-----------------------------------");

        System.out.println(response);
        
        ExecutionResults result = response.getResult();
        
        System.out.println(result);
    }

}