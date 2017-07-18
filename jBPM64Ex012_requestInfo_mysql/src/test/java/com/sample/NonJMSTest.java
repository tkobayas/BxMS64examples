package com.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.h2.tools.Server;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class NonJMSTest {

    private static EntityManagerFactory emf;

    private static PoolingDataSource ds;

    protected ExecutorService executorService;

    @Before
    public void setup() {

        // for external database datasource
        ds = setupDataSource();

        Map configOverrides = new HashMap();
        configOverrides.put( "hibernate.hbm2ddl.auto", "none" ); 

        configOverrides.put( "hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect" );

        emf = Persistence.createEntityManagerFactory( "org.jbpm.example", configOverrides );

        executorService = ExecutorServiceFactory.newExecutorService( emf );
        executorService.setThreadPoolSize( 10 );
        executorService.setInterval( 1 );
        executorService.init();
    }

    @After
    public void teardown() {
        if ( ds != null ) {
            ds.close();
        }
    }

    @Test
    public void testProcess() throws Exception {

        try {

            UserTransaction ut = InitialContext.doLookup( "java:comp/UserTransaction" );
            ut.begin();
            for ( int i = 0; i < 50; i++ ) {
                CommandContext ctxCMD = new CommandContext();
                ctxCMD.setData( "businessKey", UUID.randomUUID().toString() );
                executorService.scheduleRequest( "com.sample.MyCommand", ctxCMD );
            }
            ut.commit();

            Thread.sleep( 10000 );

            // -----------
            //            manager.disposeRuntimeEngine(runtime);

        } catch ( Throwable th ) {
            th.printStackTrace();
        }
    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName( "jdbc/jbpm-ds" );
        pds.setClassName( "bitronix.tm.resource.jdbc.lrc.LrcXADataSource" );
        pds.setMaxPoolSize( 10 );
        pds.setAllowLocalTransactions( true );
        pds.getDriverProperties().put( "user", "mysql" );
        pds.getDriverProperties().put( "password", "mysql" );
        pds.getDriverProperties().put( "url", "jdbc:mysql://localhost:3306/testbpms640_2" );
        pds.getDriverProperties().put( "driverClassName", "com.mysql.jdbc.Driver" );
        pds.init();
        return pds;
    }
}