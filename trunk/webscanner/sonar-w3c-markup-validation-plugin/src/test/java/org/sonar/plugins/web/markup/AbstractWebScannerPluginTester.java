/*
 * Sonar W3C Markup Validation Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.markup;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.web.markup.language.HtmlConstants;
import org.sonar.plugins.web.markup.rules.DefaultMarkupProfile;
import org.sonar.plugins.web.markup.rules.MarkupRuleRepository;

/**
 *
 * @author Matthijs Galesloot
 *
 */
public class AbstractWebScannerPluginTester {

  protected static class MarkupRuleFinder implements RuleFinder {

    private final MarkupRuleRepository repository;
    private final List<Rule> rules;

    public MarkupRuleFinder() {
      repository = new MarkupRuleRepository();
      rules = repository.createRules();
    }

    public Rule find(RuleQuery query) {
      return null;
    }

    public Collection<Rule> findAll(RuleQuery query) {
      return null;
    }

    public Rule findByKey(String repositoryKey, String key) {
      for (Rule rule : rules) {
        if (rule.getKey().equals(key)) {
          return rule;
        }
      }
      return null;
    }

    public Rule findById(int ruleId) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  /**
   * create standard rules profile
   */
  protected RulesProfile createStandardRulesProfile() {
    ProfileDefinition profileDefinition = new DefaultMarkupProfile();
    ValidationMessages messages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(messages);
    assertEquals(0, messages.getErrors().size());
    assertEquals(0, messages.getWarnings().size());
    assertEquals(0, messages.getInfos().size());
    return profile;
  }

  private static MavenProject loadPom(File pomFile) throws URISyntaxException {

    FileReader fileReader = null;
    try {
      fileReader = new FileReader(pomFile);
      Model model = new MavenXpp3Reader().read(fileReader);
      MavenProject project = new MavenProject(model);
      project.setFile(pomFile);
      project.addCompileSourceRoot(project.getBuild().getSourceDirectory());

      return project;
    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomFile.getPath(), e);
    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }

  private static class MockFileSystem implements ProjectFileSystem {

    public ProjectFileSystem addSourceDir(File arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public ProjectFileSystem addTestDir(File arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public File getBasedir() {
      return new File("src/test/resources");
    }

    public File getBuildDir() {
      return new File(getBasedir(), "builddir");
    }

    public File getBuildOutputDir() {
      // TODO Auto-generated method stub
      return null;
    }

    public File getFileFromBuildDirectory(String arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public List<File> getJavaSourceFiles() {
      // TODO Auto-generated method stub
      return null;
    }

    public File getReportOutputDir() {
      // TODO Auto-generated method stub
      return null;
    }

    public File getSonarWorkingDirectory() {
      // TODO Auto-generated method stub
      return null;
    }

    public Charset getSourceCharset() {
      // TODO Auto-generated method stub
      return null;
    }

    public List<File> getSourceDirs() {
      // TODO Auto-generated method stub
      return null;
    }

    public List<File> getSourceFiles(Language... arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public List<File> getTestDirs() {
      // TODO Auto-generated method stub
      return null;
    }

    public List<File> getTestFiles(Language... arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public boolean hasJavaSourceFiles() {
      // TODO Auto-generated method stub
      return false;
    }

    public boolean hasTestFiles(Language arg0) {
      // TODO Auto-generated method stub
      return false;
    }

    public List<InputFile> mainFiles(String... arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public File resolvePath(String arg0) {
      return new File(getBasedir(), arg0);
    }

    public List<InputFile> testFiles(String... arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public Resource toResource(File arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    public File writeToWorkingDirectory(String arg0, String arg1) throws IOException {
      // TODO Auto-generated method stub
      return null;
    }
  }

  protected Project loadProjectFromPom(File pomFile) throws Exception {
    MavenProject pom = loadPom(pomFile);
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId()).setPom(pom).setConfiguration(
        new MapConfiguration(pom.getProperties()));
    project.setFileSystem(new MockFileSystem());
    project.setPom(pom);
    project.setLanguageKey(HtmlConstants.KEY);
    project.setLanguage(new Language() {

      public String getName() {
        return "Web";
      }

      public String getKey() {
        return HtmlConstants.KEY;
      }

      public String[] getFileSuffixes() {
        return HtmlConstants.DEFAULT_SUFFIXES;
      }
    });

    return project;
  }
}
