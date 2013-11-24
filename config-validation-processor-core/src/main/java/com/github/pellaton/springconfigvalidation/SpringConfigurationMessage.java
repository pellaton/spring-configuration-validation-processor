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
package com.github.pellaton.springconfigvalidation;

import javax.tools.Diagnostic.Kind;

/**
 * Enum containing all the diagnostic messages that the {@link SpringConfigurationValidationProcessor} emits.
 *
 * @author Michael Pellaton
 *
 * @see SpringConfigurationValidationProcessor
 * @see javax.tools.Diagnostic
 */
public enum SpringConfigurationMessage {

  // errors on @Configuration classes
  NESTED_CLASS_NOT_STATIC(Kind.ERROR,
      "Invalid bean definition class: Nested @Configuration classes must be static."),
  CLASS_FINAL(Kind.ERROR,
      "Invalid bean definition class: @Configuration classes must not be final."),
  MISSING_NO_ARG_CONSTRUCTOR(Kind.ERROR,
      "Invalid bean definition class: @Configuration classes must have a visible no-arg constructor."),
  AUTOWIRED_CONSTRUCTOR(Kind.ERROR,
      "Invalid bean definition class: @Configuration class constructors must not be @Autowired."),

  // errors on @Bean methods
  BEAN_METHOD_FINAL(Kind.ERROR,
      "Invalid factory method: @Bean methods must not be final."),
  BEAN_METHOD_RETURNS_VOID(Kind.ERROR,
      "Invalid factory method: @Bean methods must have a non-void return type."),
  BEAN_METHOD_PRIVATE(Kind.ERROR, "Invalid factory method: @Bean methods must not be private."),
  BEAN_METHOD_NOT_IN_CONFIGURATION(Kind.ERROR,
      "Invalid factory method: @Bean methods must be declared in classes annotated with @Configuration."),

  // warnings on @Bean methods
  STATIC_BEAN_METHOD(Kind.WARNING,
      "Only @Bean methods returning a BeanFactoryPostProcessor should be static."),
  BFPP_BEAN_METHOD_NOT_STATIC(Kind.WARNING,
      "@Bean methods returning a BeanFactoryPostProcessor should be static.");


  private final Kind kind;
  private final String message;


  private SpringConfigurationMessage(Kind kind, String message) {
    this.kind = kind;
    this.message = message;
  }


  /**
   * Gets the diagnostic kind of this message.
   *
   * @return the diagnostic kind of this message
   * @see Kind
   */
  public Kind getKind() {
    return this.kind;
  }

  /**
   * Gets the message to be displayed.
   *
   * @return the message to be displayed
   */
  public String getMessage() {
    return this.message;
  }
}
