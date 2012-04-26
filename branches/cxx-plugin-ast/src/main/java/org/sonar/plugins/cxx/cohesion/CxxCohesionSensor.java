/*
 * Sonar Cxx Plugin, open source software quality management tool.
 * Copyright (C) 2010 - 2011, Neticoa SAS France - Tous droits reserves.
 * Author(s) : Franck Bonin, Neticoa SAS France.
 *
 * Sonar Cxx Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Cxx Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar Cxx Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.cohesion;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonar.plugins.cxx.ast.CxxCppParsedFile;
import org.sonar.plugins.cxx.ast.CxxCppParser;
import org.sonar.plugins.cxx.ast.CxxCppParserException;
import org.sonar.plugins.cxx.ast.cpp.CxxClass;
import org.sonar.plugins.cxx.ast.cpp.CxxClassMember;
import org.sonar.plugins.cxx.ast.cpp.CxxClassMethod;
import org.sonar.plugins.cxx.utils.CxxSensor;
import org.sonar.plugins.cxx.utils.CxxUtils;

public class CxxCohesionSensor extends CxxSensor {

  private static final Number[] LCOM4_LIMITS = { 2, 3, 4, 5, 10 };
  
  private RangeDistributionBuilder builder = new RangeDistributionBuilder(CoreMetrics.LCOM4_DISTRIBUTION, LCOM4_LIMITS);
  private Project project = null;
  private SensorContext context = null;
  
  public void analyse(Project project, SensorContext context) {
    this.project = project;
    this.context = context;
    
    CxxCppParser parser = new CxxCppParser();
    List<InputFile> sourceFiles = project.getFileSystem().mainFiles(CxxLanguage.KEY);
    
    for(InputFile inputFile : sourceFiles) {
      if(isSourceFile(inputFile.getFile())) {  
        parseFile(parser, inputFile);
      }
    }
    
  }

  private boolean isSourceFile(File file) {
    
    
    //if(file.getAbsolutePath().endsWith(suffix)) {
      return true;
    //}
    //return false;
  }

  private void parseFile(CxxCppParser parser, InputFile inputFile) {
    try {
      CxxCppParsedFile parsedFile = parser.parseFile(inputFile);
      saveFileMeasure(inputFile, analyzeFileCohesion(parsedFile.getClasses()));
    } catch (CxxCppParserException e) {
      CxxUtils.LOG.error(e.getMessage());
    }
  }

  private void saveFileMeasure(InputFile inputFile, double fileCohesion) {
    org.sonar.api.resources.File resource =org.sonar.api.resources.File.fromIOFile(inputFile.getFile(), project);
    if(context.getResource(resource) != null) {
      context.saveMeasure(resource, CoreMetrics.LCOM4, fileCohesion);
      context.saveMeasure(resource, builder.build().setPersistenceMode(PersistenceMode.MEMORY));
    } else {
      CxxUtils.LOG.error("Resource not indexed: " + inputFile.getFile().getAbsolutePath());
    }
  }

  private double analyzeFileCohesion(Set<CxxClass> classes) {
    double fileCohesion = 0;
    Iterator<CxxClass> it = classes.iterator();
    while(it.hasNext()) {
      CxxClass clazz = it.next();
      double classCohesion = analyzeClassCohesion(clazz);
      builder.add(classCohesion);
      fileCohesion += classCohesion;
    }
    return fileCohesion;
  }

  private double analyzeClassCohesion(CxxClass clazz) {
    Set<CxxClassMember> members = clazz.getMembers();
    Set<CxxClassMethod> methods = clazz.getMethods();
    if(methods.isEmpty()) {
      return 0;
    }
    
    double lcom4 = members.size();
    Iterator<CxxClassMember> memberIt = members.iterator();
    while(memberIt.hasNext()) {
      if(isMemberUsedInMethods(memberIt.next(), methods)) {
        lcom4 = Math.max(1, lcom4-1);
      }
    }
    return lcom4;
  }

  private boolean isMemberUsedInMethods(CxxClassMember member, Set<CxxClassMethod> methods) {
    Iterator<CxxClassMethod> methodIt = methods.iterator();
    while(methodIt.hasNext()) {
      CxxClassMethod method = methodIt.next();
      List<String> usedNames = method.getBody().getDetectedNames();
      if(usedNames.contains(member.getName())) {
        return true;
      }
    }
    return false;
  }
  
}
