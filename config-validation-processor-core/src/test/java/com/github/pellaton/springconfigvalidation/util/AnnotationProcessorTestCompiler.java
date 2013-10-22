/*
 * *****************************************************************************************************************
 * Copyright 2012 Michael Pellaton
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * Contributors:
 *   Michael Pellaton
 * *****************************************************************************************************************
 */
package com.github.pellaton.springconfigvalidation.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.springframework.core.io.ClassPathResource;

/**
 * Utility class that compiles a Java class using the {@link Compiler} and an annotation {@link Processor}. This class
 * is intended to be used for tests of annotation processors.
 *
 * @author Michael Pellaton
 */
public final class AnnotationProcessorTestCompiler {

  private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
  private static final Iterable<String> COMPILER_OPTIONS = Collections.singletonList("-proc:only");


  /**
   * Avoid instantiation.
   */
  private AnnotationProcessorTestCompiler() {
    throw new AssertionError("Not instantiable.");
  }


  /**
   * Processes the java class specified. This implementation only parses and processes the java classes and does not
   * fully compile them - i.e. it does not write class files back to the disk. Basically, {@code javac} is called with
   * {@code -proc:only}.
   *
   * @param classToCompile the Java class to compile
   * @param processor the annotation {@link Processor} to use during compilation
   * @return a list of {@link Diagnostic} messages emitted during the compilation
   */
  public static List<Diagnostic<? extends JavaFileObject>> compileClass(String classToCompile, Processor processor)
      throws IOException {

    DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();

    StandardJavaFileManager fileManager = null;
    try {
      fileManager = getFileManager(collector);
      Iterable<? extends JavaFileObject> compilationUnits = getCompilationUnitOfClass(fileManager, classToCompile);

      CompilationTask task = COMPILER.getTask(null, fileManager, collector, COMPILER_OPTIONS, null, compilationUnits);
      task.setProcessors(Arrays.asList(processor));
      task.call();

      return collector.getDiagnostics();
    } finally {
      if (fileManager != null) {
        fileManager.close();
      }
    }
  }

  private static StandardJavaFileManager getFileManager(DiagnosticCollector<JavaFileObject> diagnosticCollector) {
    return COMPILER.getStandardFileManager(diagnosticCollector, Locale.getDefault(), null);
  }

  private static Iterable<? extends JavaFileObject> getCompilationUnitOfClass(StandardJavaFileManager fileManager,
      String classToCompile) throws IOException {
    ClassPathResource resource = new ClassPathResource(classToCompile + ".java");
    return fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(resource.getFile()));
  }
}
