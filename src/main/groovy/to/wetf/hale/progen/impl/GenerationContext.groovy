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

package to.wetf.hale.progen.impl

import groovy.transform.TypeChecked
import org.apache.commons.io.FileUtils

import java.nio.file.Files
import java.nio.file.Path

@TypeChecked
class GenerationContext {

  private final List<Path> tempFiles = []

  File createTempDir() {
    Path path = Files.createTempDirectory('hale-progen')
    path.toFile()
  }

  void cleanUp() {
    tempFiles.each { path ->
      FileUtils.deleteDirectory(path.toFile())
    }
  }

}
