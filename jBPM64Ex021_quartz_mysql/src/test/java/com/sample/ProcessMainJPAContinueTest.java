package com.sample;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import javax.transaction.SystemException;

import junit.framework.TestCase;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class ProcessMainJPAContinueTest extends TestCase {

    private static boolean mySQL = true;

    private static EntityManagerFactory emf;

    @Test
    public void testContinueTimer() throws Exception {

        System.setProperty("org.quartz.properties", "quartz.properties");
        //System.setProperty("org.jbpm.rm.init.timer", "false");

        setup();

        long piid = -1;

        // ---- 1st boot
        {
            RuntimeManager manager = getRuntimeManager("timer-process.bpmn2");

            System.out.println(new Date() + " : " + "RuntimeManager up");

            RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());

            KieSession ksession = runtime.getKieSession();

            Thread.sleep(10000);

            manager.disposeRuntimeEngine(runtime);

            System.out.println(new Date() + " : " + "RuntimeEngine disposed");

            manager.close();

            System.out.println(new Date() + " : " + "RuntimeManager closed");
        }

        assertTrue(true);
    }

    private static void setup() throws SystemException {
        BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
        transactionManager.setTransactionTimeout(3600);

        if (mySQL) {
            // for external database datasource
            setupDataSource();
        } else {
            // for H2 datasource
            JBPMHelper.startH2Server();
            JBPMHelper.setupDataSource();
        }

        Map configOverrides = new HashMap();
        if (mySQL) {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        } else {
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        }

        emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa", configOverrides);
    }

    private static RuntimeManager getRuntimeManager(String process) {

        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("john", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence(true).entityManagerFactory(emf).userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2).get();
        //      return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        //         return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);

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
        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testbpms640quartz");
        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
        pds.init();
        return pds;
    }
}