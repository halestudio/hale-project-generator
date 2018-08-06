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
