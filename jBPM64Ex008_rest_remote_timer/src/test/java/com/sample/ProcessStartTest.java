package com.sample;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;

public class ProcessStartTest extends TestCase {
    public void testRest() throws Exception {

        URL deploymentUrl = new URL("http://localhost:8080/business-central/");

        RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder().addUrl(deploymentUrl).addUserName("bpmsAdmin").addPassword("password1!")
                .addDeploymentId("org.kie.example:project1:1.0.0-SNAPSHOT").build();

        for (int i = 0; i < 1; i++) {

            KieSession ksession = engine.getKieSession();
            TaskService taskService = engine.getTaskService();
            AuditService auditService = engine.getAuditService();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("var1", "xxx");

            ProcessInstance processInstance = ksession.startProcess("project1.helloTimer", params);

        }
    }
}