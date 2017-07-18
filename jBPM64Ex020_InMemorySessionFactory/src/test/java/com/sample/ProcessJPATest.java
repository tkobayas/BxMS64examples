package com.sample;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.runtime.manager.impl.PerRequestRuntimeManager;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.factory.InMemorySessionFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest {

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    public void testProcess() throws Exception {

        try {

            RuntimeManager manager = getRuntimeManager( "sample.bpmn" );

            for ( int i = 0; i < 100; i++ ) {
                RuntimeEngine runtime = manager.getRuntimeEngine( ProcessInstanceIdContext.get() );
                KieSession ksession = runtime.getKieSession();

                // start a new process instance
                Map<String, Object> params = new HashMap<String, Object>();
                ProcessInstance pi = ksession.startProcess( "com.sample.bpmn.hello", params );
                System.out.println( "A process instance started : pid = " + pi.getId() );

                // -----------
                manager.disposeRuntimeEngine( runtime );
            }

            InMemorySessionFactory factory = (InMemorySessionFactory) ((PerRequestRuntimeManager) manager).getFactory();
            System.out.println( factory );

            Field field = InMemorySessionFactory.class.getDeclaredField( "sessions" );
            field.setAccessible( true );
            Map<Long, KieSession> sessions = (Map<Long, KieSession>) field.get( factory );

            System.out.println( "### factory.sessions.size() = " + sessions.size() );
            
            manager.close();

        } catch ( Throwable th ) {
            th.printStackTrace();
        }
    }

    private static RuntimeManager getRuntimeManager(String process) {
        Properties properties = new Properties();
        properties.setProperty("krisv", "");
        properties.setProperty("mary", "");
        properties.setProperty("john", "");
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
            .newEmptyBuilder()
            .addConfiguration("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName())
            .addConfiguration("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName())            
            .registerableItemsFactory(new SimpleRegisterableItemsFactory())
            .userGroupCallback(userGroupCallback)
            .addAsset(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2);

        return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(builder.get());

    }
}