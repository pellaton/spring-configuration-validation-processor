## spring-configuration-validation-processor [![Build Status](https://travis-ci.org/pellaton/spring-configuration-validation-processor.png?branch=master)](https://travis-ci.org/pellaton/spring-configuration-validation-processor)

This project provides a [Java 6 Annotation processor](http://docs.oracle.com/javase/7/docs/api/javax/annotation/processing/package-summary.html) that emits compiler warnings and errors in case one of the following conditions is encountered in a Spring [@Configuration](http://docs.spring.io/spring/docs/3.2.4.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html) class:
- @Configuration classes must not be final.
- @Configuration classes must have a visible no-arg constructor.
- @Configuration class constructors must not be @Autowired.
- Nested @Configuration classes must be static.
- @Bean methods must not be private.
- @Bean methods must not be final.
- @Bean methods must have a non-void return type.
- @Bean methods should be declared in classes annotated with @Configuration.
- @Bean methods returning a BeanFactoryPostProcessor should be static.
- Only @Bean methods returning a BeanFactoryPostProcessor should be static.

##Quick Start
### Maven
1. Add the following dependency to your Maven POM:

  ``` xml
  <dependencies>
      <dependency>
        <groupId>com.github.pellaton.config-validation-processor</groupId>
        <artifactId>config-validation-processor-java17</artifactId>
        <!-- For Java 11: <artifactId>config-validation-processor-java11</artifactId> -->
        <!-- For Java 8: <artifactId>config-validation-processor-java8</artifactId> -->
        <!-- For Java 7: <artifactId>config-validation-processor-java7</artifactId> -->
        <version>3.0.7</version>
      </dependency>
  </dependencies>
  ```

1. Configure the maven-compiler-plugin to run the annotation processor:

  ``` xml
  <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.10.1</version>
            <configuration>
              <release>17</release>
              <annotationProcessors>
                <annotationProcessor>com.github.pellaton.springconfigvalidation.SpringConfigurationValidationProcessorJava17</annotationProcessor>
                <!-- For Java 11: <annotationProcessor>com.github.pellaton.springconfigvalidation.SpringConfigurationValidationProcessorJava11</annotationProcessor> -->
                <!-- For Java 8: <annotationProcessor>com.github.pellaton.springconfigvalidation.SpringConfigurationValidationProcessorJava8</annotationProcessor> -->
                <!-- For Java 7: <annotationProcessor>com.github.pellaton.springconfigvalidation.SpringConfigurationValidationProcessorJava7</annotationProcessor> -->
              </annotationProcessors>
            </configuration>
          </plugin>
      </plugins>
  </build>
  ```

### Gradle
Add the following to your gradle file:
  ```
configurations {
      annotationProcessor
}

task configureAnnotationProcessing(type: JavaCompile, group: 'build', description: 'Processes the @Configuration classes') {
    source = sourceSets.main.java
    classpath = configurations.compile + configurations.annotationProcessor
    options.compilerArgs = [
            "-proc:only",
            "-processor", "com.github.pellaton.springconfigvalidation.SpringConfigurationValidationProcessorJava8"
    ]
    destinationDir = buildDir
}

compileJava {
      dependsOn configureAnnotationProcessing
}

dependencies {
      annotationProcessor 'com.github.pellaton.config-validation-processor:config-validation-processor-java8:3.0.7'
}
```

### Eclipse
1. Enable annotation processing and annotation processing in editor in the Eclipse project properties (Java Compiler > Annotation Processing) 
![Screenshot](/img/annotationprocessing.png)
1. Configure the path to the processor's jar file (Java Compiler > Annotation Processing > Factory Path) 
![Screenshot](/img/factoryath.png)

### IntelliJ IDEA (Maven Project)
In IntelliJ IDEA, the annotation processor works out if the box in Maven projects configuring the processor in the compiler plugin configuration. Unfortunately, this does not work for Gradle projects :-/

### IntelliJ IDEA (Non Maven Project)
1. Add the jar file containing the annotation processor to the module libraries
1. Enable annotation processing in the global IntelliJ IDEAD compiler settings
1. Add the fully qualified class name of the processor to the annotation processors list 
![Screenshot](/img/intellijidea.png)

### Netbeans (Maven Project)
In Netbeans, the annotation processor works out if the box in Maven projects configuring the processor in the compiler plugin configuration.

### Netbeans (Non Maven Project)
1. Add the jar file containing the annotation processor to the project libraries
1. Enable annotation processing and annotation processing in editor in the project properties
1. Add the fully qualified class name of the processor to the annotation processors list 
![Screenshot](/img/netbeans.png)

# Perform a release
```$ mvn release:prepare release:perform -Darguments=-Dgpg.passphrase=SECRET```
