# hale-project-generator

Small Java library that provides an interface for generating HALE projects

### How to add to your application

The library is available in the wetransform artifactory.
You will need to configure specific Maven repositories for your build.
Following is an example configuration for Gradle:

```groovy
repositories {
  maven { // Geotools
    url 'https://repo.osgeo.org/repository/release/'
  }
  mavenCentral()
  maven { // wetransform repository (HALE and Eclipse dependencies)
    url 'https://artifactory.wetransform.to/artifactory/local'
  }
}
```

It can be added as a dependency like this (replace `<#version>` by the desired version):

**Gradle**

```groovy
dependencies {
  compile 'to.wetransform:hale-project-generator:<#version>'
}
```

**Maven**

```xml
<dependency>
    <groupId>to.wetransform</groupId>
    <artifactId>hale-project-generator</artifactId>
    <version><#version></version>
</dependency>
```
