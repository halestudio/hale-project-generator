package to.wetf.hale.progen

import groovy.transform.Canonical;
import groovy.transform.CompileStatic;

@CompileStatic
@Canonical
class XmlSchemaDescriptor {
  /**
   * Schema location
   */
  URI location
  /**
   * Schema namespace
   */
  String namespace
  /**
   * Namespace prefix
   */
  String namespacePrefix
}
