/**
 * 
 */
package com.echosource.ada.gnat.metric;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Project;

import com.echosource.ada.Ada;

/**
 * @author Akram Ben Aissi
 * 
 */
public class GnatConfiguration implements BatchExtension {

  private static final String GNAT_ANALYZE_ONLY_KEY = "sonar.dynamicAnalysis";
  private static final Boolean GNAT_ANALYZE_ONLY_DEFAULT = Boolean.FALSE;

  private static final String GNAT_EXECUTABLE_KEY = "sonar.ada.gnat.executable";
  private static final String GNAT_DEFAULT_EXECUTABLE_NAME = "gnat";

  private static final String GNAT_EXCLUDE_PACKAGE_KEY = "sonar.ada.gnat.exclude.modifier";
  private static final String GNAT_DEFAULT_EXCLUDE_PACKAGE_MODIFIER = null;

  private static final String GNAT_XML_OUTPUT_MODIFIER_KEY = "sonar.ada.xml.output.modifier";
  private static final String GNAT_DEFAULT_XML_OUTPUT_MODIFIER = "-x";

  private static final String GNAT_XML_OUTPUT_FILE_MODIFIER_KEY = "sonar.ada.xml.output.file.modifier";
  private static final String GNAT_DEFAULT_XML_OUTPUT_FILE_MODIFIER = "-ox";
  private static final String GNAT_DEFAULT_DO_NOT_GENERATE_TEXT_MODIFIER = "-nt";
  private static final String GNAT_DO_NOT_GENERATE_TEXT_MODIFIER_KEY = "sonar.ada.do.not.generate.modifier";

  private static final String GNAT_DIRECTORY_SEPARATOR = ",";
  private static final String GNAT_SOURCE_DIRECTORIES = "";
  private static final String GNAT_REPORT_FILE_RELATIVE_PATH_KEY = "sonar.ada.report.directory";
  private static final String GNAT_DEFAULT_REPORT_FILE_RELATIVE_PATH = "/logs";

  private static final String GNAT_XML_OUTPUT_FILE_KEY = "sonar.ada.report.file";
  private static final String GNAT_DEFAULT_XML_OUTPUT_FILE = "metrics.xml";

  private static final String GNAT_DEFAULT_DEFAULT_ARGUMENT = "metric";
  private static final String GNAT_DEFAULT_ARGUMENT_KEY = "sonar.ada.default.argument";
  private static final String GNAT_DEFAULT_IGNORE_DIRECTORY_MODIFIER = "";
  private static final String GNAT_IGNORE_DIRECTORY_MODIFIER_KEY = "sonar.ada.ignore.directory.modifier";
  private static final String GNAT_EXCLUDE_PACKAGES_MODIFIER_KEY = "sonar.ada.exclude.package.modifier";
  private static final String GNAT_DEFAULT_EXCLUDE_PACKAGES_MODIFIER = null;
  private static final String GNAT_DEFAULT_IGNORED_DIRECTORIES = null;
  private static final String GNAT_IGNORED_DIRECTORIES_KEY = "sonar.ada.ignored.directories";

  private static final String GNAT_AUTO_INCLUDE_SOURCE_DIRS_KEY = "sonar.ada.automaticaly.include.source.directories";
  private static final Boolean GNAT_AUTO_INCLUDE_SOURCE_DIRS_DEFAULT = Boolean.TRUE;

  private static final String GNAT_COMPILER_ARGUMENT_MODIFIER_KEY = "sonar.ada.compiler.argument.modifier";
  private static final String GNAT_DEFAULT_COMPILER_ARGUMENT_MODIFIER = "-cargs";

  private static final String GNAT_COMPILER_EXTRA_ARGUMENTS_KEY = "sonar.ada.compiler.extra.arguments";

  private static final String GNAT_INCLUDE_DIRECTORIES_KEY = "sonar.ada.include.directories";
  private static final String GNAT_INCLUDE_DIRECTORY_MODIFIER_KEY = "sonar.ada.include.directory.modifier";
  private static final String GNAT_DEFAULT_INCLUDE_DIRECTORY_MODIFIER = "-I";

  private static final String GNAT_INCLUDE_LIBRARIES_KEY = "sonar.ada.include.libraries";
  private static final String GNAT_INCLUDE_LIBRARY_MODIFIER_KEY = "sonar.ada.include.library.modifier";
  private static final String GNAT_DEFAULT_INCLUDE_LIBRARY_MODIFIER = "-L";

  protected Project project;
  protected Configuration configuration;

  /**
   * Default constructor.
   * 
   * @param project
   */
  public GnatConfiguration(Project project) {
    this.project = project;
    this.configuration = project.getConfiguration();
  }

  /**
   * Gets the report file. The path is construct as followed : {PORJECT_BUILD_DIR}\{CONFIG_RELATIVE_REPORT_FILE}\{CONFIG_REPORT_FILE_NAME}
   * 
   * @return the report file
   */
  public File getReportFile() {
    String reportFileRelativePath = getReportFileRelativePath();
    StringBuilder fileName = new StringBuilder(reportFileRelativePath).append(File.separator);
    File reportDirectory = new File(project.getFileSystem().getBuildDir().getAbsolutePath(), reportFileRelativePath);
    if ( !reportDirectory.exists()) {
      reportDirectory.mkdir();
    }
    String reportFileName = getXmlOutputFile();
    fileName.append(reportFileName);
    File reportFile = new File(project.getFileSystem().getBuildDir(), fileName.toString());
    return reportFile;
  }

