/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Project;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ArtifactSizeSensorTest {

  @Test
  public void testGetSize() {
    File f = mock(File.class);
    ArtifactSizeSensor sensor = new ArtifactSizeSensor(new Settings());

    when(f.length()).
      thenReturn(102400l, 0l);

    assertEquals(sensor.getSize(f), 100, 0.0);
    assertEquals(sensor.getSize(f), 0, 0.0);
  }

  @Test
  public void testSearchArtifactFileFromConfiguration() {
    Settings settings = new Settings();
    settings.setProperty(ArtifactSizePlugin.ARTIFACT_PATH, "foo/bar");
    Project project = mock(Project.class);
    DefaultProjectFileSystem fileSystem = mock(DefaultProjectFileSystem.class);
    when(fileSystem.getBasedir()).
      thenReturn(new File("toto"));

    ArtifactSizeSensor sensor = new ArtifactSizeSensor(settings);

    assertEquals(sensor.searchArtifactFile(project.getPom(), fileSystem), new File("toto/foo/bar"));
  }

  @Test
  public void testSearchArtifactFileFromPom() {
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

    ArtifactSizeSensor sensor = new ArtifactSizeSensor(new Settings());

    assertEquals(sensor.searchArtifactFile(pom, fileSystem), new File("toto/foo.fii"));
  }

  @Test
  public void testAnalyseWhenFileDoesNotExist() {
    SensorContext context = mock(SensorContext.class);
    Project project = mock(Project.class);

    Settings settings = new Settings();
    settings.setProperty(ArtifactSizePlugin.ARTIFACT_PATH, "foo/bar");

    DefaultProjectFileSystem fileSystem = mock(DefaultProjectFileSystem.class);
    when(project.getFileSystem()).thenReturn(fileSystem);
    when(fileSystem.getBasedir()).thenReturn(new File("toto"));

    new ArtifactSizeSensor(settings).analyse(project, context);

    verify(context, never()).saveMeasure((Measure) anyObject());
  }
}
