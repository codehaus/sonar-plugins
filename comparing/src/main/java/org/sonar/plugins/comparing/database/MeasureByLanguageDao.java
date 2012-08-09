/*
 * Sonar Comparing Plugin
 * Copyright (C) 2012 David FRANCOIS
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
package org.sonar.plugins.comparing.database;

import org.sonar.plugins.comparing.dto.LanguageAggregateMeasureDTO;
import org.sonar.plugins.comparing.dto.LanguageGlobalMeasureDTO;

import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.MeasureData;
import org.sonar.api.database.model.MeasureModel;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MetricFinder;

import javax.persistence.Query;

import java.util.List;

public class MeasureByLanguageDao {

  private DatabaseSession session;
  private MetricFinder finder;

  public MeasureByLanguageDao(DatabaseSession session, MetricFinder finder) {
    this.session = session;
    this.finder = finder;
  }

  public List<LanguageGlobalMeasureDTO> getGlobalMeasureByLanguage() {
    return MeasureByLanguageMapper.mapGlobalResultsToDTOList(
        getMeasureByLanguage(RequestConstants.NB_LOC_AND_PROJECT_BY_LANGUAGE, CoreMetrics.NCLOC_KEY));
  }

  public List<LanguageAggregateMeasureDTO> getAggregateMeasureByLanguage(final String metricKey) {
    return MeasureByLanguageMapper.mapAggregateResultsToDTOList(
        getMeasureByLanguage(RequestConstants.METRIC_AVERAGE_BY_LANGUAGE, metricKey),
        finder.findByKey(metricKey));
  }

  public void saveMeasure(String metricKey, String data) {
    Integer metricId = finder.findByKey(metricKey).getId();
    MeasureModel model = session.getSingleResult(MeasureModel.class, "metricId", metricId);
    if (model == null) {
      model = new MeasureModel();

      MeasureData datas = new MeasureData();
      datas.setSnapshotId(null);
      datas.setMeasure(model);
      datas.setData(data.getBytes());

      model.setMetricId(metricId);
      model.setMeasureData(datas);
      session.save(model);
    } else {
      model.getMeasureData().setData(data.getBytes());
      session.merge(model);
    }
  }

  // ----------------------------------------------------------------------------------
  // | PRIVATE METHODS |
  // ----------------------------------------------------------------------------------

  private List<Object[]> getMeasureByLanguage(final String query, final String metricKey) {
    Query request = session.createQuery(query);
    request = request.setParameter("metricId", finder.findByKey(metricKey).getId());
    request = request.setParameter("last", Boolean.TRUE);
    return request.getResultList();
  }

}
