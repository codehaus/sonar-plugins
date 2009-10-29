package com.hello2morrow.sonarplugin;

import java.io.File;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

public class SonarJPluginHandler implements MavenPluginHandler
{
	private static final String REPORT_DIR = "/sonarj-sonar-plugin";
    private static final String ARTIFACT_ID = "maven-sonarj-plugin";
    private static final String GROUP_ID = "com.hello2morrow.sonar";
    
    private String target;
    private String licenseFileName;
    private boolean usesArchitectureDescription = false;
    
    protected SonarJPluginHandler(String licenseFileName)
    {
        this.licenseFileName = licenseFileName;
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
        return "5.0.2";
    }

    public final boolean isFixedVersion()
    {
        return true;
    }
    
    public final void configure(Project project, MavenPlugin plugin)
    {
        target = project.getFileSystem().getBuildDir().getPath();
        plugin.setParameter("reportDir", target+REPORT_DIR);  
        plugin.setParameter("reportName", "sonarj-report");
        plugin.setParameter("context", "Sonar");
        if (licenseFileName != null && licenseFileName.length() > 0)
        {
            plugin.setParameter("license", licenseFileName);
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
        
        return target+REPORT_DIR+"/sonarj-report.xml";
    }
}
