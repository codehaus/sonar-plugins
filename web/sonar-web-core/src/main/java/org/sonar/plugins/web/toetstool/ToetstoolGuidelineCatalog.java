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

package org.sonar.plugins.web.toetstool;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Parse Toetstool page with the list of Guidelines.
 *
 * @author Matthijs Galesloot
 * @since 0.2
 */
public final class ToetstoolGuidelineCatalog {

  public class MessageDefinition {

    private String id;
    private String remark;

    public String getId() {
      return id;
    }

    public String getRemark() {
      return remark;
    }
  }

  public static void main(String[] args) throws IOException {
    new ToetstoolGuidelineCatalog().createRulesCatalog();
  }

  private final List<MessageDefinition> errors;

  private ToetstoolGuidelineCatalog() throws IOException {
    errors = new ArrayList<MessageDefinition>();
    readErrorCatalog();
  }

  /**
   * method to generate rules.xml for use in WebPlugin.
   */
  private void createRulesCatalog() {

    FileWriter writer = null;
    try {
      writer = new FileWriter("markup-errors.xml");
      writer.write("<rules>");
      for (MessageDefinition error : errors) {
        String remark = StringEscapeUtils.escapeXml(error.remark);
        String id = error.id;
        String explanation = null;
        writer.write(String.format("<rule><key>%s</key><remark>%s:%s</remark><priority>MAJOR</priority>"
            + "<explanation>%s</explanation></rule>\n", id, id, remark, explanation == null ? "" : explanation));
      }
      writer.write("</rules>");
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  private void readErrorCatalog() throws IOException {

    InputStream in = ToetstoolGuidelineCatalog.class.getClassLoader().getResourceAsStream("org/sonar/plugins/web/toetstool/toetstool.txt");

    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    Properties properties = new Properties();
    properties.load(ToetstoolGuidelineCatalog.class.getClassLoader().getResourceAsStream("org/sonar/plugins/web/toetstool/guidelines.txt"));

    // find errors with explanation
    String key;
    while ((key = reader.readLine()) != null) {

      MessageDefinition error = new MessageDefinition();
      error.id = key;
      error.remark = properties.getProperty(key);
      errors.add(error);
    }
  }
}
