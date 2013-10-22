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

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.github.pellaton.springconfigvalidation.SpringConfigurationValidationProcessor;


/**
 * {@link SpringConfigurationValidationProcessor} for Java 6 runtimes.
 * 
 * @author Michael Pellaton
 * 
 * @see SpringConfigurationValidationProcessor
 */
@SupportedAnnotationTypes(value = "org.springframework.context.annotation.Configuration")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SpringConfigurationValidationProcessorJava6 extends SpringConfigurationValidationProcessor {

}
