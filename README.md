# hale-project-generator

[![Build Status](https://travis-ci.org/wetransform-os/hale-project-generator.svg?branch=master)](https://travis-ci.org/wetransform/hale-project-generator)

Small Java library that provides an interface for generating HALE projects

### How to add to your application

The library is available in the wetransform artifactory.
You will need to configure specific Maven repositories for your build.
Following is an example configuration for Gradle:

```groovy
repositories {
  maven { // Geotools
    url 'http://download.osgeo.org/webdav/geotools/'
  }
  jcenter() // (or Maven Central)
  maven { // wetransform release repository (HALE releases and Eclipse dependencies)
    url 'https://artifactory.wetransform.to/artifactory/libs-release-local'
  }
  maven { // wetransform snapshot repository (HALE snapshots)
    url 'https://artifactory.wetransform.to/artifactory/libs-snapshot-local'
  }
  maven { // HALE artifactory (dependencies for HALE)
    url 'http://artifactory.esdi-humboldt.eu/artifactory/libs-release/'
  }
}
```

The latest version can be added as a dependency like this:

**Gradle**

```groovy
dependencies {
  compile 'to.wetransform:hale-project-generator:1.0.0'
}
```

**Maven**

```xml
<dependency>
    <groupId>to.wetransform</groupId>
    <artifactId>hale-project-generator</artifactId>
    <version>1.0.0</version>
</dependency>
```
