package com.sample;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

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
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class CreateSchema {

    private static PoolingDataSource ds;

    public static void main( String[] args ) {
        setupDataSource();
        
        testCreateJBPMSchema();
        
        System.out.println( "finished" );
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

    public static void testCreateJBPMSchema() {
        Scanner scanner = new Scanner( CreateSchema.class.getResourceAsStream( "/mysql5-jbpm-schema.sql" ) ).useDelimiter( ";" );
        try {
            Connection connection = ((DataSource) InitialContext.doLookup( "jdbc/jbpm-ds" )).getConnection();
            Statement stmt = connection.createStatement();
            while ( scanner.hasNext() ) {
                String sql = scanner.next();
                stmt.executeUpdate( sql );
            }
            stmt.close();
            connection.close();
        } catch ( Exception e ) {

        }
    }

}