package to.wetf.hale.progen.schema.xml

import groovy.transform.Canonical;
import groovy.transform.CompileStatic;

@CompileStatic
@Canonical
class XmlSchemaInfo {
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
