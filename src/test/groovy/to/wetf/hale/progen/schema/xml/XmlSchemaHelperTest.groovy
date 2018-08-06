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

package to.wetf.hale.progen.schema.xml

import org.eclipse.equinox.nonosgi.registry.RegistryFactoryHelper
import org.junit.BeforeClass
import org.junit.Test

import to.wetf.hale.progen.schema.impl.DefaultSchemaDescriptor

/**
 * Tests for {@link XmlSchemaHelper}
 *
 * @author Simon Templer
 */
class XmlSchemaHelperTest {

  @BeforeClass
  static void init() {
    // initialize registry
    RegistryFactoryHelper.getRegistry()
  }

  @Test
  void testNamespacePrefix1() {
    def loc = URI.create('https://inspire.ec.europa.eu/schemas/au/4.0/AdministrativeUnits.xsd')
    def schema = new DefaultSchemaDescriptor(loc)

    def info = XmlSchemaHelper.loadInfo(schema)

    assert loc == info.location
    assert 'http://inspire.ec.europa.eu/schemas/au/4.0' == info.namespace
    assert 'au' == info.namespacePrefix
  }

}
