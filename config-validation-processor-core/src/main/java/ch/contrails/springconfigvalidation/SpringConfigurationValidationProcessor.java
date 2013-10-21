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

import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Java 6 annotation processor that processes classes annotated with {@code @Configuration}. This processor is meant for
 * source validation during compilation only and does not write any output besides compiler messages. The following list
 * mentions all checks performed:
 *
 * <pre>
 * Class Checks
 * [x] Error: Invalid bean definition class: @Configuration classes must not be final.
 * [x] Error: Invalid bean definition class: @Configuration classes must have a visible no-arg constructor.
 * [x] Error: Invalid bean definition class: @Configuration class constructors must not be @Autowired.
 * [x] Error: Invalid bean definition class: Nested @Configuration classes must be static.
 * [ ] Error: Invalid bean definition class: @Configuration classes must not be local.
 *
 * Method Checks
 * [x] Error: Invalid factory method: @Bean methods must not be private.
 * [x] Error: Invalid factory method: @Bean methods must not be final.
 * [x] Error: Invalid factory method: @Bean methods must have a non-void return type.
 * [x] Warn:  @Bean methods returning a BeanFactoryPostProcessor should be static.
 * [x] Warn:  Only @Bean methods returning a BeanFactoryPostProcessor should be static.
 * </pre>
 *
 * @author Michael Pellaton
 *
 * @see <a href="http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/beans.html#beans-java">Spring Frameowrk Reference: Java-based container configuration</a>
 * @see <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/context/annotation/Configuration.html">Spring API Documentation: Configuration</a>
 * @see <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/context/annotation/Bean.html">Spring API Documentation: Bean</a>
 */
public abstract class SpringConfigurationValidationProcessor extends AbstractProcessor {

  private TypeElement autowiredTypeElement;
  private TypeElement beanTypeElement;
  private TypeElement bfppTypeElement;
  private TypeElement configurationTypeElement;

  private Messager messager;
  private Types typeUtils;
  private Elements elementUtils;


  /**
   * No-argument constructor.
   */
  public SpringConfigurationValidationProcessor() {
    super();
  }


  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    this.messager = processingEnv.getMessager();
    this.typeUtils = processingEnv.getTypeUtils();
    this.elementUtils = processingEnv.getElementUtils();

