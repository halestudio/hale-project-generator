package to.wetf.hale.progen

import java.util.List;

import javax.xml.namespace.QName

import groovy.transform.Canonical;
import groovy.transform.CompileStatic;

@CompileStatic
@Canonical
class ProjectConfiguration {
  /**
   * The name of the project.
   */
  String projectName
  /**
   * The name of the project author.
   */
  String projectAuthor
  /**
   * The project description.
   */
  String projectDescription

  /**
   * The names of the source schema types that are relevant for the mapping.
   */
  List<QName> relevantSourceTypes = []

  /**
   * The names of the target schema types that are relevant for the mapping.
   */
  List<QName> relevantTargetTypes = []
}
