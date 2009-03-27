/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.codehaus.sonarncss;

import org.codehaus.sonarncss.checkstyle.CheckstyleJavaNcssBridge;
import org.codehaus.sonarncss.checkstyle.CheckstyleLauncher;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;
import org.codehaus.sonarncss.sensors.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class JavaNcss {

  private final Resource project;
  private final List<File> filesToAnalyse;

  private final List<ASTSensor> javaNcssVisitors = Arrays.asList(new PackageSensor(), new FileSensor(),
      new ClassSensor(), new MethodSensor(), new LocSensor(), new BlankLineSensor(), new CommentSensors(),
      new JavadocSensor(), new StatementSensor(), new BrancheSensor(), new ComplexitySensor());

  private JavaNcss(File dirToAnalyse) {
    this(traverse(dirToAnalyse));
  }

  public static Resource analyze(String dirOrFilePathToAnalyze) {
    JavaNcss javaNcss = new JavaNcss(new File(dirOrFilePathToAnalyze));
    return javaNcss.analyzeSources();
  }

  public static Resource analyze(File dirOrFileToAnalyze) {
    if (dirOrFileToAnalyze == null) {
      throw new IllegalStateException("There is no directory or file to analyse as the File object is null.");
    }
    JavaNcss javaNcss = new JavaNcss(dirOrFileToAnalyze);
    return javaNcss.analyzeSources();
  }

  private JavaNcss(List<File> filesToAnalyse) {
    this.filesToAnalyse = filesToAnalyse;
    project = new Resource("Project", JavaType.PROJECT);
    Stack<Resource> resourcesStack = new Stack<Resource>();
    resourcesStack.add(project);
    for (ASTSensor visitor : javaNcssVisitors) {
      visitor.setResourcesStack(resourcesStack);
    }
    CheckstyleJavaNcssBridge.setJavaNcssASTVisitors(javaNcssVisitors);
  }

  public static Resource analyze(List<File> filesToAnalyse) {
    JavaNcss javaNcss = new JavaNcss(filesToAnalyse);
    return javaNcss.analyzeSources();
  }

  private Resource analyzeSources() {
    CheckstyleLauncher.launchCheckstyleEngine(filesToAnalyse);
    project.compute();
    return project;
  }

  /**
   * Traverses a specified node looking for files to check. Found files are
   * added to a specified list. Subdirectories are also traversed.
   *
   * @param aNode  the node to process
   * @param aFiles list to add found files to
   */
  private static List<File> traverse(File aNode) {
    List<File> files = new ArrayList<File>();
    if (aNode.canRead()) {
      if (aNode.isDirectory()) {
        final File[] nodes = aNode.listFiles();
        for (File node : nodes) {
          files.addAll(traverse(node));
        }
      } else if (aNode.isFile()) {
        files.add(aNode);
      }
    }
    return files;
  }

}
