/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.jmeter.xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class AssertionResult {

  @XStreamAsAttribute
  private Boolean error;

  @XStreamAsAttribute
  private Boolean failure;

  @XStreamAsAttribute
  private String failureMessage;

  @XStreamAsAttribute
  private String name;

  public Boolean getError() {
    return error;
  }

  public Boolean getFailure() {
    return failure;
  }

  public String getFailureMessage() {
    return failureMessage;
  }

  public String getName() {
    return name;
  }
}
