package com.sample;

import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class KieServerUtils {

    private static final String BASE_URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String DEFAULT_USERNAME = "kieserver";
    private static final String DEFAULT_PASSWORD = "kieserver1!";


    public static ProcessServicesClient getProcessServiceClient() {
        return getProcessServiceClient(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static ProcessServicesClient getProcessServiceClient(String username, String password) {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, username, password);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        ProcessServicesClient proessServicesClient = client.getServicesClient(ProcessServicesClient.class);

        return proessServicesClient;
    }

    public static UserTaskServicesClient getUserTaskServiceClient() {
        return getUserTaskServiceClient(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static UserTaskServicesClient getUserTaskServiceClient(String username, String password) {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, username, password);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        UserTaskServicesClient userTaskServiceClient = client.getServicesClient(UserTaskServicesClient.class);

        return userTaskServiceClient;
    }
    
    public static QueryServicesClient getQueryServicesClient() {
        return getQueryServicesClient(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static QueryServicesClient getQueryServicesClient(String username, String password) {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, username, password);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        QueryServicesClient queryServiceClient = client.getServicesClient(QueryServicesClient.class);

        return queryServiceClient;
    }
    
    public static ProcessServicesClient getProcessServicesClient() {
        return getProcessServicesClient(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static ProcessServicesClient getProcessServicesClient(String username, String password) {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, username, password);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        ProcessServicesClient processServiceClient = client.getServicesClient(ProcessServicesClient.class);

        return processServiceClient;
    }
}
