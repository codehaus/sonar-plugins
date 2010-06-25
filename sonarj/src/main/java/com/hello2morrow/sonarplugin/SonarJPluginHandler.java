/*
 * Sonar-SonarJ-Plugin
 * Open source plugin for Sonar
 * Copyright (C) 2009, 2010 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hello2morrow.sonarplugin;

import java.io.File;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

public class SonarJPluginHandler implements MavenPluginHandler
{
	private static final String REPORT_DIR = "sonarj-sonar-plugin";
    private static final String ARTIFACT_ID = "maven-sonarj-plugin";
    private static final String GROUP_ID = "com.hello2morrow.sonar";
    
    private String target;
    private final String licenseFileName;
    private final String activationCode;
    private boolean usesArchitectureDescription = false;
    
    protected SonarJPluginHandler(String licenseFileName, String activationCode)
    {
        this.licenseFileName = licenseFileName;
        this.activationCode = activationCode;
    }
    
    public final String getArtifactId()
    {
        return ARTIFACT_ID;
    }

    public final String[] getGoals()
    {
        return new String[] { "sonarj" };
    }

    public final String getGroupId()
    {
        return GROUP_ID;
    }

    public final String getVersion()
    {
        return "6.0.0-SNAPSHOT";
    }

    public final boolean isFixedVersion()
    {
        return true;
    }
    
    public final void configure(Project project, MavenPlugin plugin)
    {
        target = project.getFileSystem().getBuildDir().getPath();
        plugin.setParameter("sonarReportDir", REPORT_DIR);
        plugin.setParameter("sonarReportName", "sonarj-report");
        plugin.setParameter("context", "Sonar");
        plugin.setParameter("aggregate", "false", true);
        if (licenseFileName != null && licenseFileName.length() > 0 && plugin.getParameter("license") == null)
        {
            plugin.setParameter("license", licenseFileName);
        }
        if (activationCode != null && activationCode.length() > 0 && plugin.getParameter("activationCode") == null)
        {
            plugin.setParameter("activationCode", activationCode);
        }
        if (plugin.getParameter("file") != null)
        {
        	usesArchitectureDescription = true;
        }
        else
        {
            String artifactId = project.getArtifactId();
            File file = new File(artifactId+".sonarj");
            
            usesArchitectureDescription = file.exists();
        }
    }
    
    public final boolean isUsingArchitectureDescription()
    {
    	return usesArchitectureDescription;
    }
    
    public final String getReportFileName()
    {
        assert target != null;
        
        return target+"/"+REPORT_DIR+"/sonarj-report.xml";
    }
}
