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

package to.wetf.hale.progen.impl

import eu.esdihumboldt.hale.common.core.HalePlatform
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.ImportProvider
import eu.esdihumboldt.hale.common.core.io.ResourceAdvisor
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor
import eu.esdihumboldt.hale.common.core.io.extension.ResourceAdvisorExtension
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.hale.common.core.io.project.model.Project
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.io.SchemaIO
import eu.esdihumboldt.util.io.InputSupplier
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.apache.commons.io.FilenameUtils
import org.eclipse.core.runtime.content.IContentType
import org.eclipse.equinox.nonosgi.registry.RegistryFactoryHelper
import org.osgi.framework.Version
import to.wetf.hale.progen.ProjectConfiguration
import to.wetf.hale.progen.ProjectGenerator
import to.wetf.hale.progen.XmlSchemaProjectGenerator
import to.wetf.hale.progen.schema.BundleMode
import to.wetf.hale.progen.schema.SchemaDescriptor
import to.wetf.hale.progen.schema.xml.XmlSchemaDescriptor
import to.wetf.hale.progen.schema.xml.XmlSchemaHelper
import to.wetf.hale.progen.schema.xml.XmlSchemaInfo

import javax.xml.XMLConstants
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@TypeChecked
class ProjectGeneratorImpl implements ProjectGenerator, XmlSchemaProjectGenerator {

  /**
   * Identifier of the XML schema reader.
   */
  private static final String XML_SCHEMA_READER_ID = 'eu.esdihumboldt.hale.io.xsd.reader'

  @Override
  public void generateProject(OutputStream outProject, Iterable<SchemaDescriptor> sourceSchemas,
      Iterable<SchemaDescriptor> targetSchemas, ProjectConfiguration config) {
    initHale()

    final GenerationContext context = new GenerationContext()
    try {

      // init project object
      final Project project = createProject(config)

      // source schemas
      project.resources.addAll(
        createSchemaConfigurations(sourceSchemas.toList(), context, SchemaSpaceID.SOURCE))

      // target schemas
      project.resources.addAll(
        createSchemaConfigurations(targetSchemas.toList(), context, SchemaSpaceID.TARGET))

      // save project
      saveProject(project, outProject, 'halez')

    } finally {
      context.cleanUp()
    }
  }

  @Override
  public void generateTargetXSDProject(OutputStream outProject, InputStream inTargetXSD, ProjectConfiguration config) {
    initHale()

    final GenerationContext context = new GenerationContext()
    try {

      // init project object
      final Project project = createProject(config)

      // save target schema to temporary file
      File tmpDir = context.createTempDir()
      File tmpSchema = new File(tmpDir, 'target.xsd')
      Files.copy(inTargetXSD, tmpSchema.toPath(), StandardCopyOption.REPLACE_EXISTING)
      def descriptor = new XmlSchemaDescriptor(tmpSchema.toURI(), BundleMode.REFERENCE)

      // target schema reader
      IOConfiguration schemaConf = createSchemaConfiguration(descriptor, context, SchemaSpaceID.TARGET)
      project.resources << schemaConf

      // save project
      saveProject(project, outProject, 'halez')

    } finally {
      context.cleanUp()
    }
  }

  @Override
  public void generateTargetXSDProject(OutputStream outProject, Iterable<XmlSchemaInfo> targetXSDs, ProjectConfiguration config) {
    generateProject(outProject,
      Collections.<SchemaDescriptor>emptyList(),
      targetXSDs.collect{ XmlSchemaInfo si -> (SchemaDescriptor) new XmlSchemaDescriptor(si.location, BundleMode.REFERENCE) },
      config)
  }

  // helper methods

  private void initHale() {
    // initialize registry
    RegistryFactoryHelper.getRegistry()
  }

  private Project createProject(ProjectConfiguration pc) {
    // create project
    final Project project = new Project()

    // basic medadata
    project.author = pc.projectAuthor ?: 'HALE (generated)'
    project.name = pc.projectName ?: 'Unnamed project'
    project.description = pc.projectDescription

    project.haleVersion = Version.parseVersion(HalePlatform.coreVersion.toString())

    project.created = new Date()
    project.modified = project.created

    // project configuration service
    ComplexConfigurationService config = ProjectIO.createProjectConfigService(project)

    // target mapping relevant types
    if (pc.relevantTargetTypes) {
      config.setList(SchemaIO.getMappingRelevantTypesParameterName(SchemaSpaceID.TARGET),
        pc.relevantTargetTypes.collect{ typeName ->
          typeName.toString()
        })
    }

    // source mapping relevant types
    if (pc.relevantSourceTypes) {
      config.setList(SchemaIO.getMappingRelevantTypesParameterName(SchemaSpaceID.SOURCE),
        pc.relevantSourceTypes.collect{ typeName ->
          typeName.toString()
        })
    }

    project
  }

