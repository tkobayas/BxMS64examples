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
import org.jbpm.process.audit.JPAAuditLogService;
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
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    private static final boolean H2 = false;

    private static final int MAX_THREAD = 100;

    protected static final int LOOP = 10;

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
            //            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); // Change for other DB

            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect"); // Change for other DB
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

            // NOTE: This test doesn't reproduce deadlock yet.
            //       Just as basis of a reproducer

            final RuntimeManager manager = getRuntimeManager();

            // JPAAuditLogService logService = new JPAAuditLogService(ksession.getEnvironment());
            // logService.clear();

            BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
            transactionManager.setTransactionTimeout(3600); // longer timeout for a debugger

            // warmup
            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
            KieSession ksession = runtime.getKieSession();
            ProcessInstance pi = ksession.startProcess("parent", null);
            TaskService taskService = runtime.getTaskService();
            // ------------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
                for (TaskSummary taskSummary : list) {
                    System.out.println("john starts a task : taskId = " + taskSummary.getId());
                    taskService.start(taskSummary.getId(), "john");
                    taskService.complete(taskSummary.getId(), "john", null);
                }
            }
            manager.disposeRuntimeEngine(runtime);

            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD);

            for (int n = 0; n < MAX_THREAD; n++) {
                executor.execute(new Runnable() {

                    public void run() {

                        for (int i = 0; i < LOOP; i++) {
                            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                            KieSession ksession = runtime.getKieSession();
                            Map<String, Object> params = new HashMap<String, Object>();
                            ProcessInstance pi = ksession.startProcess("parent", params);
                            long processInstanceId = pi.getId();
                            System.out.println("[" + Thread.currentThread().getName() + "] A process instance started : pid = " + pi.getId());

                            manager.disposeRuntimeEngine(runtime);
                            
                            // ===============
                            
                            
                            RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
                            
                            AuditService auditService = runtime2.getAuditService();
                            List<? extends ProcessInstanceLog> subProcessInstanceList = auditService.findSubProcessInstances(processInstanceId);
                            ProcessInstanceLog subProcessInstance = subProcessInstanceList.get(0);

                            TaskService taskService = runtime2.getTaskService();
                            // ------------
                            {
                                List<Long> list = taskService.getTasksByProcessInstanceId(subProcessInstance.getProcessInstanceId());
//                                List<Long> list = taskService.getTasksByProcessInstanceId(processInstanceId);

                                for (Long taskId : list) {
                                    System.out.println("[" + Thread.currentThread().getName() + "] john starts a task : taskId = " + taskId + " for piid = " + processInstanceId);
                                    taskService.start(taskId, "john");
                                    taskService.complete(taskId, "john", null);
                                }
                            }
                            manager.disposeRuntimeEngine(runtime2);
                        }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(300, TimeUnit.SECONDS);

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

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true).entityManagerFactory(emf).userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("parent.bpmn"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("child.bpmn"), ResourceType.BPMN2)
                .get();
        return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);

    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(100);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "mysql");
        pds.getDriverProperties().put("password", "mysql");
        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testbpms640_2");
        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
        pds.init();
        return pds;
    }
}