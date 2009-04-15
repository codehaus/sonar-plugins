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
package org.codehaus.sonarncss.entities;

import java.util.ArrayList;
import java.util.List;

public class Measures {
  private JavaType javaType;

  private long methods = 0;
  private long classes = 0;
  private long files = 0;
  private long packages = 0;

  private long loc = 0;
  private long blankLines = 0;

  private long statements = 0;
  private long branches = 0;
  private long complexity = 0;

  private long nonJavadocLines = 0;
  private long javadocLines = 0;
  private long methodsWithJavadoc = 0;
  private long classesWithJavadoc = 0;

  public Measures(JavaType javaType) {
    this.javaType = javaType;
    if (javaType == JavaType.PACKAGE) {
      addPackage();
    } else if (javaType == JavaType.FILE) {
      addFile();
    } else if (javaType == JavaType.CLASS) {
      addClass();
    } else if (javaType == JavaType.METHOD) {
      addMethod();
    }
  }

  public long getLoc() {
    return loc;
  }

  public Measures setLoc(long loc) {
    this.loc = loc;
    return this;
  }

  public long getNcloc() {
    return getLoc() == 0 ? 0 : getLoc() - getCommentLines() - getBlankLines();
  }

  public double getAvgMethodCmp() {
    if (methods != 0) {
      return (double) complexity / (double) methods;
    } else {
      throw new IllegalStateException(
          "Unable to compute the average complexity by method as the number of methods == 0");
    }
  }

  public double getAvgClassCmp() {
    if (classes != 0) {
      return (double) complexity / (double) classes;
    } else {
      throw new IllegalStateException(
          "Unable to compute the average complexity by class as the number of classes == 0");
    }
  }

  public double getAvgFileCmp() {
    if (files != 0) {
      return (double) complexity / (double) files;
    } else {
      throw new IllegalStateException(
          "Unable to compute the average complexity by file as the number of files == 0");
    }
  }

  public double getAvgMethodStmts() {
    if (methods != 0) {
      return (double) statements / (double) methods;
    } else {
      throw new IllegalStateException(
          "Unable to compute the average statements by method as the number of methods == 0");
    }
  }

  public double getAvgClassStmts() {
    if (classes != 0) {
      return (double) statements / (double) classes;
    } else {
      throw new IllegalStateException(
          "Unable to compute the average statements by class as the number of classes == 0");
    }
  }

  public double getAvgFileStmts() {
    if (files != 0) {
      return (double) statements / (double) files;
    } else {
      throw new IllegalStateException(
          "Unable to compute the average statements by file as the number of files == 0");
    }
  }

  public JavaType getJavaType() {
    return javaType;
  }

  public long getBlankLines() {
    return blankLines;
  }

  public Measures setBlankLines(long blankLines) {
    this.blankLines = blankLines;
    return this;
  }

  public long getStatements() {
    return statements;
  }

  public void addstatement() {
    statements++;
  }

  public long getNonJavadocLines() {
    return nonJavadocLines;
  }

  public long getCommentLines() {
    return nonJavadocLines + javadocLines;
  }

  public Measures setNonJavadocLines(long nonJavadocLines) {
    this.nonJavadocLines = nonJavadocLines;
    return this;
  }

  public Measures addMethodWithJavadoc(long javadocLines) {
    methodsWithJavadoc++;
    this.javadocLines += javadocLines;
    return this;
  }

  public Measures addClassWithJavadoc(long javadocLines) {
    classesWithJavadoc++;
    this.javadocLines += javadocLines;
    return this;
  }

  public long getComplexity() {
    return complexity;
  }

  public Measures setComplexity(long complexity) {
    this.complexity = complexity;
    return this;
  }

  public long getBranches() {
    return branches;
  }

  public long getMethodsWithJavadoc() {
    return methodsWithJavadoc;
  }

  public long getClassesWithJavadoc() {
    return classesWithJavadoc;
  }

  public Measures addBranch() {
    branches++;
    return this;
  }

  public long getMethods() {
    return methods;
  }

  public Measures addMethod() {
    methods++;
    return this;
  }

  public Measures setMethods(long methods) {
    this.methods = methods;
    return this;
  }

  public long getClasses() {
    return classes;
  }

  public Measures addClass() {
    classes++;
    return this;
  }

  public Measures setClasses(long classes) {
    this.classes = classes;
    return this;
  }

  public long getFiles() {
    return files;
  }

  public Measures addFile() {
    files++;
    return this;
  }

  public long getPackages() {
    return packages;
  }

  public Measures addPackage() {
    packages++;
    return this;
  }

  public long getJavadocLines() {
    return javadocLines;
  }

  public void addMeasures(List<Measures> measuresList) {
    for (Measures measures : measuresList) {
      methods += measures.getMethods();
      classes += measures.getClasses();
      files += measures.getFiles();
      packages += measures.getPackages();

      blankLines += measures.getBlankLines();
      loc += measures.getLoc();

      branches += measures.getBranches();
      complexity += measures.getComplexity();
      statements += measures.getStatements();

      nonJavadocLines += measures.getNonJavadocLines();
      javadocLines += measures.getJavadocLines();
      methodsWithJavadoc += measures.getMethodsWithJavadoc();
      classesWithJavadoc += measures.getClassesWithJavadoc();
    }
  }

  public void addMeasures(Measures... measuresArray) {
    List<Measures> measuresList = new ArrayList<Measures>();
    for (int i = 0; i < measuresArray.length; i++) {
      measuresList.add(measuresArray[i]);
    }
    addMeasures(measuresList);
  }

  public Measures setStatements(long statements) {
    this.statements = statements;
    return this;
  }

  public double getPercentOfCommentLines() {
    if (loc != 0) {
      return (double) (nonJavadocLines + javadocLines) / (double) loc;
    } else {
      throw new IllegalStateException("Unable to compute the percentage of comment lines as 'loc' == 0");
    }
  }

  public void setJavadocBlock(long javadocLines) {
    if (javaType == JavaType.CLASS) {
      classesWithJavadoc++;
    } else if (javaType == JavaType.METHOD) {
      methodsWithJavadoc++;
    } else {
      throw new IllegalStateException("It's fordidden to add javadoc on other thing than a class or a method.");
    }
    this.javadocLines += javadocLines;
  }

  public double getPercentOfMethodsWithJavadoc() {
    if (methods != 0) {
      return (double) methodsWithJavadoc / (double) methods;
    } else {
      throw new IllegalStateException(
          "Unable to compute the percentage of methods with javadoc there is no method.");
    }
  }

  public double getPercentOfClassesWithJavadoc() {
    if (classes != 0) {
      return (double) classesWithJavadoc / (double) classes;
    } else {
      throw new IllegalStateException(
          "Unable to compute the percentage of classes with javadoc there is no class.");
    }
  }

  public String toString() {
    return "cmp=" + complexity + ",stmts=" + statements + ",meth=" + methods + ",cla=" + classes;
  }
}