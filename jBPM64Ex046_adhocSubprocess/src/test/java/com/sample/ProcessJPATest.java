package com.sample;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.h2.tools.Server;
import org.jbpm.examples.junit.Person;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
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
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    private static final boolean H2 = true;

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
        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // comment out if you don't want to clean up tables
        if (H2) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        } else {
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

            RuntimeManager manager = getRuntimeManager();
            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
            KieSession ksession = runtime.getKieSession();

            TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                    workItemHandler);
            ProcessInstance processInstance = ksession
                    .startProcess("AdHocSubProcess");
            assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
            assertEquals("Entry", ((WorkflowProcessInstance) processInstance).getVariable("x"));
            WorkItem workItem = workItemHandler.getWorkItem();
            assertNull(workItem);


            ksession.fireAllRules();

            ksession.signalEvent("Hello2", null, processInstance.getId());
            workItem = workItemHandler.getWorkItem();
            assertNotNull(workItem);

            ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
            // -----------
            manager.disposeRuntimeEngine(runtime);

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
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.drl"), ResourceType.DRL)
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
        pds.getDriverProperties().put("user", "mysql");
        pds.getDriverProperties().put("password", "mysql");
        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testbpms640");
        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
        pds.init();
        return pds;
    }
}