  private List<IOConfiguration> createSchemaConfigurations(List<SchemaDescriptor> schemas,
    GenerationContext context, SchemaSpaceID ssid) {

    def results = []
    // separate into XML schemas and other schemas

    // XML schemas
    def xmlSchemas = schemas.findAll{it.isXmlSchema()}
    if (xmlSchemas) {
      if (xmlSchemas.size() > 1) {
        // create combined XML schema
        SchemaDescriptor schema = createCombinedXmlSchema(xmlSchemas, context)
        results << createSchemaConfiguration(schema, context, ssid)
      }
      else {
        results << createSchemaConfiguration(xmlSchemas[0], context, ssid)
      }
    }

    // not XML schemas
    schemas.findAll{!it.isXmlSchema()}.each { schema ->
      results << createSchemaConfiguration(schema, context, ssid)
    }

    results
  }

  private IOConfiguration createSchemaConfiguration(SchemaDescriptor schema,
    GenerationContext context, SchemaSpaceID ssid) {

    def result = schema.createIOConfiguration(ssid)

    switch (schema.bundleMode) {
      case BundleMode.REFERENCE:
        // reference schema URI
        result.providerConfiguration.put(ImportProvider.PARAM_SOURCE,
            Value.of(schema.location.toString()))
        break;
      default:
        // bundle schema w/ project (because local files will be included by default)

        def tempDir = context.createTempDir()

        String fileName = FilenameUtils.getName(schema.getLocation().getPath().toString())
        if (!fileName) {
          fileName = "file";
        }
        File newFile = new File(tempDir, fileName)
        Path target = newFile.toPath()

        boolean includeRemote = BundleMode.DEEP_COPY == schema.bundleMode

        def contentType = schema.getContentType()
        def reporter = new DefaultIOReporter(schema.getInputSupplier(), 'Copy resource', 'copy', false)
        ResourceAdvisor ra = ResourceAdvisorExtension.getInstance().getAdvisor(contentType)
        ra.copyResource(schema.getInputSupplier(), target, contentType, includeRemote, reporter)

        // reference copied file
        result.providerConfiguration.put(ImportProvider.PARAM_SOURCE,
            Value.of(newFile.toURI().toString()))
    }

    result
  }

  private SchemaDescriptor createCombinedXmlSchema(List<SchemaDescriptor> schemas, GenerationContext context) {
    boolean deep = schemas.any{ it.bundleMode == BundleMode.DEEP_COPY }

    def xmlSchemas = schemas.collect{ XmlSchemaHelper.loadInfo(it) }

    Collection<String> shortIds = xmlSchemas.findResults { it.namespacePrefix }
    def shortId
    if (shortIds) {
      shortId = shortIds.join('_')
    }
    else {
      shortId = UUID.randomUUID().toString()
    }
    String filename = "combined-${shortId}.xsd"
    File tempDir = context.createTempDir()
    File schemaFile = new File(tempDir, filename)

    createCombinedSchema(schemaFile, "http://esdi-humboldt.eu/hale/schema-combined-$shortId", xmlSchemas)

    def mode = deep ? BundleMode.DEEP_COPY : BundleMode.COPY
    new XmlSchemaDescriptor(schemaFile.toURI(), mode)
  }

  @TypeChecked(TypeCheckingMode.SKIP)
  private void createCombinedSchema(File file, targetNamespace, List<XmlSchemaInfo> schemas) {
    def xmlBuilder = new StreamingMarkupBuilder()
    def xml = xmlBuilder.bind {
      mkp.declareNamespace( xsd: XMLConstants.W3C_XML_SCHEMA_NS_URI )
      schemas.each { schema ->
        mkp.declareNamespace( (schema.namespacePrefix): schema.namespace )
      }
      'xsd:schema'(elementFormDefault: 'qualified', targetNamespace: targetNamespace) {
        schemas.each { schema ->
          'xsd:import'(namespace: schema.namespace, schemaLocation: schema.location)
        }
      }
    }

    file.withOutputStream {
      XmlUtil.serialize(xml, it)
    }
  }

  @TypeChecked(TypeCheckingMode.SKIP)
  private void saveProject(Project project, OutputStream outProject, String extension) {
    // write project
    InputSupplier<InputStream> input = null
    IContentType projectType = HaleIO.findContentType(ProjectWriter.class, input, "project.$extension");
    IOProviderDescriptor factory = HaleIO.findIOProviderFactory(ProjectWriter.class, projectType, null);
    ProjectWriter projectWriter;
    try {
      projectWriter = (ProjectWriter) factory.createExtensionObject();
    } catch (Exception e1) {
      throw new IllegalStateException("Failed to create project writer", e1);
    }
    projectWriter.setTarget(new WriteOnceOutputSupplier(outProject));

    // store (incomplete) save configuration
    IOConfiguration saveConf = new IOConfiguration();
    projectWriter.storeConfiguration(saveConf.getProviderConfiguration());
    saveConf.setProviderId(factory.getIdentifier());
    project.setSaveConfiguration(saveConf);

    SaveProjectAdvisor advisor = new SaveProjectAdvisor(project);
    // advisor.setServiceProvider(serviceProvider);
    advisor.prepareProvider(projectWriter)
    advisor.updateConfiguration(projectWriter)

    IOReport report = null;
    try {
      report = projectWriter.execute(null);
    } catch (Exception e) {
      throw new IllegalStateException("Error writing project file.", e)
    }
    if (report != null) {
      if (!report.isSuccess() || report.errors) {
        throw new IllegalStateException("Error writing project file.")
      }
    }
  }

}
