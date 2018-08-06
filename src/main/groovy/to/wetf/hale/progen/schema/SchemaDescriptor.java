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

package to.wetf.hale.progen.schema;

import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;

public interface SchemaDescriptor {

  /**
   * Identifier of the XML schema reader.
   */
  static final String XML_SCHEMA_READER_ID = "eu.esdihumboldt.hale.io.xsd.reader";

  /**
   * States if the location from {@link #getLocation()} should be used to
   * reference/load the schema (instead of bundling the schema with the project).
   *
   * @return if the location should be used to reference/load the schema
   */
  BundleMode getBundleMode();

  /**
   * Get the (external) location of the schema.
   *
   * @return the location of the schema
   */
  URI getLocation();

  /**
   * Get the ID of the reader that should be used to load the schema.
   *
   * @return the identifier of the schema reader
   */
  String getReaderId();

  /**
   * Create an I/O configuration to load the schema.
   *
   * @return the I/O configuration for loading the schema
   */
  default IOConfiguration createIOConfiguration(SchemaSpaceID schemaSpace) {
    IOConfiguration result = new IOConfiguration();

    result.setProviderId(getReaderId());

    if (schemaSpace != null) {
      switch (schemaSpace) {
      case SOURCE:
        result.setActionId(SchemaIO.ACTION_LOAD_SOURCE_SCHEMA);
        break;
      case TARGET:
        result.setActionId(SchemaIO.ACTION_LOAD_TARGET_SCHEMA);
        break;
      }
    }

    //TODO later add/allow options

    return result;
  }

  /**
   * Get an input supplier for reading the schema file.
   *
   * @return an input supplier for the schema
   */
  default LocatableInputSupplier<InputStream> getInputSupplier() {
    return new DefaultInputSupplier(getLocation());
  }

  /**
   * States if the schema is an XML schema.
   *
   * @return <code>true</code> if the schema is an XML schema, <code>false</code> otherwise
   */
  default boolean isXmlSchema() {
    return XML_SCHEMA_READER_ID.equals(getReaderId());
  }

  /**
   * Get the associated content type.
   *
   * @return the schema file content type
   */
  default IContentType getContentType() {
    return HaleIO.findContentType(SchemaReader.class, getInputSupplier(), getLocation().getPath());
  }

}
