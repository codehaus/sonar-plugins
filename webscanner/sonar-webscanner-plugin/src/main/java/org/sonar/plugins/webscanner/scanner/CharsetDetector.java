/*
 * Sonar Webscanner Plugin
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

package org.sonar.plugins.webscanner.scanner;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.syndication.io.XmlReader;

public class CharsetDetector {

  /**
   * Tries to get charset from {@code meta} tag.
   *
   */
  private static String getCharsetFromMetaTag(byte[] content) {
    try {
      String utf8string = new String(content, "UTF-8");

      Pattern metaRegexp = Pattern.compile("<meta\\s*[^>]*\\s*content=(\"|')?text/html;\\s+charset=([^\"';]+)(\"|'|;)?[^>]*>",
          Pattern.CASE_INSENSITIVE);
      Matcher matcher = metaRegexp.matcher(utf8string);

      if (matcher.find()) {
        return matcher.group(2);
      } else {
        return null;
      }
    } catch (Exception ex) {
      return null;
    }
  }

  public static String detect(File file) {
    try {
      String charset = getCharsetFromMetaTag(FileUtils.readFileToByteArray(file));

      if (charset == null) {
        return findCharset(file);
      } else {
        return charset;
      }
    } catch (IOException e) {
      return null;
    }
  }

  private static String findCharset(File file) {
    XmlReader reader = null;
    try {
      reader = new XmlReader(file);
      return reader.getEncoding();
    } catch (IOException e) {
      return null;
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

}
