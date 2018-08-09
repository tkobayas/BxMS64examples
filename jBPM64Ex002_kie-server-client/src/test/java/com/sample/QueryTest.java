package com.sample;

import static com.sample.Constants.CONTAINER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class QueryTest extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        QueryServicesClient queryClient = KieServerRestUtils.getQueryServicesClient();
        List<NodeInstance> findActiveNodeInstances = queryClient.findActiveNodeInstances(1L, 0, 10);

        System.out.println("findActiveNodeInstances = " + findActiveNodeInstances);

    }
}