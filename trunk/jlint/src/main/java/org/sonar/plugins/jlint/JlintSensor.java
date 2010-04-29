
package org.sonar.plugins.jlint;

import org.sonar.api.batch.AbstractViolationsStaxParser;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.utils.XmlParserException;

import javax.xml.stream.XMLStreamException;
import java.io.File;

public class JlintSensor implements Sensor, DependsUponMavenPlugin {

  private RulesProfile profile;
  private RulesManager rulesManager;
  private JlintMavenPluginHandler pluginHandler;

  public JlintSensor(RulesProfile profile, RulesManager rulesManager, JlintMavenPluginHandler pluginHandler) {
    this.profile = profile;
    this.rulesManager = rulesManager;
    this.pluginHandler = pluginHandler;
  }


  public void analyse(Project project, SensorContext context) {
    try {
      AbstractViolationsStaxParser parser = getStaxParser(context);
      File report = new File(project.getFileSystem().getBuildDir(), "jlint-violations.xml");
      parser.parse(report);

    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getFileSystem().hasJavaSourceFiles()
        && !profile.getActiveRulesByPlugin(JlintPlugin.KEY).isEmpty();
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    return pluginHandler;
  }

  private AbstractViolationsStaxParser getStaxParser(SensorContext context) {
    return new JlintViolationsXmlParser(context, profile, rulesManager);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}