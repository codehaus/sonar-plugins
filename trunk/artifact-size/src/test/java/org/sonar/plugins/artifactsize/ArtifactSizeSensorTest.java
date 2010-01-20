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
package org.sonar.plugins.artifactsize;

import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.apache.commons.configuration.Configuration;
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Build;
import static org.mockito.Mockito.*;

import java.io.File;

import static junit.framework.Assert.assertEquals;

public class ArtifactSizeSensorTest {

  @Test
  public void testGetSize() {
    File f = mock(File.class);
    ArtifactSizeSensor sensor = new ArtifactSizeSensor();

    when(f.length()).
      thenReturn(102400l, 0l);

    assertEquals(sensor.getSize(f), 100, 0.0);
    assertEquals(sensor.getSize(f), 0, 0.0);
  }

  @Test
  public void testSearchArtifactFileFromConfiguration() {
    Configuration configuration = mock(Configuration.class);
    Project project = new Project(null, configuration);

    when(project.getConfiguration().getString(ArtifactSizePlugin.ARTIFACT_PATH)).
      thenReturn("foo/bar");

    ArtifactSizeSensor sensor = new ArtifactSizeSensor();

    DefaultProjectFileSystem fileSystem = mock(DefaultProjectFileSystem.class);
    when(fileSystem.getBasedir()).
      thenReturn(new File("toto"));

    assertEquals(sensor.searchArtifactFile(project.getPom(), fileSystem, configuration), new File("toto/foo/bar"));
  }

  @Test
  public void testSearchArtifactFileFromPom() {
    Configuration configuration = mock(Configuration.class);
    when(configuration.getString(ArtifactSizePlugin.ARTIFACT_PATH)).
      thenReturn("");

    MavenProject pom = new MavenProject();
    pom.setPackaging("fii");
    pom.setBuild(mock(Build.class));
    when(pom.getBuild().getFinalName()).
      thenReturn("foo");

    DefaultProjectFileSystem fileSystem = mock(DefaultProjectFileSystem.class);
    when(fileSystem.getBuildDir()).
      thenReturn(new File("toto"));

    Project project = mock(Project.class);
    when(project.getFileSystem()).
      thenReturn(fileSystem);

    ArtifactSizeSensor sensor = new ArtifactSizeSensor();

    assertEquals(sensor.searchArtifactFile(pom, fileSystem, configuration), new File("toto/foo.fii"));
  }

  @Test
  public void testAnalyseWhenFileDoesNotExist() {
    SensorContext context = mock(SensorContext.class);
    Configuration configuration = mock(Configuration.class);
    Project project = mock(Project.class);

    when(project.getConfiguration()).
      thenReturn(configuration);
    when(configuration.getString(ArtifactSizePlugin.ARTIFACT_PATH)).
      thenReturn("foo/bar");

    DefaultProjectFileSystem fileSystem = mock(DefaultProjectFileSystem.class);

    when(project.getFileSystem()).
      thenReturn(fileSystem);

    when(fileSystem.getBasedir()).
      thenReturn(new File("toto"));
    
    new ArtifactSizeSensor().analyse(project, context);

    verify(context, never()).saveMeasure((Measure) anyObject());
  }
}
