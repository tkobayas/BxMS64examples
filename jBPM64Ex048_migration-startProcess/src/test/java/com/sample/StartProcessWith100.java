package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.Server;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class StartProcessWith100 {

    private static EntityManagerFactory emf;

    private static PoolingDataSource ds;

    @Before
    public void setup() {

        // for external database datasource
        ds = setupDataSource();

        Map configOverrides = new HashMap();
        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // comment out if you don't want to clean up tables

        configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); // Change for other DB

        //            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect"); // Change for other DB

        emf = Persistence.createEntityManagerFactory("org.jbpm.domain", configOverrides);
    }

    @After
    public void teardown() {
        if (ds != null) {
            ds.close();
        }
    }

    @Test
    public void testProcess() {

        String groupId = "com.sample";
        String artifactId = "migration-example";
        String version = "1.0.0";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        Properties properties = new Properties();
        properties.setProperty("bpmsAdmin", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder(releaseId)
                .persistence(true)
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addEnvironmentEntry("KieContainer", kieContainer)
                .get();
        RuntimeManager runtimeManager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, releaseId.toExternalForm());
        RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtimeEngine.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance pi = ksession.startProcess("project1.helloProcess", params);
        long piid = pi.getId();
        System.out.println("A process instance started : piid = " + piid);

        runtimeManager.disposeRuntimeEngine(runtimeEngine);

        // work on task
//        RuntimeEngine runtimeEngine2 = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(piid));
//        TaskService taskService = runtimeEngine2.getTaskService();
//
//        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("bpmsAdmin", "en-UK");
//        for (TaskSummary taskSummary : list) {
//            if (taskSummary.getStatus().equals(Status.Ready) || taskSummary.getStatus().equals(Status.Reserved)) {
//                System.out.println("bpmsAdmin starts a task : taskId = " + taskSummary.getId());
//                taskService.start(taskSummary.getId(), "bpmsAdmin");
//            }
//            System.out.println("bpmsAdmin completes a task : taskId = " + taskSummary.getId());
//            taskService.complete(taskSummary.getId(), "bpmsAdmin", null);
//        }
//
//        runtimeManager.disposeRuntimeEngine(runtimeEngine2);

        // -----------
    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "mysql");
        pds.getDriverProperties().put("password", "mysql");
        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testbpms640");
        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
        pds.init();
        return pds;
    }

}