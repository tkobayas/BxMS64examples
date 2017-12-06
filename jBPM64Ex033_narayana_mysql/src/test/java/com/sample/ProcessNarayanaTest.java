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
import org.jbpm.test.util.PoolingDataSource;
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
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class ProcessNarayanaTest {

    private static EntityManagerFactory emf;

    private static PoolingDataSource ds;

    @Before
    public void setup() {
        ds = setupDataSource();

        Map configOverrides = new HashMap();
        configOverrides.put( "hibernate.hbm2ddl.auto", "create" ); // Uncomment if you don't want to clean up tables

        configOverrides.put( "hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect" ); // Change for other DB

        emf = Persistence.createEntityManagerFactory( "org.jbpm.example", configOverrides );
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

            RuntimeManager manager = getRuntimeManager( "sample.bpmn" );
            RuntimeEngine runtime = manager.getRuntimeEngine( ProcessInstanceIdContext.get() );
            KieSession ksession = runtime.getKieSession();

            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            ProcessInstance pi = ksession.startProcess( "com.sample.bpmn.hello", params );
            System.out.println( "A process instance started : pid = " + pi.getId() );

            //transactionManager.commit();

            TaskService taskService = runtime.getTaskService();

            // ------------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner( "john", "en-UK" );
                for ( TaskSummary taskSummary : list ) {
                    System.out.println( "john starts a task : taskId = " + taskSummary.getId() );
                    taskService.start( taskSummary.getId(), "john" );
                    taskService.complete( taskSummary.getId(), "john", null );
                }
            }

            // -----------
            {
                List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner( "mary", "en-UK" );
                for ( TaskSummary taskSummary : list ) {
                    System.out.println( "mary starts a task : taskId = " + taskSummary.getId() );
                    taskService.start( taskSummary.getId(), "mary" );
                    taskService.complete( taskSummary.getId(), "mary", null );
                }
            }

            // -----------
            manager.disposeRuntimeEngine( runtime );

        } catch ( Throwable th ) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager( String process ) {
        Properties properties = new Properties();
        properties.setProperty( "krisv", "HR,PM" );
        properties.setProperty( "mary", "HR" );
        properties.setProperty( "john", "PM" );
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl( properties );

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence( true ).entityManagerFactory( emf )
                .userGroupCallback( userGroupCallback ).addAsset( ResourceFactory.newClassPathResource( process ), ResourceType.BPMN2 ).get();
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager( environment );

    }

    public static PoolingDataSource setupDataSource() {

        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName( "jdbc/jbpm-ds" );
        pds.setClassName( "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource" );
        pds.getDriverProperties().put( "user", "mysql" );
        pds.getDriverProperties().put( "password", "mysql" );
        pds.getDriverProperties().put( "url", "jdbc:mysql://localhost:3306/testbpms640" );
        pds.getDriverProperties().put( "driverClassName", "com.mysql.jdbc.Driver" );
        pds.init();
        return pds;
    }
}