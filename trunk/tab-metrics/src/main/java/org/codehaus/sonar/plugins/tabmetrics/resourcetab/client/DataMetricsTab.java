/*
 * Sonar Tab Metrics Plugin
 * Copyright (C) 2012 eXcentia
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.codehaus.sonar.plugins.tabmetrics.resourcetab.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.sonar.wsclient.gwt.AbstractCallback;
import org.sonar.wsclient.gwt.AbstractListCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Metric;
import org.sonar.wsclient.services.MetricQuery;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Data Metrics Tab
 * 
 */
public class DataMetricsTab {

  private final Map<String, List<MetricTab>> data;
  private final Resource resource;
  private final FlowPanel panel;

  /**
   * Constructor
   * 
   * @param resource
   * @param panel
   */
  public DataMetricsTab(Resource resource, FlowPanel panel) {
    data = new HashMap<String, List<MetricTab>>();
    this.resource = resource;
    this.panel = panel;

    // query to obtain all Metrics
    Sonar.getInstance().findAll(MetricQuery.all(), new TabMetricsListCallback());
  }

  /**
   * @return the data
   */
  public final Map<String, List<MetricTab>> getData() {
    return data;
  }

  /**
   * @return the resource
   */
  public final Resource getResource() {
    return resource;
  }

  /**
   * @return the panel
   */
  public final FlowPanel getPanel() {
    return panel;
  }

  /**
   * Installs a Callback to obtain all Metrics
   * 
   */
  class TabMetricsListCallback extends AbstractListCallback<Metric> {

    /**
     * Obtain the association of the metrics with their domains
     * 
     * @param metrics
     * @return a map with domain as a key and a list of metrics as his values
     */
    private Map<String, List<Metric>> obtainDomainMetrics(List<Metric> metrics) {

      Map<String, List<Metric>> domainMetrics = new HashMap<String, List<Metric>>();

      for (Metric metric : metrics) {
        String domain = metric.getDomain();

        if (domainMetrics.containsKey(domain)) {
          List<Metric> auxMetrics = domainMetrics.get(domain);
          auxMetrics.add(metric);
        } else {
          List<Metric> auxMetrics = new ArrayList<Metric>();
          auxMetrics.add(metric);
          domainMetrics.put(domain, auxMetrics);
        }
      }

      return domainMetrics;
    }

    @Override
    protected void doOnResponse(List<Metric> result) {

      // Associaties domain with metrics
      Map<String, List<Metric>> domainMetrics = obtainDomainMetrics(result);
      
      // Order map
      TreeMap<String, List<Metric>> orderDomainMetrics = new TreeMap<String, List<Metric>>(domainMetrics);
      
      // Entry Set
      Set<Entry<String, List<Metric>>> entries = orderDomainMetrics.entrySet();

      for (Entry<String, List<Metric>> entry : entries) {

        String domainName = entry.getKey();

        data.put(domainName, new ArrayList<MetricTab>());

        List<Metric> metricsList = entry.getValue();

        TabMetricsCallback callback = new TabMetricsCallback(domainName, metricsList);

        // elements in a part
        String[] metricKeys = new String[metricsList.size()];

        for (int i = 0; i < metricsList.size(); i++) {
          metricKeys[i] = metricsList.get(i).getKey();
        }

        // query to obtain the resource
        ResourceQuery resourceQuery = new ResourceQuery(getResource().getId()).setVerbose(true).setMetrics(metricKeys);

        // Find metrics with the resource
        Sonar.getInstance().find(resourceQuery, callback);
      }
    }
  }

  /**
   * Installs a Callback to obtain the Resource
   * 
   */
  class TabMetricsCallback extends AbstractCallback<Resource> {

    private String domainName;

    private List<Metric> metricsList;

    /**
     * Constructor
     * 
     * @param domain
     * @param metrics
     */
    public TabMetricsCallback(String domain, List<Metric> metrics) {
      super();
      metricsList = metrics;
      domainName = domain;
    }

    /**
     * @return the domainName
     */
    public final String getDomainName() {
      return domainName;
    }

    /**
     * @return the metricsList
     */
    public final List<Metric> getMetricsList() {
      return metricsList;
    }

    /**
     * @param domainName
     *          the domainName to set
     */
    public final void setDomainName(String domainName) {
      this.domainName = domainName;
    }

    /**
     * @param metricsList
     *          the metricsList to set
     */
    public final void setMetricsList(List<Metric> metricsList) {
      this.metricsList = metricsList;
    }

    @Override
    protected void doOnResponse(Resource resource) {

      List<MetricTab> metrics = new ArrayList<MetricTab>();

      for (Metric metric : metricsList) {
        if (metric != null) {
          Measure measure = resource.getMeasure(metric.getKey());
          if (measure != null && measure.getValue() != null) {
            MetricTab metricTab = new MetricTab(metric.getKey(), metric.getName(), metric.getDescription(), measure.getValue());
            metrics = data.get(metric.getDomain());
            metrics.add(metricTab);
          }
        }
      }

      // add panel
      if ( !metrics.isEmpty()) {
        panel.add(new SimpleHeaderTabMetrics(domainName, metrics));
      }
    }
  }
}
