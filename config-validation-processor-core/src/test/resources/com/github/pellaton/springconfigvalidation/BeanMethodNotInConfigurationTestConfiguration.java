/*
 * *****************************************************************************************************************
 * Copyright 2013 Stefan Ferstl
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
 *   Stefan Ferstl
 * *****************************************************************************************************************
 */
package com.github.pellaton.springconfigvalidation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Invalid configuration class not annotated with {@code @Configuration}.  
 * 
 * @author Stefan Ferstl
 * @see SpringConfigurationValidationProcessor
 */
public class BeanMethodNotInConfigurationTestConfiguration {
  
  @Bean
  public String someString() {
    return "HELLO JUNIT";
  }
}
