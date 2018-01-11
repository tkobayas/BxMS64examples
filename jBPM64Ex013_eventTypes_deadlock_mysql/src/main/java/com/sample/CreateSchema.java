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

    public static void main(String[] args) {
        setupDataSource();

        dropTables();
        testCreateJBPMSchema();

        System.out.println("finished");
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
        pds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/testbpms640_2");
        pds.getDriverProperties().put("driverClassName", "com.mysql.jdbc.Driver");
        pds.init();
        return pds;
    }

    private static void dropTables() {
        try (Connection connection = ((DataSource) InitialContext.doLookup("jdbc/jbpm-ds")).getConnection();
                Statement stmt = connection.createStatement();) {

            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.executeUpdate("DROP TABLE attachment");
            stmt.executeUpdate("DROP TABLE audittaskimpl");
            stmt.executeUpdate("DROP TABLE bamtasksummary");
            stmt.executeUpdate("DROP TABLE booleanexpression");
            stmt.executeUpdate("DROP TABLE content");
            stmt.executeUpdate("DROP TABLE contextmappinginfo");
            stmt.executeUpdate("DROP TABLE correlationkeyinfo");
            stmt.executeUpdate("DROP TABLE correlationpropertyinfo");
            stmt.executeUpdate("DROP TABLE deadline");
            stmt.executeUpdate("DROP TABLE delegation_delegates");
            stmt.executeUpdate("DROP TABLE deploymentstore");
            stmt.executeUpdate("DROP TABLE email_header");
            stmt.executeUpdate("DROP TABLE errorinfo");
            stmt.executeUpdate("DROP TABLE escalation");
            stmt.executeUpdate("DROP TABLE eventtypes");
            stmt.executeUpdate("DROP TABLE i18ntext");
            stmt.executeUpdate("DROP TABLE nodeinstancelog");
            stmt.executeUpdate("DROP TABLE notification");
            stmt.executeUpdate("DROP TABLE notification_bas");
            stmt.executeUpdate("DROP TABLE notification_email_header");
            stmt.executeUpdate("DROP TABLE notification_recipients");
            stmt.executeUpdate("DROP TABLE organizationalentity");
            stmt.executeUpdate("DROP TABLE peopleassignments_bas");
            stmt.executeUpdate("DROP TABLE peopleassignments_exclowners");
            stmt.executeUpdate("DROP TABLE peopleassignments_potowners");
            stmt.executeUpdate("DROP TABLE peopleassignments_recipients");
            stmt.executeUpdate("DROP TABLE peopleassignments_stakeholders");
            stmt.executeUpdate("DROP TABLE processinstanceinfo");
            stmt.executeUpdate("DROP TABLE processinstancelog");
            stmt.executeUpdate("DROP TABLE querydefinitionstore");
            stmt.executeUpdate("DROP TABLE reassignment");
            stmt.executeUpdate("DROP TABLE reassignment_potentialowners");
            stmt.executeUpdate("DROP TABLE requestinfo");
            stmt.executeUpdate("DROP TABLE sessioninfo");
            stmt.executeUpdate("DROP TABLE task");
            stmt.executeUpdate("DROP TABLE task_comment");
            stmt.executeUpdate("DROP TABLE taskdef");
            stmt.executeUpdate("DROP TABLE taskevent");
            stmt.executeUpdate("DROP TABLE taskvariableimpl");
            stmt.executeUpdate("DROP TABLE variableinstancelog");
            stmt.executeUpdate("DROP TABLE workiteminfo");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCreateJBPMSchema() {
        try (Scanner scanner = new Scanner(CreateSchema.class.getResourceAsStream("/mysql5-jbpm-schema.sql")).useDelimiter(";");
                Connection connection = ((DataSource) InitialContext.doLookup("jdbc/jbpm-ds")).getConnection();
                Statement stmt = connection.createStatement();) {

            while (scanner.hasNext()) {
                String sql = scanner.next();
                //System.out.println(sql);
                if (sql.trim().isEmpty()) {
                    break;
                }
                stmt.executeUpdate(sql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}