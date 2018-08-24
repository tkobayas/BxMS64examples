/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.admin.ProcessInstanceMigrationServiceImpl;
import org.jbpm.services.api.admin.MigrationReport;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedMigrationService extends AbstractMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedMigrationService.class);

    /**
     * Change the below attributes so they fit your environment
     * 
     * 
     */
    protected static final String MIGRATION_GROUP_ID = "com.sample";
    protected static final String MIGRATION_ARTIFACT_ID = "migration-example";
    protected static final String SOURCE_MIGRATION_VERSION_V1 = "1.0.0";
    protected static final String TARGET_MIGRATION_VERSION_V2 = "2.0.0";
    protected static final Long PROCESS_INSTANCE_ID = 1L;
    protected static final String TARGET_PROCESS_ID = "project1.helloProcess";

    protected ProcessInstanceMigrationService migrationService;

    public static void main(String[] args) {

        EmbeddedMigrationService hubMigrationService = new EmbeddedMigrationService();
        hubMigrationService.prepare();

        // Source deployment unit
        KModuleDeploymentUnit sourceKjarArtifact = new KModuleDeploymentUnit(MIGRATION_GROUP_ID, MIGRATION_ARTIFACT_ID, SOURCE_MIGRATION_VERSION_V1);
        // Target deployment unit
        KModuleDeploymentUnit destinationKjarArtifact = new KModuleDeploymentUnit(MIGRATION_GROUP_ID, MIGRATION_ARTIFACT_ID, TARGET_MIGRATION_VERSION_V2);
        hubMigrationService.migrateProcessInstance(sourceKjarArtifact, destinationKjarArtifact, PROCESS_INSTANCE_ID, TARGET_PROCESS_ID);

    }

    public void prepare() {
        configureServices();

        migrationService = new ProcessInstanceMigrationServiceImpl();

    }

    public void migrateProcessInstance(DeploymentUnit sourceDeployment, DeploymentUnit targetDeployment, long processInstanceId, String targetProcessId) {

        if (!deploymentService.isDeployed(sourceDeployment.getIdentifier()) && !deploymentService.isDeployed(targetDeployment.getIdentifier())) {
            deploymentService.deploy(sourceDeployment);
            deploymentService.deploy(targetDeployment);
        }

        Map<String, String> nodeMapping = new HashMap<String, String>();
//        nodeMapping.put("_09144120-B114-451A-A0E9-8425D6955190", "_DDD56188-8EE5-40B4-80ED-AF34D3D5B696"); // put(oldNodeId, newNodeId);
        
        MigrationReport report = migrationService.migrate(sourceDeployment.getIdentifier(), processInstanceId, targetDeployment.getIdentifier(), targetProcessId, nodeMapping);
        if (report.isSuccessful()) {
            logger.info("Process instance " + processInstanceId + " is migrated successfully to " + targetDeployment.getIdentifier());
        } else {
            logger.info("Migration failed for process instance " + processInstanceId);
        }
    }

}
