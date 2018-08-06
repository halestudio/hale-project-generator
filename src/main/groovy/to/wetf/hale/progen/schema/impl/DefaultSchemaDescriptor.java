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

package to.wetf.hale.progen.schema.impl;

import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.util.Pair;
import to.wetf.hale.progen.schema.BundleMode;
import to.wetf.hale.progen.schema.SchemaDescriptor;

/**
 * Default {@link SchemaDescriptor} implementation.
 *
 * @author Simon Templer
 */
public class DefaultSchemaDescriptor implements SchemaDescriptor {

  private BundleMode bundleMode;
  private URI location;
  private String readerId;

  public DefaultSchemaDescriptor(URI location) {
    this(location, BundleMode.REFERENCE, null);
  }

  public DefaultSchemaDescriptor(URI location, BundleMode bundleMode, String readerId) {
    super();
    this.bundleMode = bundleMode;
    this.location = location;

    if (readerId == null) {
      Pair<SchemaReader, String> res = HaleIO.findIOProviderAndId(SchemaReader.class,
          getInputSupplier(), location.getPath());
      readerId = res.getSecond();
    }

    this.readerId = readerId;
  }

  @Override
  public BundleMode getBundleMode() {
    return bundleMode;
  }

  @Override
  public URI getLocation() {
    return location;
  }

  @Override
  public String getReaderId() {
    return readerId;
  }

}
