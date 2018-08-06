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