    this.autowiredTypeElement = this.elementUtils.getTypeElement(
        "org.springframework.beans.factory.annotation.Autowired");
    this.beanTypeElement = this.elementUtils.getTypeElement(
        "org.springframework.context.annotation.Bean");
    this.bfppTypeElement = this.elementUtils.getTypeElement(
        "org.springframework.beans.factory.config.BeanFactoryPostProcessor");
    this.configurationTypeElement = this.elementUtils.getTypeElement(
        "org.springframework.context.annotation.Configuration");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!roundEnv.errorRaised() && !roundEnv.processingOver()) {
      processRound(annotations, roundEnv);
    }
    return false;
  }


  private void processRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element instanceof TypeElement) {
          processElement((TypeElement) element);
        }
      }
    }
  }

  private void processElement(TypeElement typeElement) {
    for (AnnotationMirror annotation : typeElement.getAnnotationMirrors()) {
      Element annotationTypeElement = annotation.getAnnotationType().asElement();
      if (annotationTypeElement.equals(this.configurationTypeElement)) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();

        processClass(typeElement, ElementFilter.constructorsIn(enclosedElements));

        for (ExecutableElement method : ElementFilter.methodsIn(enclosedElements)) {
          processMethod(method);
        }
      }
    }
  }

  private void processClass(Element element, List<ExecutableElement> constructors) {
    processClassVisibility(element);
    processConstructors(element, constructors);
    processStaticClass(element);
  }

  private void processStaticClass(Element element) {
    if (isNestedClass(element) && !isStaticMethod(element)) {
      printMessage(SpringConfigurationMessage.NESTED_CLASS_NOT_STATIC, element);
    }
  }

  private boolean isNestedClass(Element element) {
    return element.getEnclosingElement().getKind().equals(ElementKind.CLASS);
  }

  private void processClassVisibility(Element element) {
    if (element.getModifiers().contains(Modifier.FINAL)) {
      printMessage(SpringConfigurationMessage.CLASS_FINAL, element);
    }
  }

  private void processConstructors(Element element, List<ExecutableElement> constructors) {
    boolean hasVisibleNoArgConsctructor = false;
    for (ExecutableElement constructor : constructors) {
      if (isVisibleElement(constructor) && hasNoParameter(constructor)) {
        hasVisibleNoArgConsctructor = true;
      }
      processAutowiredConstructor(constructor);
    }
    if (!hasVisibleNoArgConsctructor) {
      printMessage(SpringConfigurationMessage.MISSING_NO_ARG_CONSTRUCTOR, element);
    }
  }

  private void processAutowiredConstructor(ExecutableElement constructor) {
    List<? extends AnnotationMirror> annotations = constructor.getAnnotationMirrors();
    for (AnnotationMirror annotationMirror : annotations) {

      Element annotationTypeElement = annotationMirror.getAnnotationType().asElement();
      if (annotationTypeElement.equals(this.autowiredTypeElement)) {
        printMessage(SpringConfigurationMessage.AUTOWIRED_CONSTRUCTOR, constructor, annotationMirror);
      }
    }
  }

  private boolean hasNoParameter(ExecutableElement constructor) {
    return constructor.getParameters().isEmpty();
  }

  private boolean isVisibleElement(ExecutableElement constructor) {
    return !constructor.getModifiers().contains(Modifier.PRIVATE);
  }

  private void processMethod(ExecutableElement methodElement) {
    List<? extends AnnotationMirror> annotationMirrors = methodElement.getAnnotationMirrors();

    if (!annotationMirrors.isEmpty() && containsBeanAnnotation(annotationMirrors)) {
      processForScope(methodElement);
      processForFinal(methodElement);
      processForReturnType(methodElement);
      processForBFPP(methodElement);
    }
  }

  private boolean containsBeanAnnotation(List<? extends AnnotationMirror> annotationMirrors) {
    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (annotationMirror.getAnnotationType().asElement().equals(this.beanTypeElement)) {
        return true;
      }
    }
    return false;
  }

  private void processForFinal(ExecutableElement methodElement) {
    if (isFinalMethod(methodElement)) {
      printMessage(SpringConfigurationMessage.BEAN_METHOD_FINAL, methodElement);
    }
  }

  private void processForReturnType(ExecutableElement methodElement) {
    if (methodElement.getReturnType().getKind() == TypeKind.VOID) {
      printMessage(SpringConfigurationMessage.BEAN_METHOD_RETURNS_VOID, methodElement);
    }
  }

  private void processForBFPP(ExecutableElement methodElement) {
    boolean implementsBFPP = this.typeUtils.isAssignable(methodElement.getReturnType(), this.bfppTypeElement.asType());
    boolean isStaticMethod = isStaticMethod(methodElement);
    if (isStaticMethod) {
      if (!implementsBFPP) {
        printMessage(SpringConfigurationMessage.STATIC_BEAN_METHOD, methodElement);
      }
    } else {
      if (implementsBFPP) {
        printMessage(SpringConfigurationMessage.BFPP_BEAN_METHOD_NOT_STATIC, methodElement);
      }
    }
  }

  private void processForScope(Element methodElement) {
    if (isPrivateMethod(methodElement)) {
      printMessage(SpringConfigurationMessage.BEAN_METHOD_PRIVATE, methodElement);
    }
  }

  private boolean isPrivateMethod(Element methodElement) {
    return methodElement.getModifiers().contains(Modifier.PRIVATE);
  }

  private boolean isFinalMethod(Element methodElement) {
    return methodElement.getModifiers().contains(Modifier.FINAL);
  }

  private boolean isStaticMethod(Element methodElement) {
    return methodElement.getModifiers().contains(Modifier.STATIC);
  }

  private void printMessage(SpringConfigurationMessage message, Element element, AnnotationMirror annotationMirror) {
    this.messager.printMessage(message.getKind(), message.getMessage(), element, annotationMirror);
  }

  private void printMessage(SpringConfigurationMessage message, Element element) {
    printMessage(message, element, null);
  }
}
