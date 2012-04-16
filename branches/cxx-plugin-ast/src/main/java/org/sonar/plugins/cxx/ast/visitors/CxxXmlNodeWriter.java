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
package org.sonar.plugins.cxx.ast.visitors;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.sonar.plugins.cxx.utils.CxxUtils;

/**
 * Writes AST node information in XML format to output file
 * @author Przemyslaw Kociolek
 */
public class CxxXmlNodeWriter {

  private static final int TAB_INCREMENT = 1;
  private static final String QUOTE_CHAR = "\"";
  
  private int tabCount          = 0;
  private BufferedWriter output = null;

  /**
   * Ctor
   * @param fileName  XML output file name
   * @throws IOException  when file could not be created or opened
   */
  public CxxXmlNodeWriter(String fileName) throws IOException {
      output = new BufferedWriter( new FileWriter(fileName) );
      output.write("<?xml version=\"1.0\"?>");
      output.newLine();
  }
  
  /**
   * @return true if output file has been created
   */
  public boolean isValid() {
    return output != null;
  }
  
  public int writeNodeWithToken(String nodeName, String tokenValue, boolean isClosingNode) {
    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("token", tokenValue);
    return writeNode(nodeName, isClosingNode, attributes);
  }
    
  public int writeNode(String nodeName, boolean isClosingNode, Map<String, String> attributes) {
    try {
      tabCount = isClosingNode ? tabCount - TAB_INCREMENT : tabCount;
      String closing = isClosingNode ? "</" : "<";
      writeTabs(tabCount);
      
      if(attributes == null) {
        output.write(closing + nodeName + ">");
      } else {
        writeNodeWithAttributes(nodeName, attributes);
      }
      
      output.newLine();
      tabCount = isClosingNode ? tabCount : tabCount + TAB_INCREMENT;
    } catch (IOException e) {
      CxxUtils.LOG.error(e.getMessage());
      return ASTVisitor.PROCESS_ABORT;
    }
    return ASTVisitor.PROCESS_CONTINUE;
  }
  
  private void writeNodeWithAttributes(String nodeName, Map<String, String> attributes) throws IOException {
    output.write("<" + nodeName);
    for(Map.Entry<String, String> e : attributes.entrySet()) {
     output.write(" " + e.getKey() + "=" + QUOTE_CHAR + e.getValue() + QUOTE_CHAR);
    }
    output.write(">");    
  }

  private void writeTabs(int numberOfTabs) throws IOException {
    for(int i = 0; i < numberOfTabs; ++i) {
     output.write("\t"); 
    }
  }

  public void saveToFile() throws IOException {
    output.close();
  }

}