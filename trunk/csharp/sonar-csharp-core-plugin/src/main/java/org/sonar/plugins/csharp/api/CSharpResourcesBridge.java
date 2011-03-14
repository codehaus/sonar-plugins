/*
 * Sonar C# Plugin :: Core
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

package org.sonar.plugins.csharp.api;

import java.util.Map;
import java.util.Set;

import org.sonar.api.resources.File;
import org.sonar.api.resources.Resource;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFile;

import com.google.common.collect.Maps;

/**
 * Class that gives links between logical resources (C# types and members) and their enclosing source files. <br/>
 * It is useful when parsing third-party tool reports that contain references to the logical structure of the code: it is then necessary to
 * find to which physical file a given logical item belongs to, so that it is possible to save measure or violations to the correct Sonar
 * resource.
 */
public class CSharpResourcesBridge {

  private static CSharpResourcesBridge instance;

  private Map<String, Resource<?>> logicalToPhysicalResourcesMap;

  private CSharpResourcesBridge() {
    logicalToPhysicalResourcesMap = Maps.newHashMap();
  }

  /**
   * Returns the only instance of the {@link CSharpResourcesBridge} object.
   * 
   * @return the instance
   */
  public static CSharpResourcesBridge getInstance() {
    if (instance == null) {
      synchronized (CSharpResourcesBridge.class) {
        instance = new CSharpResourcesBridge();
      }
    }
    return instance;
  }

  /**
   * Method used to populate the map with the logical resources found in the Squid file and link them to the Sonar file.
   * 
   * @param squidFile
   *          the Squid file
   * @param sonarFile
   *          the Sonar file
   */
  public void indexFile(SourceFile squidFile, File sonarFile) {
    indexChildren(squidFile.getChildren(), sonarFile);
  }

  private void indexChildren(Set<SourceCode> sourceCodes, File sonarFile) {
    if (sourceCodes != null) {
      for (SourceCode children : sourceCodes) {
        logicalToPhysicalResourcesMap.put(children.getKey(), sonarFile);
        indexChildren(children.getChildren(), sonarFile);
      }
    }
  }

  /**
   * Returns the physical file that contains the definition of the type referenced by its namespace and its name.
   * 
   * @param namespaceName
   *          the namespace of the type
   * @param typeName
   *          the type name
   * @return the resource that contains this type, or NULL if none
   */
  public Resource<?> getFromTypeName(String namespaceName, String typeName) {
    return getFromTypeName(namespaceName + "." + typeName);
  }

  public Resource<?> getFromTypeName(String typeFullName) {
    return logicalToPhysicalResourcesMap.get(typeFullName);
  }

  public Resource<?> getFromMemberName(String memberFullName) {
    return logicalToPhysicalResourcesMap.get(memberFullName);
  }

}
