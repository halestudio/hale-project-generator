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

package to.wetf.hale.progen.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

public class WriteOnceOutputSupplier implements LocatableOutputSupplier<OutputStream> {

  private final OutputStream out;

  public WriteOnceOutputSupplier(OutputStream out) {
    super();
    this.out = out;
  }

  @Override
  public OutputStream getOutput() throws IOException {
    return out;
  }

  @Override
  public URI getLocation() {
    return null;
  }

}
