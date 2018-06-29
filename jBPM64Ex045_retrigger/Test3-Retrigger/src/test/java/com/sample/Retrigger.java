package com.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.h2.tools.Server;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JBPMHelper;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class Retrigger {

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
//        configOverrides.put("hibernate.hbm2ddl.auto", "create"); // comment out if you don't want to clean up tables
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
            TaskService taskService = runtime.getTaskService();
            
            final long processInstanceId = 1L;
            
            List<Long> badTaskIdList = new ArrayList<Long>();

			{
				List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
				for (TaskSummary taskSummary : list) {
					long taskId = taskSummary.getId();
					System.out.println("taskSummary = " + taskSummary + ", taskId = " + taskId);

					// retrigger
					Boolean retriggered = retriggerCorruptedWorkItem(taskId, processInstanceId, ksession, taskService);
					
					if (retriggered) {
						System.out.println("Exit old task : taskId = " + taskId);
						taskService.exit(taskId, "Administrator");
					} else {
						System.out.println("Nothing to retrigger. Go ahead");
						
						if (taskSummary.getStatus().equals(Status.Ready)
								|| taskSummary.getStatus().equals(Status.Reserved)) {
							taskService.start(taskSummary.getId(), "john");
						}
						taskService.complete(taskSummary.getId(), "john", null);
					}
				
				}
			}
			
			// one more time
			{
				List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
				for (TaskSummary taskSummary : list) {
					long taskId = taskSummary.getId();
					System.out.println("taskSummary = " + taskSummary + ", taskId = " + taskId);

					// retrigger
					Boolean retriggered = retriggerCorruptedWorkItem(taskId, processInstanceId, ksession, taskService);
					
					if (retriggered) {
						System.out.println("Exit old task : taskId = " + taskId);
						taskService.exit(taskId, "Administrator");
					} else {
						System.out.println("Nothing to retrigger. Go ahead");
						
						if (taskSummary.getStatus().equals(Status.Ready)
								|| taskSummary.getStatus().equals(Status.Reserved)) {
							taskService.start(taskSummary.getId(), "john");
						}
						taskService.complete(taskSummary.getId(), "john", null);
					}
				
				}
			}

            // -----------
            manager.disposeRuntimeEngine(runtime);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private Boolean retriggerCorruptedWorkItem(long taskId, final long processInstanceId, KieSession ksession,
			TaskService taskService) {
    	
    	return ksession.execute(new GenericCommand<Boolean>() {

			@Override
			public Boolean execute(Context context) {
				
				Boolean retriggered = false;

				KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();

				WorkflowProcessInstance pi = (WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId);
				Collection<NodeInstance> instances = pi.getNodeInstances();
				for (NodeInstance ni : instances) {
					System.out.println(ni);
					if (ni instanceof WorkItemNodeInstance) {
						WorkItemNodeInstance workItemNodeInstance = (WorkItemNodeInstance) ni;
						if (workItemNodeInstance.getWorkItemId() == -1 && workItemNodeInstance.getNodeName().equals("Task 1")) {
							System.out.println("  Retrigger!");
							workItemNodeInstance.retrigger(true);
							
							retriggered = true;
						}
					}
				}

				return retriggered;
			}
		});
		
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
        return RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);

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