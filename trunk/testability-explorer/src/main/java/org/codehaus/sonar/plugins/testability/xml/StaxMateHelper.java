package org.codehaus.sonar.plugins.testability.xml;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.plugins.api.maven.xml.XmlParserException;

public class StaxMateHelper {
  public static Double getDoubleValue(SMInputCursor cursor, String attributeName) {
    try {
      return Double.valueOf(cursor.getAttrIntValue(cursor.findAttrIndex(null, attributeName)));
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }

  public static String getStringValue(SMInputCursor cursor, String string) {
    try {
      return cursor.getAttrValue(cursor.findAttrIndex(null, string));
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }
}
