/*
 * Sonar C# Plugin :: Core
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

package org.sonar.plugins.csharp.api;

import java.io.File;

import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

/**
 * Class used to share information, between C# plugins, about Windows and Visual Studio elements, such as:
 * <ul>
 * <li>the environment settings (.NET SDK directory for instance),</li>
 * <li>the current Visual Studio solution that is being analyzed.</li>
 * </ul>
 */
public final class MicrosoftWindowsEnvironment {

  // primarily used to allow unit tests to work
  private static boolean allowOverrideAttributes;
  private static VisualStudioSolution currentSolution;
  private static File dotnetSdkDirectory;

  static {
    if ("true".equalsIgnoreCase(System.getProperty("MicrosoftWindowsEnvironment.allowOverrideAttributes"))) {
      allowOverrideAttributes = true;
    }
  }

  private MicrosoftWindowsEnvironment() {
  }

  /*
   * Just to make sure that nobody will override the attributes once they have been set by the C# Core plugin
   */
  private static void checkIfNotNull(Object attribute) {
    if ( !allowOverrideAttributes && attribute != null) {
      throw new SonarException("Cannot override attributes that have already been assigned on MicrosoftWindowsEnvironment class.");
    }
  }

  /**
   * <b>Must not be used.</b>
   * 
   * @param currentSolution
   *          the currentSolution to set
   */
  public static void setCurrentSolution(VisualStudioSolution currentSolution) {
    checkIfNotNull(MicrosoftWindowsEnvironment.currentSolution);
    MicrosoftWindowsEnvironment.currentSolution = currentSolution;
  }

  /**
   * Returns the {@link VisualStudioSolution} that is under analysis
   * 
   * @return the current Visual Studio solution
   */
  public static VisualStudioSolution getCurrentSolution() {
    return currentSolution;
  }

  /**
   * <b>Must not be used.</b>
   * 
   * @param dotnetSdkDirectory
   *          the dotnetSdkDirectory to set
   */
  public static void setDotnetSdkDirectory(File dotnetSdkDirectory) {
    checkIfNotNull(MicrosoftWindowsEnvironment.dotnetSdkDirectory);
    MicrosoftWindowsEnvironment.dotnetSdkDirectory = dotnetSdkDirectory;
  }

  /**
   * Returns the path of the .NET SDK
   * 
   * @return the dotnetSdkDirectory
   */
  public static File getDotnetSdkDirectory() {
    return dotnetSdkDirectory;
  }

}
