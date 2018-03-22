package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.Server;
import org.jbpm.persistence.correlation.CorrelationKeyInfo;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.KieInternalServices;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
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
        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // Uncomment if you don't want to clean up tables
        if (H2) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        } else {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); // Change for other DB

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

            RuntimeManager manager = getRuntimeManager("sample.bpmn");
            RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
            KieSession ksession = runtime.getKieSession();

            // JPAAuditLogService logService = new JPAAuditLogService(ksession.getEnvironment());


            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            
            CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
            CorrelationKey correlationKey = factory.newCorrelationKey("mybusinesskey");
            ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).createProcessInstance("com.sample.bpmn.hello", correlationKey, null);
            ksession.startProcessInstance(processInstance.getId());
            
            long piid = processInstance.getId();
            
            System.out.println("A process instance started : piid = " + piid);
            
            Object object = ((org.jbpm.process.instance.ProcessInstance)processInstance).getMetaData().get("CorrelationKey");
            System.out.println("object = " + object);

            // -----------
            manager.disposeRuntimeEngine(runtime);
            
            //=======================
            System.out.println("------------------------------------");
            
            RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(piid));
            KieSession ksession2 = runtime2.getKieSession();
            ProcessInstance processInstance2 = ksession2.getProcessInstance(piid);
            Object object2 = ((org.jbpm.process.instance.ProcessInstance)processInstance2).getMetaData().get("CorrelationKey");
            System.out.println("object2 = " + object2);
            
            JPAAuditLogService logService = new JPAAuditLogService(ksession.getEnvironment());
            ProcessInstanceLog log = logService.findProcessInstance(piid);
            String correlationKeyFound = log.getCorrelationKey();
            System.out.println("log = " + log);
            System.out.println("correlationKeyFound = " + correlationKeyFound);
            
            EntityManagerFactory myEmf = (EntityManagerFactory)ksession.getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY);
            EntityManager em = myEmf.createEntityManager();
            List<CorrelationKeyInfo> correlations = em.createNamedQuery("GetCorrelationKeysByProcessInstanceId").setParameter("pId", piid).getResultList();
            System.out.println(correlations);
                    
            manager.disposeRuntimeEngine(runtime2);


        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager(String process) {
        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("john", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true)
                .entityManagerFactory(emf).userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2).get();
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