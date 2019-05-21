package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.client.ProcessServicesClient;

public class StartProcessTest extends TestCase {

    public void testRest() throws Exception {

        ArrayList<Long> processInstanceIds = new ArrayList<Long>();
        processInstanceIds.add(1L);
        processInstanceIds.add(2L);
        processInstanceIds.add(3L);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceIds", processInstanceIds);
        
        ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient();
        long processInstanceId = processClient.startProcess(CONTAINER_ID, "tool-process.abort-tool", params);

        System.out.println("startProcess() : processInstanceId = " + processInstanceId);



    }
}