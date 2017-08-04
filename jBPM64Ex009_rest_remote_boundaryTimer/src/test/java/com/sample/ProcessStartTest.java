package com.sample;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
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
            long processInstanceId = processInstance.getId();
            
            List<Long> tasks = taskService.getTasksByProcessInstanceId(processInstanceId);
            Task task = taskService.getTaskById(tasks.get(0));
            Date createdOn = task.getTaskData().getCreatedOn();
            System.out.println("task.getTaskData().getCreatedOn() = " + createdOn);
            
            List<? extends VariableInstanceLog> findVariableInstances = auditService.findVariableInstances(processInstanceId, "expireDelay");
            VariableInstanceLog variableInstanceLog = findVariableInstances.get(findVariableInstances.size() - 1);
            String expireDelay = variableInstanceLog.getValue();
            
            System.out.println("Process Variable \"expireDelay\" = " + expireDelay);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdOn);
            calendar.add(Calendar.SECOND, Integer.parseInt(expireDelay));
            Date expireTime = calendar.getTime();
            System.out.println("expireTime = " + expireTime);

            long timeRemaining = (calendar.getTimeInMillis() - System.currentTimeMillis());
            System.out.println("Time remaining till Boundary Timer fire = " + timeRemaining + "ms");
        }
    }
}