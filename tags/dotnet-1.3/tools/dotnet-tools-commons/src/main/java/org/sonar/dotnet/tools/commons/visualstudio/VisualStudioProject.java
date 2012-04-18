/*
 * .NET tools :: Commons
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
/*
 * Created on Apr 16, 2009
 */
package org.sonar.dotnet.tools.commons.visualstudio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * A dot net project extracted from a solution
 * 
 * @author Fabrice BELLINGARD
 * @author Jose CHILLAN Apr 16, 2009
 */
public class VisualStudioProject {

  private static final Logger LOG = LoggerFactory.getLogger(VisualStudioProject.class);

  private String name;
  private File projectFile;
  private ArtifactType type;
  private String assemblyName;
  private String assemblyVersion;
  private String realAssemblyName; // assembly name found in the csproj file no matter what
  private String rootNamespace;
  private UUID projectGuid;
  private File debugOutputDir;
  private File releaseOutputDir;
  /** Output directory specified from maven */
  private String forcedOutputDir;
  private Map<String, File> buildConfOutputDirMap;
  private File directory;
  private boolean silverlightProject;
  private Map<File, SourceFile> sourceFileMap;

  private boolean unitTest;

  private boolean integTest;

  /**
   * Builds a {@link VisualStudioProject} ...
   * 
   * @param name
   * @param projectFile
   */
  public VisualStudioProject() {
    super();
  }

