package com.sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWorkItemHandler implements WorkItemHandler {

    private static Logger logger = LoggerFactory.getLogger(MyWorkItemHandler.class);

    public MyWorkItemHandler() {
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        logger.info("enter executeWorkItem()");

        String key = (String) workItem.getParameter("Key");
        //System.out.println("key = " + key);
        InitialContext ic;
        DataSource ds;
        try {
            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:comp/env/jdbc/myappdb");

        } catch (NamingException e) {
            throw new RuntimeException("Error while database access", e);
        }
        String myvalue = null;

        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement("select myvalue from MyTable where mykey = ?");) {

            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    myvalue = rs.getString(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while database access", e);
        }

        HashMap<String, Object> results = new HashMap<String, Object>();
        results.put("Result", myvalue);
        manager.completeWorkItem(workItem.getId(), results);
        return;

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }
}
