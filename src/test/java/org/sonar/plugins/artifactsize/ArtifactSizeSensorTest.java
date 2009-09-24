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
