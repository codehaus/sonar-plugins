/*
 * Copyright (C) 2010 Matthijs Galesloot
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.webscanner.html;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.Configuration;
import org.sonar.plugins.webscanner.html.FileSet.HtmlFile;

/**
 * Abstract superclass for all Validation services.
 *
 * @author Matthijs Galesloot
 * @since 0.1
 *
 */
public abstract class HtmlValidator {

  private static final Logger LOG = Logger.getLogger(HtmlValidator.class);

  private final HttpClient client;

  public HtmlValidator() {
    client = new HttpClient();
    if (Configuration.useProxy()) {
      client.getHostConfiguration().setProxy(Configuration.getProxyHost(), Configuration.getProxyPort());
    }
  }

  protected HttpClient getClient() {
    return client;
  }

  protected void executePostMethod(PostMethod post) {

    int retries = 3;

    for (int i = 0; i < retries; i++) {
      try {
        getClient().executeMethod(post);
        if (post.getStatusCode() == 200 || post.getStatusCode() == 302) {
          return;
        } else {
          LOG.warn("Bad http response: " + post.getStatusCode() + ", retrying after 1 second...");
          sleep(1000L);
        }
      } catch (UnknownHostException e) {
        if (Configuration.useProxy()) {
          LOG.warn("Unknown host, retry without proxy...");
          getClient().getHostConfiguration().setProxyHost(null);
          try {
            getClient().executeMethod(post);
          } catch (IOException e2) {
            throw new RuntimeException(e2);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected List<HtmlFile> randomSubset(List<HtmlFile> htmlFiles, Integer amount) {
    List<HtmlFile> newCollection = new ArrayList<HtmlFile>();
    for (int i = 0; i < amount && i < htmlFiles.size(); i++) {
      newCollection.add(htmlFiles.get(i));
    }
    return newCollection;
  }

  public abstract File reportFile(File file);

  protected void sleep(long sleepInterval) {
    try {
      Thread.sleep(sleepInterval);
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }

  public abstract void validateFile(File file, String url);

  /**
   * Validate a set of files using the service.
   */
  public void validateFiles(File folder) {
    FileSet fileSet = FileSet.fromXml(FileSet.getPath(folder));
    List<HtmlFile> files = removeDuplicates(fileSet.files);

    if (Configuration.getNrOfSamples() != null) {
      files = randomSubset(files, Configuration.getNrOfSamples());
    }

    int n = 0;
    for (HtmlFile file : files) {
      if (n++ > 0) {
        waitBetweenValidationRequests();
      }
      validateFile(new File(folder.getPath() + "/" + file.path), file.url);
    }
  }

  private List<HtmlFile> removeDuplicates(List<HtmlFile> list) {
    final List<HtmlFile> files = new ArrayList<HtmlFile>();
    for (HtmlFile file : list) {
      if (file.duplicateFile == null) {
        files.add(file);
      }
    }
    return files;
  }

  protected abstract void waitBetweenValidationRequests();
}
