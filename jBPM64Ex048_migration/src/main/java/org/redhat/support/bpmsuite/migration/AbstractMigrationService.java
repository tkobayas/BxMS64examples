/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.redhat.support.bpmsuite.migration;

import java.io.File;
import java.io.FilenameFilter;

import javax.persistence.EntityManagerFactory;

import org.dashbuilder.DataSetCore;
import org.jbpm.kie.services.impl.FormManagerServiceImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.UserTaskServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public abstract class AbstractMigrationService {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMigrationService.class);

	protected PoolingDataSource ds;

	protected EntityManagerFactory emf;
	protected DeploymentService deploymentService;
	protected DefinitionService bpmn2Service;
	protected RuntimeDataService runtimeDataService;
	protected ProcessService processService;
	protected UserTaskService userTaskService;
	protected QueryService queryService;

	protected TestIdentityProvider identityProvider;

	public static void configure() {
		LoggingPrintStream.interceptSysOutSysErr();

	}

	public static void reset() {
		LoggingPrintStream.resetInterceptSysOutSysErr();

	}

	protected void close() {
		DataSetCore.set(null);
		if (emf != null) {
			emf.close();
		}
		EntityManagerFactoryManager.get().clear();
		closeDataSource();
	}

	protected void configureServices() {
		buildDatasource();
		emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
		identityProvider = new TestIdentityProvider();

		// build definition service
		bpmn2Service = new BPMN2DataServiceImpl();

		queryService = new QueryServiceImpl();
		((QueryServiceImpl) queryService).setIdentityProvider(identityProvider);
		((QueryServiceImpl) queryService).setCommandService(new TransactionalCommandService(emf));
		((QueryServiceImpl) queryService).init();

		// build deployment service
		deploymentService = new KModuleDeploymentService();
		((KModuleDeploymentService) deploymentService).setBpmn2Service(bpmn2Service);
		((KModuleDeploymentService) deploymentService).setEmf(emf);
		((KModuleDeploymentService) deploymentService).setIdentityProvider(identityProvider);
		((KModuleDeploymentService) deploymentService).setManagerFactory(new RuntimeManagerFactoryImpl());
		((KModuleDeploymentService) deploymentService).setFormManagerService(new FormManagerServiceImpl());

		TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator().entityManagerFactory(emf)
				.getTaskService();

		// build runtime data service
		runtimeDataService = new RuntimeDataServiceImpl();
		((RuntimeDataServiceImpl) runtimeDataService).setCommandService(new TransactionalCommandService(emf));
		((RuntimeDataServiceImpl) runtimeDataService).setIdentityProvider(identityProvider);
		((RuntimeDataServiceImpl) runtimeDataService).setTaskService(taskService);
		((RuntimeDataServiceImpl) runtimeDataService).setTaskAuditService(TaskAuditServiceFactory
				.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService());
		((KModuleDeploymentService) deploymentService).setRuntimeDataService(runtimeDataService);

		// set runtime data service as listener on deployment service
		((KModuleDeploymentService) deploymentService).addListener(((RuntimeDataServiceImpl) runtimeDataService));
		((KModuleDeploymentService) deploymentService).addListener(((BPMN2DataServiceImpl) bpmn2Service));

		// build process service
		processService = new ProcessServiceImpl();
		((ProcessServiceImpl) processService).setDataService(runtimeDataService);
		((ProcessServiceImpl) processService).setDeploymentService(deploymentService);

		// build user task service
		userTaskService = new UserTaskServiceImpl();
		((UserTaskServiceImpl) userTaskService).setDataService(runtimeDataService);
		((UserTaskServiceImpl) userTaskService).setDeploymentService(deploymentService);
	}

	protected void buildDatasource() {
		ds = new PoolingDataSource();

		ds.setUniqueName("jdbc/testDS1");

		
		// make sure the migration is working with the same database as where your deployments are stored!
		
		// NON XA CONFIGS
		ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");

		ds.setMaxPoolSize(3);
		ds.setAllowLocalTransactions(true);
		ds.getDriverProperties().put("user", "mysql");
		ds.getDriverProperties().put("password", "mysql");
		ds.getDriverProperties().setProperty("portNumber", "3306");



		ds.getDriverProperties().setProperty("serverName", "localhost");
		ds.getDriverProperties().setProperty("databaseName", "testbpms640");

		ds.init();
	}

	protected void closeDataSource() {
		if (ds != null) {
			ds.close();
		}
	}

	public static void cleanupSingletonSessionId() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		if (tempDir.exists()) {

			String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {

					return name.endsWith("-jbpmSessionId.ser");
				}
			});
			for (String file : jbpmSerFiles) {
				logger.debug("Temp dir to be removed {} file {}", tempDir, file);
				new File(tempDir, file).delete();
			}
		}
	}

	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}

	public void setBpmn2Service(DefinitionService bpmn2Service) {
		this.bpmn2Service = bpmn2Service;
	}

	public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
		this.runtimeDataService = runtimeDataService;
	}

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

	public void setUserTaskService(UserTaskService userTaskService) {
		this.userTaskService = userTaskService;
	}

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	public void setIdentityProvider(TestIdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

}
