/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 OTS SA
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
package org.sonar.plugins.thucydides;

import org.sonar.plugins.thucydides.utils.XmlFileFilter;
import org.sonar.plugins.thucydides.utils.XStreamFactory;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.plugins.thucydides.model.AcceptanceTestRun;
import org.sonar.plugins.thucydides.model.Feature;
import org.sonar.plugins.thucydides.model.UserStory;

public class ThucydidesResultSiteParser implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(ThucydidesResultSiteParser.class);
  private final XStream xstream = new XStreamFactory().createXStream();

  public ThucydidesReport parseThucydidesReports(final File reportsPath) {
    ThucydidesReport thucydidesReport = new ThucydidesReport();
    final File[] listOfFiles = reportsPath.listFiles(new XmlFileFilter());
    for (File file : listOfFiles) {
      try {
        thucydidesReport.addThucydidesReport(parseOneReport(new FileInputStream(file.getAbsolutePath())));
      } catch (FileNotFoundException ex) {
        LOG.error(ex.getLocalizedMessage());
      }
    }
    return thucydidesReport;
  }

  public ThucydidesReport parseOneReport(final InputStream reportFile) {
    final ThucydidesReport thucydidesReport = new ThucydidesReport();
    try {
      AcceptanceTestRun acceptanceTestRun = (AcceptanceTestRun) xstream.fromXML(reportFile);

      if (acceptanceTestRun != null) {
        if (acceptanceTestRun.getResult().equals("SUCCESS")) {
          thucydidesReport.setPassed(1);
        } else if (acceptanceTestRun.getResult().equals("PENDING")) {
          thucydidesReport.setPending(1);
        } else if (acceptanceTestRun.getResult().equals("FAILURE")) {
          thucydidesReport.setFailed(1);
        }
        thucydidesReport.setTests(1);
        thucydidesReport.setDuration(acceptanceTestRun.getDuration());
        List<UserStory> stories = new ArrayList<UserStory>();
        stories.add(acceptanceTestRun.getUserStory());
        thucydidesReport.addUserStories(stories);

        List<Feature> features = new ArrayList<Feature>();
        features.add(acceptanceTestRun.getUserStory().getFeature());
        thucydidesReport.addFeatures(features);
      }
    } catch (Exception ex) {
      LOG.error(ex.getLocalizedMessage());
    }
    return thucydidesReport;
  }
}
