/*
 * Sonar C# Plugin :: StyleCop
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

package org.sonar.plugins.csharp.stylecop.runner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;
import org.sonar.plugins.csharp.stylecop.StyleCopConstants;

/**
 * Class that generates MSBuild
 */
public class MsBuildFileGenerator {

  protected static final String MSBUILD_FILE = "stylecop-msbuild.xml";
  private Configuration configuration;
  private VisualStudioSolution solution;

  public MsBuildFileGenerator(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the current VS solution
   * 
   * @param solution
   *          the solution
   */
  public void setVisualStudioSolution(VisualStudioSolution solution) {
    this.solution = solution;
  }

  /**
   * Generates the MSBuild file in the given output folder
   * 
   * @param outputFolder
   *          the output folder
   */
  public File generateFile(File outputFolder) {
    File msBuildFile = new File(outputFolder, MSBUILD_FILE);
    File styleCopRuleFile = new File(outputFolder, StyleCopConstants.STYLECOP_RULES_FILE);
    File reportFile = new File(outputFolder, StyleCopConstants.STYLECOP_REPORT_XML);

    FileWriter writer = null;
    try {
      writer = new FileWriter(msBuildFile);
      generateContent(writer, styleCopRuleFile, reportFile);
      writer.flush();
    } catch (IOException e) {
      throw new SonarException("Error while generating the MSBuild file needed to launch StyleCop: " + msBuildFile.getAbsolutePath(), e);
    } finally {
      IOUtils.closeQuietly(writer);
    }

    return msBuildFile;
  }

  protected void generateContent(Writer writer, File styleCopRuleFile, File reportFile) throws IOException {
    writer.append("<?xml version=\"1.0\" ?>\n");
    writer
        .append("<Project xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\" DefaultTargets=\"StyleCopLaunch\" ToolsVersion=\"3.5\">\n");
    writer.append("    <PropertyGroup>\n");
    writer.append("        <ProjectRoot>");
    StringEscapeUtils.escapeXml(writer, solution.getSolutionDir().getAbsolutePath());
    writer.append("</ProjectRoot>\n");
    writer.append("        <StyleCopRoot>");
    StringEscapeUtils.escapeXml(writer, configuration.getString(StyleCopConstants.INSTALL_DIR_KEY, StyleCopConstants.INSTALL_DIR_DEFVALUE));
    writer.append("</StyleCopRoot>\n");
    writer.append("    </PropertyGroup>\n");
    writer.append("    <UsingTask TaskName=\"StyleCopTask\" AssemblyFile=\"$(StyleCopRoot)\\Microsoft.StyleCop.dll\"></UsingTask>\n");
    writer.append("    <ItemGroup>\n");
    generateProjectList(writer);
    writer.append("    </ItemGroup>\n");
    writer.append("    <Target Name=\"StyleCopLaunch\">\n");
    writer.append("        <CreateItem Include=\"%(Project.RootDir)%(Project.Directory)**\\*.cs\">\n");
    writer.append("            <Output ItemName=\"SourceAnalysisFiles\" TaskParameter=\"Include\"></Output>\n");
    writer.append("        </CreateItem>\n");
    writer.append("        <StyleCopTask MaxViolationCount=\"-1\" OverrideSettingsFile=\"");
    StringEscapeUtils.escapeXml(writer, styleCopRuleFile.getAbsolutePath());
    writer.append("\"\n            OutputFile=\"");
    StringEscapeUtils.escapeXml(writer, reportFile.getAbsolutePath());
    writer.append("\"\n            TreatErrorsAsWarnings=\"true\" ForceFullAnalysis=\"true\"\n");
    writer.append("            SourceFiles=\"@(SourceAnalysisFiles);@(CSFile)\"\n");
    writer.append("            ProjectFullPath=\"");
    StringEscapeUtils.escapeXml(writer, solution.getSolutionFile().getAbsolutePath());
    writer.append("\"></StyleCopTask>\n");
    writer.append("    </Target>\n");
    writer.append("</Project>");
  }

  private void generateProjectList(Writer writer) throws IOException {
    for (VisualStudioProject project : solution.getProjects()) {
      writer.append("        <Project Include=\"");
      StringEscapeUtils.escapeXml(writer, project.getProjectFile().getAbsolutePath());
      writer.append("\"></Project>\n");
    }
  }

}
