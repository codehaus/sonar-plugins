/*
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.codehaus.sonar.plugins.testability.xml;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.utils.XmlParserException;

public final class StaxMateHelper {
  
  private StaxMateHelper() {
    // Utility Class
  }
  
  public static Double getDoubleValue(SMInputCursor cursor, String attributeName) {
    try {
      return Double.valueOf(cursor.getAttrIntValue(cursor.findAttrIndex(null, attributeName)));
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }

  public static String getStringValue(SMInputCursor cursor, String attributeName) {
    try {
      return cursor.getAttrValue(cursor.findAttrIndex(null, attributeName));
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }

  public static int getIntValue(SMInputCursor cursor, String attributeName) {
    try {
      return cursor.getAttrIntValue(cursor.findAttrIndex(null, attributeName));
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }
}
