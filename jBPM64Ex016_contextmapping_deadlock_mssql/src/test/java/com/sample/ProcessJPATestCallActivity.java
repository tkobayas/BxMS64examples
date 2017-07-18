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
public class ProcessJPATestCallActivity {

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


            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD);

            for (int n = 0; n < MAX_THREAD; n++) {
                executor.execute(new Runnable() {

                    public void run() {
                        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                        KieSession ksession = runtime.getKieSession();
                        
                        Map<String, Object> params = new HashMap<String, Object>();
                        ProcessInstance pi = ksession.startProcess("parent", params);
                        System.out.println("A process instance started : pid = " + pi.getId());
                        
                        long piid = pi.getId();
                        
                        manager.disposeRuntimeEngine(runtime);
                        
                        //----
                        
                        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
                        TaskService taskService2 = runtime2.getTaskService();
                        
                        {
                            List<TaskSummary> list = taskService2.getTasksAssignedAsPotentialOwner( "john", "en-UK" );
                            System.out.println( "list.size() = " + list.size() );
                            if (list.size() > 0) {
                                TaskSummary taskSummary = list.get( 0 );
                                
                                RuntimeEngine runtime3 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(taskSummary.getProcessInstanceId()));
                                TaskService taskService3 = runtime3.getTaskService();
                                
                                Long taskId = taskSummary.getId();
                                
                                System.out.println("john starts a task : taskId = " + taskId);
                                taskService3.start(taskId, "john");
                                taskService3.complete(taskId, "john", null);
                                
                                manager.disposeRuntimeEngine( runtime3 );
                            }
                        }
                        
                        manager.disposeRuntimeEngine(runtime2);
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(3000, TimeUnit.SECONDS);
            
            System.out.println( "===== finish ====" );

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
                .addAsset(ResourceFactory.newClassPathResource("child.bpmn"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("parent.bpmn"), ResourceType.BPMN2)
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