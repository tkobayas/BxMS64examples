package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceWithVarsQueryMapper;
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;
import org.jbpm.services.api.query.QueryResultMapper;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class AdvancedQueryTest2 extends TestCase {

    public void testRest() throws Exception {

        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();

        QueryFilterSpec filterSpec = new QueryFilterSpecBuilder()
                .greaterThan(QueryResultMapper.COLUMN_TASK_PROCESSINSTANCEID, "2").get();

//        List<UserTaskInstanceWithVarsDesc> taskInstanceLogs = 
//                queryClient.query("getAllTaskInstancesWithVariables"
//                        , UserTaskInstanceWithVarsQueryMapper.get().getName()
//                        , filterSpec
//                        , new Integer(0)
//                        , new Integer(10)
//                        , UserTaskInstanceWithVarsDesc.class);
//        
//        UserTaskInstanceWithVarsDesc userTaskInstanceWithVarsDesc = taskInstanceLogs.get(0);
//        
//        System.out.println(userTaskInstanceWithVarsDesc.getVariables());
        
        // UserTaskInstanceWithVarsDesc is converted into TaskInstance at server side
        // variables can be retrieved by taskInstance.getInputData()
        List<TaskInstance> taskInstanceLogs = 
                queryClient.query("getAllTaskInstancesWithVariables"
                        , UserTaskInstanceWithVarsQueryMapper.get().getName()
                        , filterSpec
                        , new Integer(0)
                        , new Integer(10)
                        , TaskInstance.class);
        
        TaskInstance taskInstance = taskInstanceLogs.get(0);
        
        System.out.println(taskInstance.getInputData());

    }
}