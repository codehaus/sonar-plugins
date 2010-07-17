package org.sonar.plugin.groovyGMNarc.groovy;

/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
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
 *
 */


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.sonar.plugin.groovyGMNarc.Groovy;

import java.io.File;
import java.util.List;

/**
 * Groovy Tester.
 *
 * A couple of simple sanity tests
 */
public class GMetricsResultsTest extends TestCase {

    Groovy gtest = null;
    public GMetricsResultsTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
     //   gtest = new Groovy();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     *
     * Method: getKey()
     *
     */
    public void testParseGMetricsFile() throws Exception {

      GMetricsResults gmr = new GMetricsResults();
      List<GMetricMetric> gMetricMetrics = null;
      gMetricMetrics = gmr.parseGMetricsFile("./test-data/Test_GMetrics.xml",true);
      assertNotNull(gMetricMetrics);
      List<String> uniquePackageList = null;
      uniquePackageList = gmr.getUniquePackageNames();
      assertNotNull(uniquePackageList);
    }




    public static Test suite() {
        return new TestSuite(GMetricsResultsTest.class);
    }
}