/*
 * Sonar W3C Markup Validation Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.markup.constants;

/**
 * Constants for the W3C Markup Validation Service
 *
 * @author Matthijs Galesloot
 * @sicne 1.0
 */
public interface MarkupValidatorConstants {

  // name of parameter for validation URL
  String VALIDATION_URL = "sonar.w3cmarkup.url";

  // name of parameter for waiting time between validation requests
  String PAUSE_BETWEEN_VALIDATIONS = "sonar.w3cmarkup.pauseBetweenValidations";

  // the default URL for the online validation service
  String DEFAULT_URL = "http://validator.w3.org/check";

  // PROXY HOST
  String PROXY_HOST = "sonar.w3cmarkup.http.proxyHost";
  // PROXY PORT
  String PROXY_PORT = "sonar.w3cmarkup.http.proxyPort";
}
