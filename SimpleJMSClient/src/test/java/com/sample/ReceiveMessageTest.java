package com.sample;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveMessageTest {

    private static final Logger log = LoggerFactory.getLogger(ReceiveMessageTest.class.getName());

    private static final String JMS_USERNAME = "bpmsAdmin";
    private static final String JMS_PASSWORD = "password1!";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";

    @Test
    public void testReceiveMessage() throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        Destination destination = null;
        TextMessage message = null;
        Context context = null;

        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        env.put(Context.PROVIDER_URL, "remote://localhost:4447");
        env.put(Context.SECURITY_PRINCIPAL, JMS_USERNAME);
        env.put(Context.SECURITY_CREDENTIALS, JMS_PASSWORD);
        context = new InitialContext(env);

        destination = (Queue) context.lookup("jms/queue/KIE.SERVER.SIGNAL");
        connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);

        // Create the JMS connection, session, producer, and consumer
        connection = connectionFactory.createConnection(JMS_USERNAME, JMS_PASSWORD);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        producer = session.createProducer(destination);
        consumer = session.createConsumer(destination);
        connection.start();

//        String content = "Hello Message";

//        log.info("Sending a message with content: " + content);

//        message = session.createTextMessage(content);
//        producer.send(message);

        message = (TextMessage) consumer.receive(5000);
        log.info("Received message with content " + message.getText());

        if (context != null) {
            context.close();
        }

        // closing the connection takes care of the session, producer, and consumer
        if (connection != null) {
            connection.close();
        }

    }

}
