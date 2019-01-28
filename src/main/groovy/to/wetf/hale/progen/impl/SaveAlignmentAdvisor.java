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

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Headless advisor for saving an alignment.
 *
 * @author Simon Templer
 */
public class SaveAlignmentAdvisor extends AbstractIOAdvisor<AlignmentWriter> {

  private final ProjectInfo projectInfo;
  private final Alignment alignment;
  private final SchemaSpace sourceSchema;
  private final SchemaSpace targetSchema;

  public SaveAlignmentAdvisor(ProjectInfo projectInfo, Alignment alignment, SchemaSpace sourceSchema,
      SchemaSpace targetSchema) {
    super();
    this.projectInfo = projectInfo;
    this.alignment = alignment;
    this.sourceSchema = sourceSchema;
    this.targetSchema = targetSchema;
  }

  @Override
  public void prepareProvider(AlignmentWriter provider) {
    super.prepareProvider(provider);

    provider.setTargetSchema(targetSchema);
    provider.setSourceSchema(sourceSchema);
    provider.setAlignment(alignment);
    if (provider instanceof ProjectInfoAware) {
      ProjectInfoAware aware = (ProjectInfoAware) provider;
      aware.setProjectInfo(projectInfo);
      // aware.setProjectLocation(projectLoadLocation);
    }
  }

}
