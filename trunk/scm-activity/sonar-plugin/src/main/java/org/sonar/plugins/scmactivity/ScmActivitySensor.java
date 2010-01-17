package org.sonar.plugins.scmactivity;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensor implements Sensor {
  public boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed if scm connection is defined
    return project.getPom().getScm() != null;
  }

  public void analyse(Project project, SensorContext context) {
    Logger log = LoggerFactory.getLogger(getClass());

    ProjectFileSystem fileSystem = project.getFileSystem();
    List<File> sourceDirs = fileSystem.getSourceDirs();

    try {
      ExtScmManager scmManager = new ExtScmManager();
      // Add all SCM providers we want to use
      scmManager.setScmProvider("svn", new SvnExeScmProvider());
      scmManager.setScmProvider("git", new GitExeScmProvider());

      String connectionUrl = project.getPom().getScm().getConnection();
      log.info("SCM connection URL: {}", connectionUrl);
      ScmRepository repository = scmManager.makeScmRepository(connectionUrl);

      List<File> files = fileSystem.getJavaSourceFiles();
      for (File file : files) {
        log.info("Analyzing {}", file.getAbsolutePath());
        Resource resource = JavaFile.fromIOFile(file, sourceDirs, false);
        analyzeBlame(scmManager, repository, file, context, resource);
      }
    } catch (ScmException e) {
      throw new RuntimeException(e);
    }
  }

  protected void analyzeBlame(ExtScmManager scmManager, ScmRepository repository, File basedir, String filename, SensorContext context, Resource resource) throws ScmException {
    BlameScmResult result = scmManager.blame(repository, new ScmFileSet(basedir), filename);
    // TODO check result.isSuccess()

    Date lastActivity = null;
    String lastRevision = null;

    PropertiesBuilder<Integer, String> authorsBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_AUTHORS_DATA);
    PropertiesBuilder<Integer, String> datesBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_DATE_DATA);
    PropertiesBuilder<Integer, String> revisionsBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_REVISION_DATA);

    List<BlameLine> lines = result.getLines();
    for (int i = 0; i < lines.size(); i++) {
      BlameLine line = lines.get(i);
      Date date = line.getDate();
      String revision = line.getRevision();
      String author = line.getAuthor();

      datesBuilder.add(i, formatLastActivity(date));
      revisionsBuilder.add(i, revision);
      authorsBuilder.add(i, author);

      if (lastActivity == null || lastActivity.before(date)) {
        lastActivity = date;
        lastRevision = revision;
      }
    }

    if (lastActivity != null) {
      context.saveMeasure(resource, authorsBuilder.build());
      context.saveMeasure(resource, datesBuilder.build());
      context.saveMeasure(resource, revisionsBuilder.build());

      Measure lastRevisionMeasure = new Measure(ScmActivityMetrics.REVISION, lastRevision);
      context.saveMeasure(resource, lastRevisionMeasure);

      Measure lastActivityMeasure = new Measure(ScmActivityMetrics.LAST_ACTIVITY, formatLastActivity(lastActivity));
      context.saveMeasure(resource, lastActivityMeasure);
    }
  }

  protected void analyzeBlame(ExtScmManager scmManager, ScmRepository repository, File file, SensorContext context, Resource resource) throws ScmException {
    File basedir = file.getParentFile();
    String filename = file.getName();
    analyzeBlame(scmManager, repository, basedir, filename, context, resource);
  }

  public static String formatLastActivity(Date lastActivity) {
    SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);
    return sdf.format(lastActivity);
  }
}