  /**
   * Gets the relative path of a file contained in the project. <br>
   * For example, if the visual studio project is C:/MySolution/MyProject/MyProject.csProj and the file is
   * C:/MySolution/MyProject/Dummy/Foo.cs, then the result is Dummy/Foo.cs
   * 
   * @param file
   *          the file whose relative path is to be computed
   * @return the relative path, or <code>null</code> if the file is not in the project subdirectories
   */
  public String getRelativePath(File file) {
    File canonicalDirectory;
    try {
      canonicalDirectory = directory.getCanonicalFile();

      File canonicalFile = file.getCanonicalFile();

      String filePath = canonicalFile.getPath();
      String directoryPath = canonicalDirectory.getPath();
      if (!filePath.startsWith(directoryPath)) {
        // The file is not in the directory
        return null;
      }
      return StringUtils.removeStart(StringUtils.removeStart(filePath, directoryPath), "\\");
    } catch (IOException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("io exception with file " + file, e);
      }
      return null;
    }
  }

  /**
   * Returns the name.
   * 
   * @return The name to return.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the projectFile.
   * 
   * @return The projectFile to return.
   */
  public File getProjectFile() {
    return this.projectFile;
  }

  /**
   * Returns the type.
   * 
   * @return The type to return.
   */
  public ArtifactType getType() {
    return this.type;
  }

  public UUID getProjectGuid() {
    return projectGuid;
  }

  /**
   * Provides the location of the generated artifact(s) of this project according to the build configuration(s) used.
   * 
   * @param buildConfigurations
   *          Visual Studio build configurations used to generate the project
   * @return
   */
  public File getArtifactDirectory(String buildConfigurations) {
    File artifactDirectory = null;
    if (StringUtils.isNotEmpty(forcedOutputDir)) {
      // first trying to use forcedOutputDir as a relative path
      File assemblyDirectory = new File(directory, forcedOutputDir);

      if (!assemblyDirectory.exists()) {
        // path specified "forcedOutputDir" should be absolute,
        // not relative to the project root directory
        assemblyDirectory = new File(forcedOutputDir);
      }

      artifactDirectory = assemblyDirectory;

    } else if (StringUtils.isEmpty(buildConfigurations) || StringUtils.contains(buildConfigurations, "Debug")) {
      artifactDirectory = debugOutputDir;
    } else if (buildConfOutputDirMap == null) {
      // no custom build conf, debug not wanted, get the release one
      artifactDirectory = releaseOutputDir;
    } else {
      // get the first one found
      String buildConfiguration = Arrays.asList(StringUtils.split(buildConfigurations, ",; ")).get(0);
      artifactDirectory = buildConfOutputDirMap.get(buildConfiguration);
    }
    return artifactDirectory;
  }

  /**
   * Gets the generated assembly according to the build configurations
   * 
   * @param buildConfigurations
   *          Visual Studio build configurations used to generate the project
   * @return
   */
  public File getArtifact(String buildConfigurations) {
    File artifactDirectory = getArtifactDirectory(buildConfigurations);
    return new File(artifactDirectory, getArtifactName());
  }

  /**
   * Gets the generated assemblies according to the build configurations. There is zero or one single assembly generated except for web
   * assemblies.
   * 
   * @param buildConfigurations
   *          Visual Studio build configurations used to generate the project
   * @return a Set of the generated assembly files. If no files found, the set will be empty.
   */
  public Set<File> getGeneratedAssemblies(String buildConfigurations) {
    Set<File> result = Sets.newHashSet();
    File assembly = getArtifact(buildConfigurations);
    if (assembly.exists()) {
      result.add(assembly);
    } else {
      LOG.info("Skipping the non generated assembly of project : {}", name);
    }
    return result;
  }

  /**
   * Gets the name of the artifact.
   * 
   * @return
   */
  public String getArtifactName() {
    return realAssemblyName + "." + getExtension();
  }

  @Override
  public String toString() {
    return "Project(name=" + name + ", type=" + type + ", directory=" + directory + ", file=" + projectFile
      + ", assemblyName=" + assemblyName + ", rootNamespace=" + rootNamespace + ", debugDir=" + debugOutputDir + ", releaseDir="
      + releaseOutputDir + ")";
  }

  /**
   * Returns the assemblyName.
   * 
   * @return The assemblyName to return.
   */
  public String getAssemblyName() {
    return this.assemblyName;
  }

  public String getAssemblyVersion() {
    return this.assemblyVersion;
  }

  void setAssemblyVersion(String assemblyVersion) {
    this.assemblyVersion = assemblyVersion;
  }

  /**
   * Sets the assemblyName.
   * 
   * @param assemblyName
   *          The assemblyName to set.
   */
  void setAssemblyName(String assemblyName) {
    this.assemblyName = assemblyName;
    if (StringUtils.isEmpty(realAssemblyName)) {
      realAssemblyName = assemblyName;
    }
  }

  /**
   * Returns the rootNamespace.
   * 
   * @return The rootNamespace to return.
   */
  public String getRootNamespace() {
    return this.rootNamespace;
  }

  /**
   * Sets the rootNamespace.
   * 
   * @param rootNamespace
   *          The rootNamespace to set.
   */
  void setRootNamespace(String rootNamespace) {
    this.rootNamespace = rootNamespace;
  }

  /**
   * Returns the debugOutputDir.
   * 
   * @return The debugOutputDir to return.
   */
  public File getDebugOutputDir() {
    return this.debugOutputDir;
  }

  /**
   * Sets the debugOutputDir.
   * 
   * @param debugOutputDir
   *          The debugOutputDir to set.
   */
  void setDebugOutputDir(File debugOutputDir) {
    this.debugOutputDir = debugOutputDir;
  }

  /**
   * Sets the releaseOutputDir.
   * 
   * @param releaseOutputDir
   *          The releaseOutputDir to set.
   */
  void setReleaseOutputDir(File releaseOutputDir) {
    this.releaseOutputDir = releaseOutputDir;
  }

  /**
   * Returns the directory.
   * 
   * @return The directory to return.
   */
  public File getDirectory() {
    return this.directory;
  }

  /**
   * Sets the root directory of the project. For a regular project, this is where is located the csproj file.
   * 
   * @param directory
   *          The directory to set.
   */
  void setDirectory(File directory) {
    try {
      this.directory = directory.getCanonicalFile();
    } catch (IOException e) {
      LOG.warn("Invalid project directory : " + directory);
    }
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          The name to set.
   */
  void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the projectFile.
   * 
   * @param projectFile
   *          The projectFile to set.
   */
  void setProjectFile(File projectFile) {
    this.projectFile = projectFile;
  }

  /**
   * Sets the projectGuid.
   * 
   * @param projectGuid 
   *          The projectGuid to set.
   */
  void setProjectGuid(UUID projectGuid) {
    this.projectGuid = projectGuid;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          The type to set.
   */
  void setType(ArtifactType type) {
    this.type = type;
  }

  /**
   * @return true if the project contains tests (unit or integ).
   */
  public boolean isTest() {
    return unitTest || integTest;
  }

  public boolean isUnitTest() {
    return this.unitTest;
  }

  public boolean isIntegTest() {
    return this.integTest;
  }

  void setUnitTest(boolean test) {
    this.unitTest = test;
  }

  void setIntegTest(boolean test) {
    this.integTest = test;
  }

  /**
   * Gets the artifact extension (.dll or .exe)
   * 
   * @return the extension
   */
  public String getExtension() {
    String result = null;
    switch (type) {
      case EXECUTABLE:
        result = "exe";
        break;
      case LIBRARY:
      case WEB:
        result = "dll";
        break;
      default:
        result = null;
    }
    return result;
  }

  /**
   * Gets all the files contained in the project
   * 
   * @return
   */
  public Collection<SourceFile> getSourceFiles() {
    if (sourceFileMap == null) {
      initializeSourceFileMap();
    }
    return sourceFileMap.values();
  }

  private void initializeSourceFileMap() {
    Map<File, SourceFile> allFiles = new LinkedHashMap<File, SourceFile>(); // Case of a regular project
    if (projectFile != null) {
      List<String> filesPath = ModelFactory.getFilesPath(projectFile);

      for (String filePath : filesPath) {
        try {
          // We build the file and retrieves its canonical path
          File file = new File(directory, filePath).getCanonicalFile();
          String fileName = file.getName();
          String folder = StringUtils.replace(StringUtils.removeEnd(StringUtils.removeEnd(filePath, fileName), "\\"), "\\", "/");
          SourceFile sourceFile = new SourceFile(this, file, folder, fileName);
          allFiles.put(file, sourceFile);
        } catch (IOException e) {
          LOG.error("Bad file :" + filePath, e);
        }
      }

    } else {
      // For web projects, we take all the C# files
      List<File> csharpFiles = listRecursiveFiles(directory, ".cs");
      for (File file : csharpFiles) {
        SourceFile sourceFile = new SourceFile(this, file, file.getParent(), file.getName());
        allFiles.put(file, sourceFile);
      }
    }
    this.sourceFileMap = allFiles;
  }

  Map<File, SourceFile> getSourceFileMap() {
    if (sourceFileMap == null) {
      initializeSourceFileMap();
    }
    return sourceFileMap;
  }

  /**
   * Recursively lists the files of a directory
   * 
   * @param dir
   *          the directory to list
   * @param extension
   * @return
   */
  private List<File> listRecursiveFiles(File dir, String extension) {
    List<File> result = new ArrayList<File>();
    File[] content = dir.listFiles();
    if (content != null) {
      for (File file : content) {
        if (file.isDirectory()) {
          List<File> matching = listRecursiveFiles(file, extension);
          result.addAll(matching);
        } else {
          // Look for matching file names
          if (StringUtils.endsWithIgnoreCase(file.getName(), extension)) {
            result.add(file);
          }
        }
      }
    }
    return result;
  }

  /**
   * Gets the source representation of a given file.
   * 
   * @param file
   *          the file to retrieve
   * @return the associated source file, or <code>null</code> if the file is not included in the assembly.
   */
  public SourceFile getFile(File file) {
    File currentFile;
    try {
      currentFile = file.getCanonicalFile();
    } catch (IOException e) {
      // File not found
      if (LOG.isDebugEnabled()) {
        LOG.debug("file not found " + file, e);
      }
      return null;
    }
    // We ensure the source files are loaded
    getSourceFiles();
    return sourceFileMap.get(currentFile);
  }

  /**
   * Test if this project is a parent directory of the given file.
   * 
   * @param file
   *          the file to check
   * @return <code>true</code> if the file is under this project
   */
  public boolean isParentDirectoryOf(File file) {
    return ModelFactory.isSubDirectory(directory, file);
  }

  /**
   * Checks if the project contains a given source file.
   * 
   * @param file
   *          the file to check
   * @return <code>true</code> if the project contains the file
   */
  public boolean contains(File file) {
    if (file == null || !file.exists()) {
      return false;
    }
    try {
      File currentFile = file.getCanonicalFile();
      // We ensure the source files are loaded
      getSourceFiles();
      return sourceFileMap.containsKey(currentFile);
    } catch (IOException e) {
      LOG.debug("file error", e);
    }

    return false;
  }

  public boolean isWebProject() {
    return (projectFile == null);
  }

  /**
   * @param silverlightProject
   *          true if it is a silverlight project
   */
  void setSilverlightProject(boolean silverlightProject) {
    this.silverlightProject = silverlightProject;
  }

  /**
   * @return true if it is a silverlight project
   */
  public boolean isSilverlightProject() {
    return silverlightProject;
  }

  void setBuildConfOutputDirMap(Map<String, File> buildConfOutputDirMap) {
    this.buildConfOutputDirMap = buildConfOutputDirMap;
  }

  void setForcedOutputDir(String forcedOutputDir) {
    this.forcedOutputDir = forcedOutputDir;
  }

}
