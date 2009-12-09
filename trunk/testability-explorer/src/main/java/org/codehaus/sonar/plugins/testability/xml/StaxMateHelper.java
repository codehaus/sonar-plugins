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
