/*
 * Copyright 2018 wetransform GmbH
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

package to.wetf.hale.progen

import javax.xml.namespace.QName
import org.junit.Test

import to.wetf.hale.progen.impl.ProjectGeneratorImpl
import to.wetf.hale.progen.schema.SchemaDescriptor
import to.wetf.hale.progen.schema.xml.XmlSchemaDescriptor

import static org.junit.Assert.*


class ProjectGeneratorTest {

  private static final boolean DELETE_TEST_PROJECTS = true

  @Test
  void testInspire() {
    File projectFile = File.createTempFile('progen', '.halez')
    if (DELETE_TEST_PROJECTS) {
      projectFile.deleteOnExit()
    }

    // prepare list of relevant target types
    String NS_HYP = 'http://inspire.ec.europa.eu/schemas/hy-p/4.0'
    List<QName> targetTypes = [
      new QName(NS_HYP, 'WatercourseType'),
      new QName(NS_HYP, 'StandingWaterType')
    ]
    List<QName> sourceTypes = [
      new QName(NS_HYP, 'WatercourseType')
    ]

    // prepare list of schemas
    List<SchemaDescriptor> sourceSchemas = []
    sourceSchemas << new XmlSchemaDescriptor(URI.create(
      'http://inspire.ec.europa.eu/schemas/hy-p/4.0/HydroPhysicalWaters.xsd'), true)
    List<SchemaDescriptor> targetSchemas = []
    targetSchemas << new XmlSchemaDescriptor(URI.create(
      'http://inspire.ec.europa.eu/schemas/hy-p/4.0/HydroPhysicalWaters.xsd'), false)

    // prepare project configuration
    ProjectConfiguration config = new ProjectConfiguration()
    config.projectName = 'Map to INSPIRE HydroPhysicalWaters from itself'
    config.relevantTargetTypes = targetTypes
    config.relevantSourceTypes = sourceTypes

    projectFile.withOutputStream { outProject ->

      // create project generator
      ProjectGenerator gen = new ProjectGeneratorImpl()

      // run project generation
      gen.generateProject(outProject, sourceSchemas, targetSchemas, config)

    }

    println "Generated HALE project file ${projectFile.absolutePath}"

    assertTrue(projectFile.exists())
    assertTrue(projectFile.size() > 0)

    //TODO test generated project in some way?

  }

}
