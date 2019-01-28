/*
 * Copyright 2016 wetransform GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package to.wetf.hale.progen.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOAdvisorRegister;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.AdvisorProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;

/**
 * Headless advisor for saving a project.
 *
 * @author Simon Templer
 */
public class SaveProjectAdvisor extends AbstractIOAdvisor<ProjectWriter> implements IOAdvisorRegister {

  private final Map<String, IOAdvisor<?>> advisors = new HashMap<>();

  private final Project project;

  public SaveProjectAdvisor(Project project) {
    super();
    this.project = project;

    // use an empty alignment
    Alignment alignment = new DefaultAlignment();
    SchemaSpace sourceSchema = new DefaultSchemaSpace();
    SchemaSpace targetSchema = new DefaultSchemaSpace();

    advisors.put("eu.esdihumboldt.hale.io.align.write",
        new SaveAlignmentAdvisor(project, alignment, sourceSchema, targetSchema));
  }

  @Override
  public void prepareProvider(ProjectWriter provider) {
    provider.setProject(project);
  }

  @Override
  public void updateConfiguration(ProjectWriter provider) {
    provider.getProject().setModified(new Date());
    provider.getProject().setHaleVersion(HalePlatform.getCoreVersion());
    Map<String, ProjectFile> projectFiles = ProjectIO
        .createDefaultProjectFiles(this);

    for (ProjectFile pf : projectFiles.values()) {
      if (pf instanceof AdvisorProjectFile) {
        ((AdvisorProjectFile) pf).setAdvisorRegister(this);
      }
    }

    provider.setProjectFiles(projectFiles);
//    if (projectLoadLocation != null) {
//      provider.setPreviousTarget(projectLoadLocation);
//    }
  }

  @Override
  public IOAdvisor<?> findAdvisor(String actionId, ServiceProvider serviceProvider) {
    IOAdvisor<?> advisor = advisors.get(actionId);
    advisor.setServiceProvider(serviceProvider); // not sure if this is needed here
    return advisor;
  }

}
