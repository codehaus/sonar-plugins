/*
 * Sonar C-Rules Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.c.checks;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceCodeTreeDecorator;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceProject;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.squid.indexer.SquidIndex;

import com.sonar.c.CAstScanner;
import com.sonar.c.CConfiguration;
import com.sonar.c.api.ast.CAstVisitor;
import com.sonar.c.api.metric.CMetric;

public class CheckUtils {

  public static SourceFile scanFile(String cFilePath, CAstVisitor... visitors) {
    SourceProject project = new SourceProject("cProject");
    SquidIndex index = new SquidIndex();
    index.index(project);
    CAstScanner scanner = new CAstScanner(project, new CConfiguration());

    registerDefaultVisitors(scanner);
    registerVisitors(scanner, visitors);

    File cFileToTest = FileUtils.toFile(CheckUtils.class.getResource(cFilePath));
    if (cFileToTest == null || !cFileToTest.exists()) {
      throw new AssertionError("The c file to test '" + cFilePath + "' doesn't exist.");
    }
    scanner.scanFile(cFileToTest);

    SourceCodeTreeDecorator decorator = new SourceCodeTreeDecorator(project);
    decorator.decorateWith(CMetric.values());
    Collection<SourceCode> sources = index.search(new QueryByType(SourceFile.class));
    if (sources.size() != 1) {
      throw new AssertionError("Only one SourceFile was expected whereas " + sources.size() + " has been returned.");
    } else {
      SourceFile file = (SourceFile)sources.iterator().next();
      return file;
    }
  }

  private static void registerVisitors(CAstScanner scanner, CAstVisitor... visitors) {
    for (CAstVisitor visitor : visitors) {
      scanner.accept(visitor);
    }
  }

  private static void registerDefaultVisitors(CAstScanner scanner) {
    Collection<Class<? extends CAstVisitor>> visitors = scanner.getVisitorClasses();
    for (Class<? extends CAstVisitor> visitor : visitors) {
      try {
        scanner.accept(visitor.newInstance());
      } catch (Exception e) {
        throw new RuntimeException("Unable to instanciate CAstVisitor : " + visitor.getCanonicalName(), e);
      }
    }
  }
  
}
