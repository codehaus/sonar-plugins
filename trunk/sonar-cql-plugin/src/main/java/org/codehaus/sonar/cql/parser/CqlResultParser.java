package org.codehaus.sonar.cql.parser;

import static java.util.Locale.ENGLISH;
import static org.sonar.api.utils.ParsingUtils.parseNumber;
import static org.sonar.api.utils.ParsingUtils.scaleValue;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

/**
 * @since 2.4
 */
public class CqlResultParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(CqlResultParser.class);
  public void parseReport(File xmlFile, final SensorContext context) {
    try {
      LOGGER.info("xdepend parsing [{}]",xmlFile);
      StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {

        public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
          try {
            rootCursor.advance();
            parseCQlGroups(rootCursor.descendantElementCursor("Group"), context);
          } catch (ParseException e) {
            throw new XMLStreamException(e);
          }
        }
      });
      parser.parse(xmlFile);
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }
  
  private void parseCQlGroups(SMInputCursor cqlGroup, SensorContext context) throws ParseException, XMLStreamException {
    while (cqlGroup.getNext() != null) {
      LOGGER.info("parsing CQL Group [{}] with status [{}]",cqlGroup.getAttrValue("Name"),cqlGroup.getAttrValue("Status"));
      parseCQlQueries(cqlGroup.descendantElementCursor("Query"), context);
    }
  }
  
  private void parseCQlQueries(SMInputCursor cqlQuery, SensorContext context) throws ParseException, XMLStreamException {
    while (cqlQuery.getNext() != null) {
      LOGGER.info("parsing CQL Query [{}] with status [{}]",cqlQuery.getAttrValue("Name"),cqlQuery.getAttrValue("Status"));
    }
  }


  
}
