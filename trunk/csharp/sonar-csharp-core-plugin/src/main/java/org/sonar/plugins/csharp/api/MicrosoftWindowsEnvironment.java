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

import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

/**
 * Class used to share information, between C# plugins, about Windows and Visual Studio elements, such as:
 * <ul>
 * <li>the environment settings (.NET SDK directory for instance),</li>
 * <li>the current Visual Studio solution that is being analyzed.</li>
 * </ul>
 */
public final class MicrosoftWindowsEnvironment implements BatchExtension {

  // primarily used to allow unit tests to work
  private boolean allowOverrideAttributes;
  private VisualStudioSolution currentSolution;
  private File dotnetSdkDirectory;

  public MicrosoftWindowsEnvironment() {
    if ("true".equalsIgnoreCase(System.getProperty("this.allowOverrideAttributes"))) {
      allowOverrideAttributes = true;
    }
  }

  public MicrosoftWindowsEnvironment(boolean allowOverrideAttributes) {
    this.allowOverrideAttributes = allowOverrideAttributes;
  }

  /*
   * Just to make sure that nobody will override the attributes once they have been set by the C# Core plugin
   */
  private void checkIfNotNull(Object attribute) {
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
  public void setCurrentSolution(VisualStudioSolution currentSolution) {
    checkIfNotNull(this.currentSolution);
    this.currentSolution = currentSolution;
  }

  /**
   * Returns the {@link VisualStudioSolution} that is under analysis
   * 
   * @return the current Visual Studio solution
   */
  public VisualStudioSolution getCurrentSolution() {
    return currentSolution;
  }

  /**
   * <b>Must not be used.</b>
   * 
   * @param dotnetSdkDirectory
   *          the dotnetSdkDirectory to set
   */
  public void setDotnetSdkDirectory(File dotnetSdkDirectory) {
    checkIfNotNull(this.dotnetSdkDirectory);
    this.dotnetSdkDirectory = dotnetSdkDirectory;
  }

  /**
   * Returns the path of the .NET SDK
   * 
   * @return the dotnetSdkDirectory
   */
  public File getDotnetSdkDirectory() {
    return dotnetSdkDirectory;
  }

}
