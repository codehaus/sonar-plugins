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
package org.sonar.cpp;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class CppFileSetUtil {

    private static FileSet createFileSet(File baseDir, String includes, String excludes) {
        FileSet fs = new FileSet();
        fs.setDir(baseDir);
        fs.setProject(new Project());

        StringTokenizer tokens;

        tokens = new StringTokenizer(includes,",");
        while(tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            fs.createInclude().setName(token);
        }
        if(excludes!=null) {
            tokens = new StringTokenizer(excludes,",");
            while(tokens.hasMoreTokens()) {
                String token = tokens.nextToken().trim();
                fs.createExclude().setName(token);
            }
        }
        return fs;
    }
	
	
	public static String[] findCppUnitReports(File parentPathFile) {
		FileSet fs = createFileSet(parentPathFile, "**/*.xml",  null);
		DirectoryScanner ds = fs.getDirectoryScanner(new Project());
		String[] cppunitFiles = ds.getIncludedFiles();
		return cppunitFiles;			
	}
	
}
