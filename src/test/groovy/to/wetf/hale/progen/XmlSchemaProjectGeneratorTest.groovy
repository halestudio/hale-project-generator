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

package to.wetf.hale.progen

import javax.xml.namespace.QName
import org.junit.Test

import to.wetf.hale.progen.impl.ProjectGeneratorImpl
import to.wetf.hale.progen.schema.xml.XmlSchemaInfo

import static org.junit.Assert.*


class XmlSchemaProjectGeneratorTest {

  private static final boolean DELETE_TEST_PROJECTS = true

  @Test
  void testSchemaProvided() {
    URL schemaUrl = getClass().getClassLoader().getResource("schema/multischema1.xsd")

    File projectFile = File.createTempFile('progen', '.halez')
    if (DELETE_TEST_PROJECTS) {
      projectFile.deleteOnExit()
    }

    // prepare list of relevant target types
    String NS_EF = 'http://inspire.ec.europa.eu/schemas/ef/4.0'
    String NS_AM = 'http://inspire.ec.europa.eu/schemas/am/4.0'
    List<QName> targetTypes = [
      new QName(NS_EF, 'EnvironmentalMonitoringNetworkType'),
      new QName(NS_EF, 'EnvironmentalMonitoringFacilityType'),
      new QName(NS_AM, 'ManagementRestrictionOrRegulationZoneType')
    ]

    // prepare project configuration
    ProjectConfiguration config = new ProjectConfiguration()
    config.relevantTargetTypes = targetTypes

    projectFile.withOutputStream { outProject ->
      schemaUrl.openStream().withStream { inTargetXSD ->

        // create project generator
        XmlSchemaProjectGenerator gen = new ProjectGeneratorImpl()

        // run project generation
        gen.generateTargetXSDProject(outProject, inTargetXSD, config)

      }
    }

    println "Generated HALE project file ${projectFile.absolutePath}"

    assertTrue(projectFile.exists())
    assertTrue(projectFile.size() > 0)

    //TODO test generated project in some way?

  }

  @Test
  void testInspireSingle() {
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

    // prepare list of schemas
    List<XmlSchemaInfo> schemas = []
    schemas << new XmlSchemaInfo(
      location: URI.create('http://inspire.ec.europa.eu/schemas/hy-p/4.0/HydroPhysicalWaters.xsd'),
      namespace: NS_HYP,
      namespacePrefix: 'hy-p')

    // prepare project configuration
    ProjectConfiguration config = new ProjectConfiguration()
    config.projectName = 'Map to INSPIRE HydroPhysicalWaters'
    config.relevantTargetTypes = targetTypes

    projectFile.withOutputStream { outProject ->

      // create project generator
      XmlSchemaProjectGenerator gen = new ProjectGeneratorImpl()

      // run project generation
      gen.generateTargetXSDProject(outProject, schemas, config)

    }

    println "Generated HALE project file ${projectFile.absolutePath}"

    assertTrue(projectFile.exists())
    assertTrue(projectFile.size() > 0)

    //TODO test generated project in some way?

  }

  @Test
  void testInspireMulti() {
    File projectFile = File.createTempFile('progen', '.halez')
    if (DELETE_TEST_PROJECTS) {
      projectFile.deleteOnExit()
    }

    // prepare list of relevant target types
    String NS_HYP = 'http://inspire.ec.europa.eu/schemas/hy-p/4.0'
    String NS_HYN = 'http://inspire.ec.europa.eu/schemas/hy-n/4.0'
    List<QName> targetTypes = [
      new QName(NS_HYP, 'WatercourseType'),
      new QName(NS_HYP, 'StandingWaterType'),
      new QName(NS_HYN, 'WatercourseLinkType')
    ]

    // prepare list of schemas
    List<XmlSchemaInfo> schemas = []
    schemas << new XmlSchemaInfo(
      location: URI.create('http://inspire.ec.europa.eu/schemas/hy-p/4.0/HydroPhysicalWaters.xsd'),
      namespace: NS_HYP,
      namespacePrefix: 'hy-p')
    schemas << new XmlSchemaInfo(
      location: URI.create('http://inspire.ec.europa.eu/schemas/hy-n/4.0/HydroNetwork.xsd'),
      namespace: NS_HYN,
      namespacePrefix: 'hy-n')

    // prepare project configuration
    ProjectConfiguration config = new ProjectConfiguration()
    config.relevantTargetTypes = targetTypes

    projectFile.withOutputStream { outProject ->

      // create project generator
      XmlSchemaProjectGenerator gen = new ProjectGeneratorImpl()

      // run project generation
      gen.generateTargetXSDProject(outProject, schemas, config)

    }

    println "Generated HALE project file ${projectFile.absolutePath}"

    assertTrue(projectFile.exists())
    assertTrue(projectFile.size() > 0)

    //TODO test generated project in some way?

  }

}
