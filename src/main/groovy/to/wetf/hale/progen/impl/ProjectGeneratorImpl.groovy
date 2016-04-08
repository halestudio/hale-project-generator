package to.wetf.hale.progen.impl

import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.ImportProvider
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.io.SchemaIO
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil;

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption;
import java.util.List

import javax.xml.XMLConstants;
import javax.xml.namespace.QName

import org.eclipse.core.runtime.content.IContentType
import org.eclipse.equinox.nonosgi.registry.RegistryFactoryHelper;
import org.osgi.framework.Version
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import to.wetf.hale.progen.ProjectConfiguration
import to.wetf.hale.progen.ProjectGenerator
import to.wetf.hale.progen.SchemaDescriptor

@CompileStatic
class ProjectGeneratorImpl implements ProjectGenerator {
  
  /**
   * Identifier of the XML schema reader.
   */
  private static final String XML_SCHEMA_READER_ID = 'eu.esdihumboldt.hale.io.xsd.reader'
  
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
      SchemaDescriptor targetInfo = new SchemaDescriptor(location: tmpSchema.toURI()) 
      
      // target schema reader
      IOConfiguration schemaConf = createSchemaConfiguration(
        Collections.singletonList(targetInfo), context)
      project.resources << schemaConf
      
      // save project
      saveProject(project, outProject, 'halez')
    
    } finally {
      context.cleanUp()
    }
  }
  
  @Override
  public void generateTargetXSDProject(OutputStream outProject, Iterable<SchemaDescriptor> targetXSDs, ProjectConfiguration config) {
    initHale()
    
    final GenerationContext context = new GenerationContext()
    try {
      
      // init project object
      final Project project = createProject(config)
      
      // target schema reader
      List<SchemaDescriptor> schemas = []
      targetXSDs.each { schemas << it }
      IOConfiguration schemaConf = createSchemaConfiguration(schemas, context)
      project.resources << schemaConf
      
      // save project
      saveProject(project, outProject, 'halez')
    
    } finally {
      context.cleanUp()
    }
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
    
    project.haleVersion = Version.parseVersion('2.9.0') //XXX possible to determine?
    
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
    
    project
  }
  
  private IOConfiguration createSchemaConfiguration(List<SchemaDescriptor> schemas,
      GenerationContext context) {
    IOConfiguration result = new IOConfiguration()
    result.actionId = SchemaIO.ACTION_LOAD_TARGET_SCHEMA
    result.providerId = XML_SCHEMA_READER_ID

    if (schemas.size() == 1) {
      result.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
          Value.of(schemas[0].location.toString()))
    }
    else {
      // create combined schema

      List<String> shortIds = schemas.collect { it.namespacePrefix }
      def shortId = shortIds.join('_')
      String filename = "combined-${shortId}.xsd"
      File tempDir = context.createTempDir()
      File schemaFile = new File(tempDir, filename)
      
      createCombinedSchema(schemaFile, "http://esdi-humboldt.eu/hale/schema-combined-$shortId", schemas)

      result.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
          Value.of(schemaFile.toURI()))
    }

    result
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  private void createCombinedSchema(File file, targetNamespace, List<SchemaDescriptor> schemas) {
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
  
  private void saveProject(Project project, OutputStream outProject, String extension) {
    // write project
    IContentType projectType = HaleIO.findContentType(ProjectWriter.class, null, "project.$extension");
    IOProviderDescriptor factory = HaleIO.findIOProviderFactory(ProjectWriter.class, projectType, null);
    ProjectWriter projectWriter;
    try {
      projectWriter = (ProjectWriter) factory.createExtensionObject();
    } catch (Exception e1) {
      throw new IllegalStateException("Failed to create project writer", e1);
    }
    projectWriter.setProject(project);
    projectWriter.setProjectFiles(new HashMap<String, ProjectFile>());
    projectWriter.setTarget(new WriteOnceOutputSupplier(outProject));

    // store (incomplete) save configuration
    IOConfiguration saveConf = new IOConfiguration();
    projectWriter.storeConfiguration(saveConf.getProviderConfiguration());
    saveConf.setProviderId(factory.getIdentifier());
    project.setSaveConfiguration(saveConf);

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
