package to.wetf.hale.progen;

import java.io.InputStream;
import java.io.OutputStream;

import to.wetf.hale.progen.impl.ProjectGeneratorImpl;
import to.wetf.hale.progen.schema.xml.XmlSchemaInfo;

public interface XmlSchemaProjectGenerator {

  /**
   * Create a default project generator instance.
   *
   * @return a new project generator instance
   */
  static XmlSchemaProjectGenerator create() {
    return new ProjectGeneratorImpl();
  }

  /**
   * Generate a HALE project based on a provided target XML Schema.
   *
   * @param outProject the output stream to write the generated project to
   * @param inTargetXSD the input stream with the content of the single XSD file,
   *   the XSD may depend on external resources, if they are generally accessible
   * @param config the project configuration with various settings for project
   *   generation
   */
  public void generateTargetXSDProject(OutputStream outProject, InputStream inTargetXSD, ProjectConfiguration config);

  /**
   * Generate a HALE project based on a set of target XML Schemas.
   *
   * @param outProject the output stream to write the generated project to
   * @param targetXSDs descriptors of target XML Schemas at public locations,
   *   if there are multiple, the namespace and namespace prefix for each descriptor
   *   is mandatory
   * @param config the project configuration with various settings for project
   *   generation
   */
  public void generateTargetXSDProject(OutputStream outProject, Iterable<XmlSchemaInfo> targetXSDs, ProjectConfiguration config);

}
