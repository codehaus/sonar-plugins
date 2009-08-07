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

package org.sonar.cpp.cppunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.cpp.CppFileSetUtil;
import org.sonar.plugins.api.maven.MavenCollector;
import org.sonar.plugins.api.maven.MavenPluginHandler;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;



public class CppunitMavenCollector implements MavenCollector {

	private static Logger logger = LoggerFactory.getLogger(CppunitMavenCollector.class);


	public Class<? extends MavenPluginHandler> dependsOnMavenPlugin(MavenPom pom) {
		return null;
	}

	public boolean shouldStopOnFailure() {
		return true;
	}

	public boolean shouldCollectOn(MavenPom pom) {
		return true;
	}

	protected boolean shouldCollectIfNoSources() {
		return true;
	}

	public void collect(MavenPom pom, ProjectContext context) {
		try {

			String cppunitResulstPath = (String) pom.getProperty("sonar.cppunit.reportsPath");
			logger.info("sonar.cppunit.reportsPath="+cppunitResulstPath);
			
			String surefireResultsPath = (String) pom.getProperty("sonar.surefire.reportsPath");
			logger.info("sonar.surefire.reportsPath="+surefireResultsPath);

			if (cppunitResulstPath==null){
				logger.info("The cppunit metric is not activated.");
				return;
			}
			
			CppUnitTransformer transformer = new CppUnitTransformer();
			String[] cppunitFiles = CppFileSetUtil.findCppUnitReports(new File(cppunitResulstPath));

			if (cppunitFiles.length > 0) {
				
				File junitOutputPath = new File(surefireResultsPath);
				if (!junitOutputPath.exists()){
					junitOutputPath.mkdirs();
				}

				for (String cppunitFileName : cppunitFiles) {
					
					logger.debug("Process the cppunit file name :" +cppunitFileName);	
					
					File fileCppunitResult = new File(cppunitResulstPath, cppunitFileName);
					FileInputStream fileStream = new FileInputStream(fileCppunitResult);
					String fileCppunitReportName = fileCppunitResult.getName();
					try {
						transformer.transform(fileCppunitReportName, fileStream,junitOutputPath);
					} catch (Exception te) {
            throw new IOException("Could not transform the Cppunit report." + te);
						// Java6 only : throw new IOException("Could not transform the Cppunit report.", te);
					} finally {
						fileStream.close();
					}
				}
			}
		} 
		catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

	


}
