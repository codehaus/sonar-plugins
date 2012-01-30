/*
 * Sonar Python Plugin
 * Copyright (C) 2011 Waleri Enns
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

package org.sonar.plugins.python;

import org.junit.Test;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Scopes;

public class PythonFileTest {

  @Test
  public void testOrdinaryFile() throws Exception {
    String fname = "main.py";
    // PythonFile pyfile = new PythonFile(fname, null, false);
    
    // assert (pyfile.getParent() != null);
    // assert (pyfile.getLanguage() == Python.INSTANCE);
    // assert (pyfile.getName() == fname);
    // assert (pyfile.getLongName() == fname);
    // assert (pyfile.getScope() == Scopes.FILE);
    // assert (pyfile.getQualifier() == Qualifiers.FILE);
  }

  @Test
  public void testFileInSubDir() throws Exception {
    String pathname = "subdir/main.py";
    String basename = "main.py";
    String dirname = "subdir";
    // PythonFile pyfile = new PythonFile(pathname, null, false);
    
    // assert (pyfile.getParent() != null);
    // assert (pyfile.getLanguage() == Python.INSTANCE);
    // assert (pyfile.getName().equals(basename));
    // assert (pyfile.getLongName().equals(pathname));
    // assert (pyfile.getScope() == Scopes.FILE);
    // assert (pyfile.getQualifier() == Qualifiers.FILE);
  }
}
