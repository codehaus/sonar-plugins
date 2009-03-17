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

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.sonar.commons.Metric;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.php.Php;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PhpDependResultsParserTest {

  private ProjectContext context;
  private PhpDependConfiguration config;

  @Before
  public void before() throws Exception {
    File xmlReport = new File(getClass().getResource("/org/sonar/plugins/phpdepend/PhpDependResultsParserTest/phpunit-report.xml").toURI());
    context = mock(ProjectContext.class);
    config = mock(PhpDependConfiguration.class);
    stub(config.getSourceDir()).toReturn(new File("C:\\projets\\_PHP\\Money"));
    stub(config.getReportFile(PhpDependConfiguration.PHPUNIT_OPT)).toReturn(xmlReport);

    Map<Metric, String> attributeByMetrics = new HashMap<Metric, String>();
    attributeByMetrics.put(CoreMetrics.NLOC, "ncloc");
    attributeByMetrics.put(CoreMetrics.COMMENT_LINES, "cloc");

    PhpDependResultsParser parser = new PhpDependResultsParser(config, context, attributeByMetrics);
    parser.parse();
  }

  @Test(expected = PhpDependExecutionException.class)
  public void shouldThrowAnExceptionWhenReportNotFound() {
    stub(config.getReportFile(PhpDependConfiguration.PHPUNIT_OPT)).toReturn(new File("path/to/nowhere"));
    PhpDependResultsParser parser = new PhpDependResultsParser(config, null);
    parser.parse();
  }

  @Test
  public void shouldGenerateProjectMeasures() {
    verify(context).addMeasure(CoreMetrics.NLOC, 517.0);
    verify(context).addMeasure(CoreMetrics.COMMENT_LINES, 251.0);
  }

  @Test
  public void shouldGenerateFileMeasures() {
    verify(context).addMeasure(Php.newFile("Money.php"), CoreMetrics.NLOC, 120.0);
    verify(context).addMeasure(Php.newFile("Money.php"), CoreMetrics.COMMENT_LINES, 68.0);
    verify(context).addMeasure(Php.newFile("Sources/MoneyBag.php"), CoreMetrics.NLOC, 195.0);
    verify(context).addMeasure(Php.newFile("Sources/MoneyBag.php"), CoreMetrics.COMMENT_LINES, 56.0);
    verify(context).addMeasure(Php.newFile("Sources/MoneyTest.php"), CoreMetrics.NLOC, 184.0);
    verify(context).addMeasure(Php.newFile("Sources/MoneyTest.php"), CoreMetrics.COMMENT_LINES, 71.0);
    verify(context).addMeasure(Php.newFile("Sources/Common/IMoney.php"), CoreMetrics.NLOC, 18.0);
    verify(context).addMeasure(Php.newFile("Sources/Common/IMoney.php"), CoreMetrics.COMMENT_LINES, 56.0);
  }

  @Test
  public void shouldGenerateDirectoryMeasures() {
    verify(context).addMeasure(Php.newDirectory("Sources"), CoreMetrics.NLOC, 379.0);
    verify(context).addMeasure(Php.newDirectory("Sources"), CoreMetrics.COMMENT_LINES, 127.0);
    verify(context).addMeasure(Php.newDirectory("Sources/Common"), CoreMetrics.NLOC, 18.0);
    verify(context).addMeasure(Php.newDirectory("Sources/Common"), CoreMetrics.COMMENT_LINES, 56.0);
  }

  @Test
  public void shouldNotThrowExceptionIfAMetricIsNotPresent() {
    Map<Metric, String> attributeByMetrics = new HashMap<Metric, String>();
    attributeByMetrics.put(new Metric("doesnt_exists"), "doesnt_exists");
    PhpDependResultsParser parser = new PhpDependResultsParser(config, context, attributeByMetrics);
    parser.parse();
  }

}
