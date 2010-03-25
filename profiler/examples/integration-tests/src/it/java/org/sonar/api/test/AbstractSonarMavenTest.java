package org.sonar.api.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Inspired by <a href="http://svn.sonatype.org/flexmojos/trunk/flexmojos-testing/flexmojos-test-harness">flexmojos-test-harness</a>.
 *
 * @author Evgeny Mandrikov
 */
public class AbstractSonarMavenTest {
  private static final ReadWriteLock copyProjectLock = new ReentrantReadWriteLock();

  protected static File projectsSource;

  protected static File projectsWorkdir;

  private static File mavenHome;

  private static Properties props;

  private static Properties profilerPropertis;

  @BeforeClass
  public static void init() throws IOException {
    if (props != null) {
      return;
    }
    props = new Properties();
    ClassLoader cl = AbstractSonarMavenTest.class.getClassLoader();
    InputStream is = cl.getResourceAsStream("baseTest.properties");
    if (is != null) {
      try {
        props.load(is);
      } finally {
        is.close();
      }
    }
    projectsSource = new File(getProperty("projects-source"));
    projectsWorkdir = new File(getProperty("projects-target"));
    mavenHome = new File(getProperty("fake-maven"));

    // Profiler configuration
    String jprofilerHome = getProperty("profiler-home");
    profilerPropertis = new Properties();
    profilerPropertis.setProperty("LD_LIBRARY_PATH", jprofilerHome + "/bin/linux-x86"); // TODO
    StringBuilder profilerOpts = new StringBuilder();
    profilerOpts.append("-agentlib:jprofilerti=offline,id=106,config=").append(getProperty("profiler-config")).append(' ');
    profilerOpts.append("-Xbootclasspath/a:").append(jprofilerHome).append("/bin/agent.jar");
    profilerPropertis.setProperty("MAVEN_OPTS", profilerOpts.toString());
  }

  protected static Verifier test(File projectDirectory) throws VerificationException {
    Verifier verifier = getVerifier(projectDirectory);
    // First of all we should compile project
    verifier.executeGoal("compile");
    // Execute sonar with profiler
    verifier.executeGoal("sonar:sonar", profilerPropertis);
    // TODO resave snapshot with proper name
    return verifier;
  }

  protected static synchronized String getProperty(String key) {
    return props.getProperty(key);
  }

  @SuppressWarnings({"unchecked"})
  protected static Verifier getVerifier(File projectDirectory) throws VerificationException {
    System.setProperty("maven.home", mavenHome.getAbsolutePath());

    Verifier verifier = new Verifier(projectDirectory.getAbsolutePath());
    List<String> options = verifier.getCliOptions();
//    options.add("--offline");
    options.add("--no-plugin-updates");
    options.add("--batch-mode");
    options.add("--debug");
    // TODO use local repo
//    verifier.getVerifierProperties().put( "use.mavenRepoLocal", "true" );
//    verifier.setLocalRepo( getProperty( "fake-repo" ) );

//    verifier.setLogFileName(getTestName() + ".log");
    verifier.setAutoclean(false);
    return verifier;
  }

  protected static File getProject(String projectName) throws IOException {
    copyProjectLock.writeLock().lock();
    try {
      File projectFolder = new File(projectsSource, projectName);
      Assert.assertTrue(
          "Project " + projectName + " folder not found.\n" + projectFolder.getAbsolutePath(),
          projectFolder.isDirectory()
      );

      File destDir = new File(projectsWorkdir, projectName); // TODO include testName
      FileUtils.copyDirectory(projectFolder, destDir, HiddenFileFilter.VISIBLE);
      return destDir;
    } finally {
      copyProjectLock.writeLock().unlock();
    }
  }
}
