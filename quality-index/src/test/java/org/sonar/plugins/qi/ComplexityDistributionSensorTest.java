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
package org.sonar.plugins.qi;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SquidSearch;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.JavaFile;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceMethod;
import org.sonar.squid.api.Query;

import java.util.ArrayList;
import java.util.Collection;

public class ComplexityDistributionSensorTest {

  @Test
  public void testDependsUpon() {
    ComplexityDistributionSensor complexityDistributionSensor = new ComplexityDistributionSensor(null);
    assertThat(complexityDistributionSensor.dependsUpon().size(), is(1));
  }

  @Test
  public void testMeasureSaving() {
    SensorContext context = mock(SensorContext.class);
    Resource resource = new JavaFile("foo");

    RangeDistributionBuilder distrib = new RangeDistributionBuilder(QIMetrics.QI_COMPLEX_DISTRIBUTION, QIPlugin.COMPLEXITY_BOTTOM_LIMITS);
    ComplexityDistributionSensor complexityDistributionSensor = new ComplexityDistributionSensor(null);

    assertThat(distrib.build().getPersistenceMode(), is(PersistenceMode.FULL));
    complexityDistributionSensor.saveMeasure(context, new SourceFile("foo.java"), distrib);


    verify(context).saveMeasure(eq(resource), argThat(new BaseMatcher<Measure>() {
      public boolean matches(Object o) {
        Measure m = (Measure) o;
        assertThat(m.getPersistenceMode(), is(PersistenceMode.MEMORY));
        return StringUtils.isNotEmpty(m.getData());
      }

      public void describeTo(Description description) {
      }
    }));
  }

  @Test
  public void testNclocDistributionComputingForFile() {
    Number[] limits = QIPlugin.COMPLEXITY_BOTTOM_LIMITS;
    Collection<SourceCode> units = new ArrayList<SourceCode>();

    units.add(createMethod(22));
    units.add(createMethod(2));
    units.add(createMethod(2));
    units.add(createMethod(1));
    units.add(createMethod(36));
    units.add(createMethod(36));
    units.add(createMethod(46));
    units.add(createMethod(56));

    ComplexityDistributionSensor complexityDistributionSensor = new ComplexityDistributionSensor(new SquidSearchImpl(units));
    RangeDistributionBuilder distribution = complexityDistributionSensor.computeDistributionForFile(null, limits);

    Measure m = distribution.build();
    assertThat(m.getData(), is("1=1;2=2;10=0;20=1;30=4"));
    assertThat(m.getMetric(), is(QIMetrics.QI_COMPLEX_DISTRIBUTION));
  }

  private SourceMethod createMethod(int cc) {
    SourceMethod method = new SourceMethod("foo");
    method.setMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, cc);
    return method;
  }

  class SquidSearchImpl implements SquidSearch {
    private Collection<SourceCode> units;

    SquidSearchImpl(Collection<SourceCode> units) {
      this.units = units;
    }

    public Collection<SourceCode> search(Query... query) {
      return units;
    }

    public SourceCode search(String key) {
      return null;
    }
  }
}
