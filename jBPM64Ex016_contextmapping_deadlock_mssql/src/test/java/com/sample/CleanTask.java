package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.Server;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class CleanTask {

    private static final boolean H2 = false;

    private static final int MAX_THREAD = 2;

    private static EntityManagerFactory emf;

    private static Server h2Server;
    private static PoolingDataSource ds;

    @Before
    public void setup() {

        if (H2) {
            // for H2 datasource
            h2Server = JBPMHelper.startH2Server();
            ds = JBPMHelper.setupDataSource();
        } else {
            // for external database datasource
            ds = setupDataSource();
        }

        Map configOverrides = new HashMap();
        configOverrides.put("hibernate.hbm2ddl.auto", "none"); // Uncomment if you don't want to clean up tables
        if (H2) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        } else {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect"); // Change for other DB

//            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect"); // Change for other DB
        }
        emf = Persistence.createEntityManagerFactory("org.jbpm.example", configOverrides);
    }

    @After
    public void teardown() {
        if (ds != null) {
            ds.close();
        }
        if (h2Server != null) {
            h2Server.shutdown();
        }
    }

    @Test
    public void testProcess() throws Exception {

        try {

            RuntimeManager manager = getRuntimeManager();



            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
            TaskService taskService = runtime.getTaskService();
            
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
                for (TaskSummary taskSummary : list) {
                    System.out.println("john starts a task : taskId = " + taskSummary.getId());
                    
                    RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(taskSummary.getProcessInstanceId()));
                    TaskService taskService2 = runtime2.getTaskService();
                    
                    taskService2.start(taskSummary.getId(), "john");
                    taskService2.complete(taskSummary.getId(), "john", null);
                    
                    manager.disposeRuntimeEngine( runtime2 );
                }
            }

            // -----------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
                for (TaskSummary taskSummary : list) {
                    System.out.println("mary starts a task : taskId = " + taskSummary.getId());
                    
                    RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(taskSummary.getProcessInstanceId()));
                    TaskService taskService2 = runtime2.getTaskService();
                    
                    taskService2.start(taskSummary.getId(), "mary");
                    taskService2.complete(taskSummary.getId(), "mary", null);
                    
                    manager.disposeRuntimeEngine( runtime2 );
                }
            }



            // -----------

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager() {
        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("john", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true)
                .entityManagerFactory(emf).userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2)
//                .addAsset(ResourceFactory.newClassPathResource("parent.bpmn"), ResourceType.BPMN2)
                .get();
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);

    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "RedHat1!");
        pds.getDriverProperties().put("url", "jdbc:sqlserver://win2012ad.gsslab.nrt.redhat.com:1433;databaseName=BPMS640");
        pds.getDriverProperties().put("driverClassName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        pds.init();
        return pds;
    }
}