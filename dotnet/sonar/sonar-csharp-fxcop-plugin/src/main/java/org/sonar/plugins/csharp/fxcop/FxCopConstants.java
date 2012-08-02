/*
 * Sonar C# Plugin :: FxCop
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

package org.sonar.plugins.csharp.fxcop;

/**
 * Constants of the FxCop plugin.
 */
public final class FxCopConstants {

  private FxCopConstants() {
  }

  public static final String REPOSITORY_KEY = "fxcop";
  public static final String TEST_REPOSITORY_KEY = "fxcop-test";
  public static final String REPOSITORY_NAME = "FxCop";
  public static final String TEST_REPOSITORY_NAME = "FxCop Unit Tests";

  public static final String FXCOP_RULES_FILE = "sonar.FxCop";
  public static final String FXCOP_REPORT_XML = "fxcop-report.xml";

  // ----------- Plugin Configuration Properties ----------- //
  public static final String INSTALL_DIR_KEY = "sonar.fxcop.installDirectory";
  public static final String INSTALL_DIR_DEFVALUE = "C:/Program Files/Microsoft FxCop 10.0";

  public static final String ASSEMBLIES_TO_SCAN_KEY = "sonar.dotnet.assemblies";

  public static final String ASSEMBLY_DEPENDENCY_DIRECTORIES_KEY = "sonar.fxcop.assemblyDependencyDirectories";

  public static final String IGNORE_GENERATED_CODE_KEY = "sonar.fxcop.ignoreGeneratedCode";
  public static final boolean IGNORE_GENERATED_CODE_DEFVALUE = true;

  public static final String TIMEOUT_MINUTES_KEY = "sonar.fxcop.timeoutMinutes";
  public static final int TIMEOUT_MINUTES_DEFVALUE = 10;

  public static final String MODE = "sonar.fxcop.mode";

  public static final String REPORTS_PATH_KEY = "sonar.fxcop.reports.path";

}
