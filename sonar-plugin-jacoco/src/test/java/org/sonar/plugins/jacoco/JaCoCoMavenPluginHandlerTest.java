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

import org.apache.maven.project.MavenProject;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.resources.Project;

public class JaCoCoMavenPluginHandlerTest {
  protected JaCoCoMavenPluginHandler handler;

  @Before
  public void before() {
    handler = new JaCoCoMavenPluginHandler();
  }


  @Test
  public void notFixedVersion() {
    // first of all, version was fixed : see http://jira.codehaus.org/browse/SONAR-1055
    // but it's more reasonable to let users fix the version : see http://jira.codehaus.org/browse/SONAR-1310
    assertThat(new JaCoCoMavenPluginHandler().isFixedVersion(), is(false));
  }

  @Test
  public void activateXmlFormat() {
    Project project = mock(Project.class);
    when(project.getPom()).thenReturn(new MavenProject());
    when(project.getExclusionPatterns()).thenReturn(new String[0]);

    MavenPlugin jacocoPlugin = new MavenPlugin(JaCoCoMavenPluginHandler.GROUP_ID, JaCoCoMavenPluginHandler.ARTIFACT_ID, null);
    handler.configure(project, jacocoPlugin);

    assertThat(jacocoPlugin.getParameter("formats/format"), is("xml"));
  }

  @Test
  public void setJaCoCoExclusions() {
    Project project = mock(Project.class);
    when(project.getPom()).thenReturn(new MavenProject());
    when(project.getExclusionPatterns()).thenReturn(new String[]{"**/Foo.java", "com/*Test.*", "com/*"});

    MavenPlugin jacocoPlugin = new MavenPlugin(JaCoCoMavenPluginHandler.GROUP_ID, JaCoCoMavenPluginHandler.ARTIFACT_ID, null);
    handler.configure(project, jacocoPlugin);

    assertThat(jacocoPlugin.getParameters("instrumentation/excludes/exclude")[0], is("**/Foo.class"));
    assertThat(jacocoPlugin.getParameters("instrumentation/excludes/exclude")[1], is("com/*Test.*"));
    assertThat(jacocoPlugin.getParameters("instrumentation/excludes/exclude")[2], is("com/*.class"));

  }
}
