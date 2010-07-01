/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource SA
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
package org.sonar.plugins.twitter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * @author Evgeny Mandrikov
 */
public class TwitterPublisher implements PostJob {

  public void executeOn(Project project, SensorContext context) {
    Configuration configuration = project.getConfiguration();
    String hostUrl = configuration.getString("sonar.host.url", "http://localhost:9000");
    String username = configuration.getString(TwitterPlugin.USERNAME_PROPERTY);
    String password = configuration.getString(TwitterPlugin.PASSWORD_PROPERTY);

    if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
      return;
    }

    String url = hostUrl + "/project/index/" + project.getKey();

    String status = String.format("%s analyzed: %s", project.getName(), url);

    updateStatus(username, password, status);
  }

  public void updateStatus(String username, String password, String status) {
    Logger logger = LoggerFactory.getLogger(getClass());
    logger.info("Updating Twitter status to: '{}'", status);
    TwitterFactory factory = new TwitterFactory();
    Twitter twitter = factory.getInstance(username, password);
    try {
      twitter.updateStatus(status);
    } catch (TwitterException e) {
      logger.warn("Exception updating Twitter status", e);
    }
  }

}
