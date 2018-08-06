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

package to.wetf.hale.progen.schema.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import to.wetf.hale.progen.schema.SchemaDescriptor;

/**
 * Helpers for XML schemas.
 *
 * @author Simon Templer
 */
public class XmlSchemaHelper {

  /**
   * Load XML schema information for a schema.
   *
   * @param schema the schema
   * @return the XML schema information
   * @throws IOException
   * @throws XMLStreamException
   */
  public static XmlSchemaInfo loadInfo(SchemaDescriptor schema) throws IOException, XMLStreamException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    String namespace = XMLConstants.NULL_NS_URI;
    String prefix = null;

    try (InputStream input = schema.getInputSupplier().getInput()) {
      XMLStreamReader reader = inputFactory.createXMLStreamReader(input);

      while (reader.hasNext()) {
        reader.next();

        if (reader.isStartElement()) {
          // found root element
          String ns = reader.getAttributeValue("", "targetNamespace");
          if (ns != null) {
            namespace = ns;
          }

          // find prefix
          for (int i = 0; i < reader.getNamespaceCount(); i++) {
            if (namespace.equals(reader.getNamespaceURI(i))) {
              prefix = reader.getNamespacePrefix(i);
            }
          }

          break;
        }
      }
    }

    //XXX canonical constructor fails to compile in Gradle -> use setters
    XmlSchemaInfo info = new XmlSchemaInfo();
    info.setLocation(schema.getLocation());
    info.setNamespace(namespace);
    info.setNamespacePrefix(prefix);
    return info;
  }

}
