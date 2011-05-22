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
package org.sonar.plugins.web.markup.validation;


/**
 * MarkupError contains error information retrieved from the W3C error messages.
 *
 * @author Matthijs Galesloot
 * @since 0.1
 */
public class MarkupMessage {

  private Integer line;
  private String message;

  private String messageId;

  public Integer getLine() {
    return line;
  }

  public String getMessage() {
    return message;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setLine(Integer line) {
    this.line = line;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }
}
