/**
 * Copyright (c) 2010 Compuware Corp.
 * Sonar Plugin JaCoCo, open source software Sonar plugin.
 * mailto:anthony.dahanne@compuware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, this permission notice and the below disclaimer shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. WITHOUT LIMITING THE FOREGOING, COMPUWARE MAKES NO REPRESENTATIONS OR WARRANTIES CONCERNING THE COMPLETENESS, ACCURACY OR OPERATION OF THE SOFTWARE.  CLIENT SHALL HAVE THE SOLE RESPONSIBILITY FOR ADEQUATE PROTECTION AND BACKUP OF ITS DATA USED IN CONNECTION WITH THE SOFTWARE.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sonar.plugins.jacoco;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenSurefireUtils;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.resources.Project;
/**
 * 
 * NO MAVEN PLUGIN AVAILABLE FOR JACOCO YET !
 *
 */
public class JaCoCoMavenPluginHandler implements MavenPluginHandler {

  public static final String GROUP_ID = MavenUtils.GROUP_ID_CODEHAUS_MOJO;
  public static final String ARTIFACT_ID = "jacoco-maven-plugin";

  public String getGroupId() {
    return GROUP_ID;
  }

  public String getArtifactId() {
    return ARTIFACT_ID;
  }

  public String getVersion() {
    return "0.1";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"jacoco"};
  }

  public void configure(Project project, MavenPlugin jacocoPlugin) {
    configureJaCoCo(project, jacocoPlugin);
    MavenSurefireUtils.configure(project);
  }

  private void configureJaCoCo(Project project, MavenPlugin jacocoPlugin) {
    jacocoPlugin.setParameter("formats/format", "xml");
    for (String pattern : project.getExclusionPatterns()) {
      if (pattern.endsWith(".java")) {
        pattern = StringUtils.substringBeforeLast(pattern, ".") + ".class";
        
      } else if (StringUtils.substringAfterLast(pattern, "/").indexOf(".")<0) {
        pattern = pattern + ".class";
      }
      jacocoPlugin.addParameter("instrumentation/excludes/exclude", pattern);
    }
  }
}
