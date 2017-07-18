package com.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.XAConnectionFactory;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.jms.JmsAvailableJobsExecutor;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jms.PoolingConnectionFactory;

/**
 * This is a sample file to launch a process.
 */
public class JMSExecutorTest {

    private static final Logger logger = LoggerFactory.getLogger( JMSExecutorTest.class );

    private static EntityManagerFactory emf;

    private static PoolingDataSource pds;

    protected ExecutorService executorService;

    private ConnectionFactory factory;
    private Queue queue;

    private EmbeddedJMS jmsServer;

    @Before
    public void setup() throws Exception {

        startHornetQServer();

        pds = setupDataSource();

        Map configOverrides = new HashMap();
        configOverrides.put( "hibernate.hbm2ddl.auto", "none" ); 

        configOverrides.put( "hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect" ); 

        emf = Persistence.createEntityManagerFactory( "org.jbpm.example", configOverrides );

        executorService = ExecutorServiceFactory.newExecutorService( emf );

        ((ExecutorImpl) ((ExecutorServiceImpl) executorService).getExecutor()).setConnectionFactory( factory );
        ((ExecutorImpl) ((ExecutorServiceImpl) executorService).getExecutor()).setQueue( queue );

        executorService.setThreadPoolSize( 1 );
        executorService.setInterval( 1 );
        executorService.init();
    }

    @After
    public void teardown() throws Exception {

        executorService.clearAllRequests();
        executorService.clearAllErrors();

        executorService.destroy();
        if ( emf != null ) {
            emf.close();
        }
        pds.close();

        System.clearProperty( "org.kie.executor.msg.length" );
        System.clearProperty( "org.kie.executor.stacktrace.length" );

        stopHornetQServer();
    }

    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }
    
    @Test
    public void testProcess() throws Exception {

        try {
            
            System.out.println( "=======================================================" );

            CountDownAsyncJobListener countDownListener = configureListener(5);

            UserTransaction ut = InitialContext.doLookup( "java:comp/UserTransaction" );
            ut.begin();
            for ( int i = 0; i < 5; i++ ) {
                CommandContext ctxCMD = new CommandContext();
                ctxCMD.setData( "businessKey", UUID.randomUUID().toString() );
                executorService.scheduleRequest( "com.sample.MyCommand", ctxCMD );
            }
            ut.commit();
            
            System.out.println( "---------------------------------------------------------" );

            MessageReceiver receiver = new MessageReceiver();
            receiver.receiveAndProcess(queue, countDownListener);
            
            Thread.sleep( 5000 );
            
            System.out.println( "=======================================================" );



        } catch ( Throwable th ) {
            th.printStackTrace();
        }
    }

    private class MessageReceiver {
        
        void receiveAndProcess(Queue queue, CountDownAsyncJobListener countDownListener) throws Exception {
            
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            JmsAvailableJobsExecutor jmsExecutor = new JmsAvailableJobsExecutor();
            jmsExecutor.setClassCacheManager(new ClassCacheManager());
            jmsExecutor.setExecutorStoreService(((ExecutorImpl)((ExecutorServiceImpl)executorService).getExecutor()).getExecutorStoreService());
            jmsExecutor.setQueryService(((ExecutorServiceImpl)executorService).getQueryService());
            jmsExecutor.setEventSupport(((ExecutorServiceImpl)executorService).getEventSupport());
            consumer.setMessageListener(jmsExecutor);
            // since we use message listener allow it to complete the async processing
            countDownListener.waitTillCompleted(5000);
            
            consumer.close();            
            qsession.close();            
            qconnetion.close();

        }
        
        public List<Message> receive(Queue queue) throws Exception {
            List<Message> messages = new ArrayList<Message>();
            
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            
            Message m = null;
            
            while ((m = consumer.receiveNoWait()) != null) {
                messages.add(m);
            }
            consumer.close();            
            qsession.close();            
            qconnetion.close();
            
            return messages;
        }
    }
    
    private static RuntimeManager getRuntimeManager( String process ) {
        Properties properties = new Properties();
        properties.setProperty( "krisv", "" );
        properties.setProperty( "mary", "" );
        properties.setProperty( "john", "" );
        UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl( properties );

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault().persistence( true ).entityManagerFactory( emf )
                .userGroupCallback( userGroupCallback ).addAsset( ResourceFactory.newClassPathResource( process ), ResourceType.BPMN2 ).get();
        return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager( environment );

    }

    public static PoolingDataSource setupDataSource() {
        // Please edit here when you want to use your database
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName( "jdbc/jbpm-ds" );
        pds.setClassName( "bitronix.tm.resource.jdbc.lrc.LrcXADataSource" );
        pds.setMaxPoolSize( 5 );
        pds.setAllowLocalTransactions( true );
        pds.getDriverProperties().put( "user", "mysql" );
        pds.getDriverProperties().put( "password", "mysql" );
        pds.getDriverProperties().put( "url", "jdbc:mysql://localhost:3306/testbpms640_2" );
        pds.getDriverProperties().put( "driverClassName", "com.mysql.jdbc.Driver" );
        pds.init();
        return pds;
    }

    private void startHornetQServer() throws Exception {
        jmsServer = new EmbeddedJMS();
        jmsServer.start();
        logger.info( "Started Embedded JMS Server" );

        BitronixHornetQXAConnectionFactory.connectionFactory = (XAConnectionFactory) jmsServer.lookup( "ConnectionFactory" );

        PoolingConnectionFactory myConnectionFactory = new PoolingConnectionFactory();
        myConnectionFactory.setClassName( "com.sample.BitronixHornetQXAConnectionFactory" );
        myConnectionFactory.setUniqueName( "ConnectionFactory" );
        myConnectionFactory.setMaxPoolSize( 5 );
        myConnectionFactory.setAllowLocalTransactions( true );

        myConnectionFactory.init();

        factory = myConnectionFactory;

        queue = (Queue) jmsServer.lookup( "/queue/exampleQueue" );

    }

    private void stopHornetQServer() throws Exception {
        ((PoolingConnectionFactory) factory).close();
        jmsServer.stop();
        jmsServer = null;
    }
}