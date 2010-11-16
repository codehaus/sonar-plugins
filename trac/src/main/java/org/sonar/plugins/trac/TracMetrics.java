/*
 * Sonar Trac Plugin
 * Copyright (C) 2010 Thales Optronics Ltd.
 * dev@sonar.codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.sonar.plugins.trac;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class TracMetrics implements Metrics {

  public static final String DOMAIN = "Issues";

  public static final Metric ISSUES = new Metric("issues", "Trac issues", "Number of Trac issues", Metric.ValueType.INT,
      Metric.DIRECTION_NONE, false, DOMAIN);

  public static final Metric TICKET_COMPONENT = new Metric("ticket_component", "Project component", "Project component specified",
      Metric.ValueType.STRING, Metric.DIRECTION_NONE, false, DOMAIN);

  // getMetrics() method is defined in the Metrics interface and is used by
  // Sonar to retrieve the list of new Metric
  public final List<Metric> getMetrics() {
    return Arrays.asList(ISSUES, TICKET_COMPONENT);
  }
}
