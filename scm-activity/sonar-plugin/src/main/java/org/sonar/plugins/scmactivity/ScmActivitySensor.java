package org.sonar.plugins.scmactivity;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
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
      //Add all SCM providers we want to use
      scmManager.setScmProvider("svn", new SvnExeScmProvider());

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
    Date lastActivity = getLastActivity(result);
    if (lastActivity != null) {
      Measure lastActivityMeasure = new Measure(ScmActivityMetrics.LAST_ACTIVITY, formatLastActivity(lastActivity));
      context.saveMeasure(resource, lastActivityMeasure);
    }
  }

  protected void analyzeBlame(ExtScmManager scmManager, ScmRepository repository, File file, SensorContext context, Resource resource) throws ScmException {
    File basedir = file.getParentFile();
    String filename = file.getName();
    analyzeBlame(scmManager, repository, basedir, filename, context, resource);
  }

  private Date getLastActivity(BlameScmResult blame) {
    Date result = null;
    for (Date date : blame.getDates()) {
      if (result == null || result.before(date)) {
        result = date;
      }
    }
    return result;
  }

  public static String formatLastActivity(Date lastActivity) {
    SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);
    return sdf.format(lastActivity);
  }
}
