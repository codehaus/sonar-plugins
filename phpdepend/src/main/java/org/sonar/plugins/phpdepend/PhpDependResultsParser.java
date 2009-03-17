/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
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

package org.sonar.plugins.phpdepend;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.commons.Metric;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XmlParserException;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.php.Php;

import javax.xml.stream.XMLStreamConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhpDependResultsParser {

  private static final Logger LOG = LoggerFactory.getLogger(PhpDependExecutor.class);

  private PhpDependConfiguration config;
  private ProjectContext context;
  private List<String> sourcesDir;
  private Map<Metric, Bag> directoryBagByMetrics;
  private Map<Metric, String> attributeByMetrics;

  public PhpDependResultsParser(PhpDependConfiguration config, ProjectContext context) {
    this.config = config;
    this.context = context;
    this.sourcesDir = Arrays.asList(config.getSourceDir().getAbsolutePath());
    directoryBagByMetrics = new HashMap<Metric, Bag>();
    initAttributeByMetrics();
  }

  private void initAttributeByMetrics() {
    attributeByMetrics = new HashMap<Metric, String>();
    attributeByMetrics.put(CoreMetrics.NLOC, "ncloc");
    attributeByMetrics.put(CoreMetrics.COMMENT_LINES, "cloc");
  }

  public void parse() {
    File reportXml = config.getReportFile(PhpDependConfiguration.PHPUNIT_OPT);
    if (!reportXml.exists()) {
      throw new PhpDependExecutionException("Result file not found : " + reportXml.getAbsolutePath());
    }
    try {
      LOG.info("Collecting measures...");
      collectMeasures(reportXml);
    } catch (Exception e) {
      throw new XmlParserException(e);
    }
  }

  protected void collectMeasures(File reportXml) throws Exception {
    XMLInputFactory2 xmlFactory = (XMLInputFactory2) XMLInputFactory2.newInstance();
    InputStream input = new FileInputStream(reportXml);
    XMLStreamReader2 reader = (XMLStreamReader2) xmlFactory.createXMLStreamReader(input);

    while (reader.next() != XMLStreamConstants.END_DOCUMENT) {
      if (reader.isStartElement()) {
        String elementName = reader.getLocalName();
        if (elementName.equals("metrics")) {
          collectProjectMeasures(reader);
        } else if (elementName.equals("file")) {
          collectFileMeasures(reader);
        }
      }
    }
    reader.closeCompletely();

    collectDirectoryMeasures();
  }

  private void collectProjectMeasures(XMLStreamReader2 reader) throws ParseException {
    for (Map.Entry<Metric, String> attributeByMetric : attributeByMetrics.entrySet()) {
      Metric metric = attributeByMetric.getKey();
      String attribute = attributeByMetric.getValue();
      String value = reader.getAttributeValue(null, attribute);
      context.addMeasure(metric, MavenCollectorUtils.parseNumber(value));
    }
  }

  private void collectFileMeasures(XMLStreamReader2 reader) throws ParseException {
    String name = reader.getAttributeValue(null, "name");
    Resource file = Php.newFileFromAbsolutePath(name, sourcesDir);

    for (Map.Entry<Metric, String> attributeByMetric : attributeByMetrics.entrySet()) {
      Metric metric = attributeByMetric.getKey();
      String attribute = attributeByMetric.getValue();

      String value = reader.getAttributeValue(null, attribute);
      context.addMeasure(file, metric, extractValue(value));
      addMetricValueToParent(file, value, metric);
    }
  }

  private void addMetricValueToParent(Resource file, String value, Metric metric) {
    Resource parent = new Php().getParent(file);
    if (parent != null) {
      int val = Integer.parseInt(value);
      Bag directoryBag = directoryBagByMetrics.get(metric);
      if (directoryBag == null) {
        directoryBag = new HashBag();
        directoryBagByMetrics.put(metric, directoryBag);
      }
      directoryBag.add(parent, val);
    }
  }

  private void collectDirectoryMeasures() throws ParseException {
    for (Map.Entry<Metric, Bag> directoryBagByMetric : directoryBagByMetrics.entrySet()) {
      Metric metric = directoryBagByMetric.getKey();
      Bag directoryBag = directoryBagByMetric.getValue();
      for (Object o : directoryBag.uniqueSet()) {
        Resource directory = (Resource) o;
        Integer value = directoryBag.getCount(directory);
        context.addMeasure(directory, metric, extractValue(value));
      }
    }
  }

  private double extractValue(String value) throws ParseException {
    return MavenCollectorUtils.parseNumber(value);
  }

  private double extractValue(Integer value) throws ParseException {
    return MavenCollectorUtils.parseNumber(Integer.toString(value));
  }

}
