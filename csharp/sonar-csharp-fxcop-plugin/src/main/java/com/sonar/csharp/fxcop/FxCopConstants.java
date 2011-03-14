/*
 * Sonar C# Plugin :: FxCop
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

package com.sonar.csharp.fxcop;

/**
 * Constants of the FxCop plugin.
 */
public final class FxCopConstants {

  private FxCopConstants() {
  }

  public static final String PLUGIN_KEY = "fxcop";
  public static final String PLUGIN_NAME = "FxCop";

  public static final String REPOSITORY_KEY = PLUGIN_KEY;
  public static final String REPOSITORY_NAME = PLUGIN_NAME;

  public static final String LANGUAGE_KEY = "cs";
  public static final String LANGUAGE_NAME = "C#";

  public static final String FXCOP_REPORT_XML = "fxcop-report.xml";
  public static final String SL_FXCOP_REPORT_XML = "silverlight-fxcop-report.xml";

  // ----------- Plugin Configuration Properties ----------- //
  public static final String EXECUTABLE_KEY = "sonar.fxcop.executable";
  public static final String EXECUTABLE_DEFVALUE = "C:/Program Files/Microsoft FxCop 10.0/FxCopCmd.exe";

  public static final String ASSEMBLIES_TO_SCAN_KEY = "sonar.fxcop.assemblies";
  public static final String ASSEMBLIES_TO_SCAN_DEFVALUE = "";

  public static final String ASSEMBLY_DEPENDENCY_DIRECTORIES_KEY = "sonar.fxcop.assemblyDependencyDirectories";
  public static final String ASSEMBLY_DEPENDENCY_DIRECTORIES_DEFVALUE = "";

  public static final String IGNORE_GENERATED_CODE_KEY = "sonar.fxcop.ignoreGeneratedCode";
  public static final boolean IGNORE_GENERATED_CODE_DEFVALUE = true;

  public static final String TIMEOUT_MINUTES_KEY = "sonar.fxcop.timeoutMinutes";
  public static final int TIMEOUT_MINUTES_DEFVALUE = 10;

}
