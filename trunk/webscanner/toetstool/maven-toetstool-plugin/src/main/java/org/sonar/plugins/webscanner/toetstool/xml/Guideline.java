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

package org.sonar.plugins.webscanner.toetstool.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Matthijs Galesloot
 * @since 0.1
 */
@XStreamAlias("guideline")
public class Guideline {

  public static enum ValidationType {
    error, info, ok, unknown, warning
  }

  @XStreamAsAttribute
  private String ref;

  @XStreamAsAttribute
  private String reflink;

  private String remark;

  @XStreamAsAttribute
  private ValidationType type;

  public String getRef() {
    return ref;
  }

  public String getReflink() {
    return reflink;
  }

  public String getRemark() {
    return remark;
  };

  public ValidationType getType() {
    return type;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public void setReflink(String reflink) {
    this.reflink = reflink;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public void setType(ValidationType type) {
    this.type = type;
  }
}
