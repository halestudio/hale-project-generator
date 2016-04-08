package to.wetf.hale.progen.impl

import java.nio.file.Files
import java.nio.file.Path

import org.apache.commons.io.FileUtils;

import groovy.transform.CompileStatic;;

@CompileStatic
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
