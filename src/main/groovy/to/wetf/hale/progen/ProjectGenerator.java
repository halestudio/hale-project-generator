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

package to.wetf.hale.progen;

import java.io.OutputStream;

import to.wetf.hale.progen.impl.ProjectGeneratorImpl;
import to.wetf.hale.progen.schema.SchemaDescriptor;

public interface ProjectGenerator {

  /**
   * Create a default project generator instance.
   *
   * @return a new project generator instance
   */
  static ProjectGenerator create() {
    return new ProjectGeneratorImpl();
  }

  /**
   * Generate a HALE project based on the provided source and target schemas.
   *
   * @param outProject the output stream to write the generated project to
   * @param sourceSchemas the source schemas
   * @param targetSchemas the target schemas
   * @param config the project configuration with various settings for project
   *   generation
   */
  public void generateProject(OutputStream outProject, Iterable<SchemaDescriptor> sourceSchemas,
      Iterable<SchemaDescriptor> targetSchemas, ProjectConfiguration config);

}