  public boolean isAnalyzeOnly() {
    return project.getConfiguration().getBoolean(GNAT_ANALYZE_ONLY_KEY, GNAT_ANALYZE_ONLY_DEFAULT);
  }

  public boolean isIncludeSourceDirectories() {
    return project.getConfiguration().getBoolean(GNAT_AUTO_INCLUDE_SOURCE_DIRS_KEY, GNAT_AUTO_INCLUDE_SOURCE_DIRS_DEFAULT);
  }

  @SuppressWarnings("unchecked")
  public List<String> getExtraArguments() {
    return configuration.getList(GNAT_COMPILER_EXTRA_ARGUMENTS_KEY);
  }

  public List<String> getAutomaticalyIncludedDirectories() {
    List<String> automaticallyIncludedDirectories = new ArrayList<String>();
    List<File> sourceDirectories = project.getFileSystem().getSourceDirs();
    for (File dir : sourceDirectories) {
      automaticallyIncludedDirectories.add(dir.getAbsolutePath());
      automaticallyIncludedDirectories.addAll(getAutomaticalyIncludedDirectories(dir));
    }
    return automaticallyIncludedDirectories;
  }

  private List<String> getAutomaticalyIncludedDirectories(File dir) {
    List<String> dirs = new ArrayList<String>();
    if (dir.isDirectory()) {
      File[] files = dir.listFiles();
      for (File file : files) {
        if (file.isDirectory()) {
          dirs.add(file.getAbsolutePath());
          dirs.addAll(getAutomaticalyIncludedDirectories(file));
        }
      }
    }
    return dirs;
  }

  /**
   * @return
   */
  public String getExecutable() {
    return configuration.getString(GNAT_EXECUTABLE_KEY, GNAT_DEFAULT_EXECUTABLE_NAME);
  }

  public String getXmlOutputModifier() {
    return configuration.getString(GNAT_XML_OUTPUT_MODIFIER_KEY, GNAT_DEFAULT_XML_OUTPUT_MODIFIER);
  }

  public String getXmlOutputFileModifier() {
    return configuration.getString(GNAT_XML_OUTPUT_FILE_MODIFIER_KEY, GNAT_DEFAULT_XML_OUTPUT_FILE_MODIFIER);
  }

  public String getXmlOutputFile() {
    return configuration.getString(GNAT_XML_OUTPUT_FILE_KEY, GNAT_DEFAULT_XML_OUTPUT_FILE);
  }

  /**
   * @return
   */
  public String getReportFileRelativePath() {
    return configuration.getString(GNAT_REPORT_FILE_RELATIVE_PATH_KEY, GNAT_DEFAULT_REPORT_FILE_RELATIVE_PATH);
  }

  /**
   * @return
   */
  public String getExcludedPackages() {
    return configuration.getString(GNAT_EXCLUDE_PACKAGE_KEY, GNAT_DEFAULT_EXCLUDE_PACKAGE_MODIFIER);
  }

  public String getSourceDirectories() {
    return StringUtils.join(configuration.getStringArray(GNAT_SOURCE_DIRECTORIES), GNAT_DIRECTORY_SEPARATOR);
  }

  public List<String> getSourceFiles() {
    List<File> files = project.getFileSystem().getSourceFiles(Ada.INSTANCE);
    List<String> sourceFiles = new ArrayList<String>();
    for (File file : files) {
      sourceFiles.add(file.getAbsolutePath());
    }
    return sourceFiles;
  }

  /**
   * @return
   */
  public String getCompilerArgumentModifier() {
    return configuration.getString(GNAT_COMPILER_ARGUMENT_MODIFIER_KEY, GNAT_DEFAULT_COMPILER_ARGUMENT_MODIFIER);
  }

  public String getIncludeDirectoryModifier() {
    return configuration.getString(GNAT_INCLUDE_DIRECTORY_MODIFIER_KEY, GNAT_DEFAULT_INCLUDE_DIRECTORY_MODIFIER);
  }

  public String getIncludeLibraryModifier() {
    return configuration.getString(GNAT_INCLUDE_LIBRARY_MODIFIER_KEY, GNAT_DEFAULT_INCLUDE_LIBRARY_MODIFIER);
  }

  @SuppressWarnings("unchecked")
  public List<String> getIncludeDirectories() {
    return project.getConfiguration().getList(GNAT_INCLUDE_DIRECTORIES_KEY);

  }

  @SuppressWarnings("unchecked")
  public List<String> getIncludeLibraries() {
    return project.getConfiguration().getList(GNAT_INCLUDE_LIBRARIES_KEY);

  }

  public String getIgnoredDirectories() {
    return configuration.getString(GNAT_IGNORED_DIRECTORIES_KEY, GNAT_DEFAULT_IGNORED_DIRECTORIES);
  }

  public String getDoNoteGenerateTextModifier() {
    return configuration.getString(GNAT_DO_NOT_GENERATE_TEXT_MODIFIER_KEY, GNAT_DEFAULT_DO_NOT_GENERATE_TEXT_MODIFIER);
  }

  public String getExcludePackagesModifier() {
    return configuration.getString(GNAT_EXCLUDE_PACKAGES_MODIFIER_KEY, GNAT_DEFAULT_EXCLUDE_PACKAGES_MODIFIER);
  }

  public String getDefaultArgument() {
    return configuration.getString(GNAT_DEFAULT_ARGUMENT_KEY, GNAT_DEFAULT_DEFAULT_ARGUMENT);
  }

  public String getIgnoreDirectoryModifier() {
    return configuration.getString(GNAT_IGNORE_DIRECTORY_MODIFIER_KEY, GNAT_DEFAULT_IGNORE_DIRECTORY_MODIFIER);
  }

}
