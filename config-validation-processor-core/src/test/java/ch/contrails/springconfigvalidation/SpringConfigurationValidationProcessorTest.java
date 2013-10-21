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
package ch.contrails.springconfigvalidation;

import java.io.IOException;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;

import ch.contrails.springconfigvalidation.util.AnnotationProcessorTestCompiler;
import ch.contrails.springconfigvalidation.util.DiagnosticsAssert;

/**
 * Tests for {@link SpringConfigurationValidationProcessor}.
 *
 * @author Michael Pellaton
 *
 * @see SpringConfigurationValidationProcessor
 */
public class SpringConfigurationValidationProcessorTest {

  /**
   * Tests with a valid configuration class on which the processor should not emit any messages.
   */
  @Test
  public void validClass() throws IOException {
    compileAndAssertNoMessage("/ch/contrails/springconfigvalidation/ValidTestConfiguration");
  }

  /**
   * Tests the processor's detection of a final configuration class.
   */
  @Test
  public void finalClass() throws IOException {
    compileAndAssert("FinalClassTestConfiguration", SpringConfigurationMessage.CLASS_FINAL, 29);
  }

  /**
   * Tests the processor's detection of a constructor using @Autowired.
   */
  @Test
  public void autowiredConstructor() throws IOException {
    compileAndAssert("AutowiredConstructorConfiguration", SpringConfigurationMessage.AUTOWIRED_CONSTRUCTOR, 32);
  }

  /**
   * Tests the processor's detection of a missing no-argument constructor (the class has an accessible constructor
   * that takes arguments).
   */
  @Test
  public void missingNoArgConstructor() throws IOException {
    compileAndAssert("MissingNoArgConstructorConfiguration", SpringConfigurationMessage.MISSING_NO_ARG_CONSTRUCTOR, 29);
  }

  /**
   * Tests the processor's detection of a missing no-argument constructor (the class has a inaccessible no-argument
   * constructor).
   */
  @Test
  public void inaccessibleNoArgConstructor2() throws IOException {
    compileAndAssert("InaccessibleNoArgConstructorConfiguration", SpringConfigurationMessage.MISSING_NO_ARG_CONSTRUCTOR,
        29);
  }

  /**
   * Tests the processor's detection of non-{@code static} nested @Configuration classes.
   */
  @Test
  public void nestedClassNotStatic() throws IOException {
    compileAndAssert("NestedClassNotStaticTestConfiguration", SpringConfigurationMessage.NESTED_CLASS_NOT_STATIC, 32);
  }

  /**
   * Tests the processor's detection of a {@code private} bean factory method.
   */
  @Test
  public void privateBeanMethod() throws IOException {
    compileAndAssert("PrivateBeanMethodTestConfiguration", SpringConfigurationMessage.BEAN_METHOD_PRIVATE, 33);
  }

  /**
   * Tests the processor's detection of a bean factory method returning {@code void}.
   */
  @Test
  public void voidBeanMethod() throws IOException {
    compileAndAssert("VoidBeanMethodTestConfiguration", SpringConfigurationMessage.BEAN_METHOD_RETURNS_VOID, 33);
  }

  /**
   * Tests the processor's detection of a {@code final} bean factory method.
   */
  @Test
  public void finalBeanMethod() throws IOException {
    compileAndAssert("FinalBeanMethodTestConfiguration", SpringConfigurationMessage.BEAN_METHOD_FINAL, 33);
  }

  /**
   * Tests the processor's handling of a bean factory method having multiple valid annotations.
   */
  @Test
  public void multiAnnotationBeanMethod() throws IOException {
    compileAndAssertNoMessage("/ch/contrails/springconfigvalidation/MultiAnnotationBeanMethodTestConfiguration");
  }

  /**
   * Tests the processor's detection of a non-{@code static} bean factory method returning a
   * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}.
   */
  @Test
  public void bfppNotStaticBeanMethod() throws IOException {
    compileAndAssert("BFPPBeanMethodTestConfiguration", SpringConfigurationMessage.BFPP_BEAN_METHOD_NOT_STATIC, 35);
  }

  /**
   * Tests the processor's detection of a {@code static} bean factory method not returning a
   * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}.
   */
  @Test
  public void staticBeanMethod() throws IOException {
    compileAndAssert("StaticBeanMethodTestConfiguration", SpringConfigurationMessage.STATIC_BEAN_METHOD, 33);
  }


  private void compileAndAssert(String configurationClass, SpringConfigurationMessage expectedMessage,
      long expectedLineNumber) throws IOException {
    List<Diagnostic<? extends JavaFileObject>> diagnostics = AnnotationProcessorTestCompiler.compileClass(
        "/ch/contrails/springconfigvalidation/" + configurationClass, new TestSpringConfigurationValidationProcessor());
    DiagnosticsAssert.assertContainsSingleMessage(expectedMessage, expectedLineNumber, diagnostics);
  }

  private void compileAndAssertNoMessage(String configurationClass) throws IOException {
    List<Diagnostic<? extends JavaFileObject>> diagnostics =
        AnnotationProcessorTestCompiler.compileClass(configurationClass,
            new TestSpringConfigurationValidationProcessor());
    DiagnosticsAssert.assertNoCompilerMessage(diagnostics);
  }
}
