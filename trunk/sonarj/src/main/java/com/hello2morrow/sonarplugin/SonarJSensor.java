package com.hello2morrow.sonarplugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.rules.Violation;

import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdArchitectureViolation;
import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroups;
import com.hello2morrow.sonarplugin.xsd.XsdCyclePath;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdProjects;
import com.hello2morrow.sonarplugin.xsd.XsdTypeRelation;
import com.hello2morrow.sonarplugin.xsd.XsdViolations;

public final class SonarJSensor implements Sensor, DependsUponMavenPlugin
{
    public static final String LICENSE_FILE_NAME = "sonarj.license";
    
    private static final Logger LOG = LoggerFactory.getLogger(SonarJSensor.class);

    private static final String ACD = "Average component dependency (ACD)";
    private static final String NCCD = "Normalized cumulative component dependency (NCCD)";
    private static final String INTERNAL_PACKAGES = "Number of internal packages";
    private static final String INSTRUCTIONS = "Number of instructions";    
    private static final String UNASSIGNED_TYPES = "Number of unassigned types";
    private static final String VIOLATING_DEPENDENCIES = "Number of violating type dependencies";
    private static final String VIOLATING_TYPES = "Number of violating types";
    private static final String CYCLIC_LAYER_GROUPS = "Number of cyclic layer groups";
    private static final String CYCLIC_LAYERS = "Number of cyclic layers";
    private static final String CYCLIC_SUBSYSTEMS = "Number of cyclic subsystems";
    private static final String CYCLIC_VERTICAL_SLICE_GROUPS = "Number of cyclic vertical slice groups";
    private static final String CYCLIC_VERTICAL_SLICES = "Number of cyclic vertical slices";
    private static final String TYPE_DEPENDENCIES = "Number of type dependencies (all)";
    private static final String JAVA_FILES = "Number of Java source files (non-excluded)";
    private static final String IGNORED_VIOLATIONS = "Number of ignored violations";
    private static final String IGNORED_WARNINGS = "Number of ignored warnings";
    private static final String TASKS = "Number of tasks";
    private static final String THRESHOLD_WARNINGS = "Number of warnings (thresholds)";
    private static final String WORKSPACE_WARNINGS = "Number of warnings (workspace)";
    
    private final SonarJPluginHandler pluginHandler;
    private Map<String, Number> generalMetrics;
    private Map<String, Number> projectMetrics;
    private SensorContext sensorContext;
    private RulesManager rulesManager;
    private RulesProfile rulesProfile;
    
    protected static ReportContext readSonarjReport(String fileName)
    {
        ReportContext result = null;
        InputStream input = null;
        
        try
        {
            JAXBContext context = JAXBContext.newInstance("com.hello2morrow.sonarplugin.xsd");
            Unmarshaller u = context.createUnmarshaller();
            
            input = new FileInputStream(fileName);
            result = (ReportContext) u.unmarshal(input);
        }
        catch (JAXBException e)
        {
            LOG.error("JAXB Problem in "+fileName, e);
        }
        catch (FileNotFoundException e)
        {
            LOG.error("Cannot open "+fileName, e);
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    LOG.error("Cannot close "+fileName, e);
                }
            }
        }
        return result;
    }
    
    public SonarJSensor(Configuration config, RulesManager rulesManager, RulesProfile rulesProfile)
    {
        String licenseFileName = config.getString(LICENSE_FILE_NAME);
        pluginHandler = new SonarJPluginHandler(licenseFileName);
        this.rulesManager = rulesManager;
        this.rulesProfile = rulesProfile;
        if (rulesManager == null)
        {
            LOG.warn("No RulesManager provided to sensor");
        }
        if (rulesProfile == null)
        {
            LOG.warn("No RulesProfile given to sensor");
        }
    }
    
    public final MavenPluginHandler getMavenPluginHandler(Project proj)
    {
        return pluginHandler;
    }

    public final boolean shouldExecuteOnProject(Project project)
    {
        return true; 
    }

    private Map<String, Number> readAttributes(XsdAttributeRoot root)
    {
        Map<String, Number> result = new HashMap<String, Number>();

        for (XsdAttributeCategory cat : root.getAttributeCategory())
        {
            for (XsdAttribute attr : cat.getAttribute())
            {
                String attrName = attr.getName();
                String value = attr.getValue();
                
                try
                {
	                if (value.indexOf('.') >= 0)
	                {
	                    result.put(attrName, Double.valueOf(value));
	                }
	                else if (value.indexOf(':') == -1)
	                {
	                    result.put(attrName, Integer.valueOf(value));
	                }
                }
                catch (NumberFormatException e)
                {
                	// Ignore this value
                }
            }
        }
        return result;
    }
    
    private void saveMeasure(String key, Metric metric)
    {
    	saveMeasure(key, metric, Double.MAX_VALUE, Double.MAX_VALUE);
    }
    
    private void saveMeasure(Metric metric, double value)
    {
    	saveMeasure(metric, value, Double.MAX_VALUE, Double.MAX_VALUE);    	
    }
    
    private void saveMeasure(String key, Metric metric, double warnThreshold, double errorThreshold)
    {
    	double value = projectMetrics.get(key).doubleValue();
    	
    	saveMeasure(metric, value, warnThreshold, errorThreshold);
    }
    
    private void saveMeasure(Metric metric, double value, double warnThreshold, double errorThreshold)
    {
    	Metric.Level alertLevel = Metric.Level.OK;
    	
    	if (value >= errorThreshold)
    	{
    		alertLevel = Metric.Level.ERROR;
    	}
    	else if (value >= warnThreshold)
    	{
    		alertLevel = Metric.Level.WARN;
    	}
    	saveMeasure(metric, value, alertLevel);
    }
    
    private void saveMeasure(Metric metric, double value, Metric.Level alertLevel)
    {
    	Measure m = new Measure(metric, value);
    	
    	m.setAlertStatus(alertLevel);
    	sensorContext.saveMeasure(m);    	
    }
    
    private String getAttribute(List<XsdAttribute> map, String name)
    {
    	String value = null;
    	
    	for (XsdAttribute attr : map)
    	{
    		if (attr.getName().equals(name))
    		{
    			value = attr.getValue();
    			break;
    		}
    	}
    	return value;
    }
    
    @SuppressWarnings("unchecked")
    private void analyseCycleGroups(ReportContext report, Number internalPackages)
    {
        XsdCycleGroups cycleGroups = report.getCycleGroups();
        double cyclicity = 0;
        double biggestCycleGroupSize = 0;
        double cyclicPackages = 0;
        int cycleGroupId = 0;
        
        for (XsdCycleGroup group : cycleGroups.getCycleGroup())
        {
            if (group.getNamedElementGroup().equals("Package"))
            {
                int groupSize = group.getCyclePath().size();
                
                cycleGroupId++;
                cyclicPackages += groupSize;
                cyclicity += groupSize*groupSize;
                if (groupSize > biggestCycleGroupSize)
                {
                    biggestCycleGroupSize = groupSize;
                }
                for (XsdCyclePath cycleMember : group.getCyclePath())
                {
                    String packageName = cycleMember.getParent();
                    Resource thePackage = sensorContext.getResource(packageName);
                    
                    if (thePackage != null)
                    {
                        sensorContext.saveMeasure(thePackage, SonarJMetrics.CYCLE_GROUP_SIZE, new Double(groupSize));
                        sensorContext.saveMeasure(thePackage, SonarJMetrics.CYCLE_GROUP_ID, new Double(cycleGroupId));
                    }
                    else
                    {
                        LOG.error("Cannot find package resource "+packageName);
                    }
                }
            }
        }

        final double bcgLimit = 5.0; // TODO: make configurable
        
        saveMeasure(SonarJMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize, bcgLimit, 2.0*bcgLimit);
        saveMeasure(SonarJMetrics.CYCLICITY, cyclicity);
        saveMeasure(SonarJMetrics.CYCLIC_PACKAGES, cyclicPackages);
        
        final double rcLimit = 7.5; // TODO: make configurable
        double relativeCyclicity = 100.0 * Math.sqrt(cyclicity) / internalPackages.doubleValue(); 
        
        saveMeasure(SonarJMetrics.RELATIVE_CYCLICITY, relativeCyclicity, rcLimit, 2.0 * rcLimit);

        sensorContext.saveMeasure(SonarJMetrics.INTERNAL_PACKAGES, internalPackages.doubleValue());        
    }
    
    @SuppressWarnings("unchecked")
    private final void addArchitectureMeasures(Project project, ReportContext report)
    {
    	int cyclicArtifacts = 0;
    	
    	for (String key : new String[] { CYCLIC_LAYERS, CYCLIC_LAYER_GROUPS, CYCLIC_VERTICAL_SLICES, CYCLIC_VERTICAL_SLICE_GROUPS, CYCLIC_SUBSYSTEMS })
    	{
    		cyclicArtifacts += projectMetrics.get(key).intValue();
    	}
    	saveMeasure(SonarJMetrics.CYCLIC_ARTIFACTS, cyclicArtifacts, 2.0, 5.0);
    	saveMeasure(UNASSIGNED_TYPES, SonarJMetrics.UNASSIGNED_TYPES, 1, 20);
    	saveMeasure(VIOLATING_TYPES, SonarJMetrics.VIOLATING_TYPES, 20, 50);
    	saveMeasure(VIOLATING_DEPENDENCIES, SonarJMetrics.VIOLATING_DEPENDENCIES, 20, 50);
    	saveMeasure(TASKS, SonarJMetrics.TASKS, 50, 100);
    	saveMeasure(THRESHOLD_WARNINGS, SonarJMetrics.THRESHOLD_WARNINGS, 50, 100);
    	saveMeasure(WORKSPACE_WARNINGS, SonarJMetrics.WORKSPACE_WARNINGS, 10, 20);
    	saveMeasure(IGNORED_VIOLATIONS, SonarJMetrics.IGNORED_VIOLATONS);
    	saveMeasure(IGNORED_WARNINGS, SonarJMetrics.IGNORED_WARNINGS);
    	
    	XsdViolations violations = report.getViolations();
    	
    	saveMeasure(SonarJMetrics.ARCHITECTURE_VIOLATIONS, Double.valueOf(violations.getNumberOf()), 5, 10);
        
    	if (rulesManager != null && rulesProfile != null)
    	{    
        	Rule rule = rulesManager.getPluginRule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.ARCH_RULE_KEY);
            ActiveRule activeRule = rulesProfile.getActiveRule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.ARCH_RULE_KEY);
    
            if (rule != null && activeRule != null)
            {
                for (XsdArchitectureViolation violation : violations.getArchitectureViolations())
            	{
            		String toName = getAttribute(violation.getArchitectureViolation().getAttribute(), "To");
            		String toElemType = getAttribute(violation.getArchitectureViolation().getAttribute(), "To element type").toLowerCase();
            		String target = toElemType+' '+toName;
            		
            		for (XsdTypeRelation rel : violation.getTypeRelation())
            		{
            			String fromType = getAttribute(rel.getAttribute(), "From");
            			String toType = getAttribute(rel.getAttribute(), "To");
            			String msg = "Type "+toType+" from "+target+" must not be used from here";
            			Resource javaFile = sensorContext.getResource(fromType);
            			
            			if (javaFile == null)
            			{
            			    LOG.error("Cannot obtain resource "+fromType);
            			    continue;
            			}
            			for (XsdPosition pos : rel.getPosition())
            			{
            				Violation v = new Violation(rule, javaFile);
            				
            				v.setMessage(msg);
            				v.setLineId(Integer.valueOf(pos.getLine()));
            				v.setPriority(activeRule.getPriority());
            				sensorContext.saveViolation(v);
            				LOG.info("Adding violation");
            			}
            		}
            	}
            }
            else if (rule == null)
            {
                LOG.error("SonarJ architecture rule not found");
            }
            else if (activeRule == null)
            {
                LOG.info("SonarJ architecture rule deactivated");
            }
    	}
    }
    
    protected final void analyse(Project project, SensorContext sensorContext, ReportContext report)
    {
    	this.sensorContext = sensorContext;
        generalMetrics = readAttributes(report.getAttributes());
        
        Number internalPackages = generalMetrics.get(INTERNAL_PACKAGES);
        
        if (internalPackages.intValue() == 0)
        {
        	LOG.warn("No classes found in project "+project.getName());
        	return;
        }
        
        XsdProjects projects = report.getProjects();  
        List<XsdAttributeRoot> projectList = projects.getProject();
        
        if (projectList.size() > 1)
        {
            LOG.error("SonarJ Plugin cannot (yet) handle SonarJ multi project systems");
            return;
        }        
        
        projectMetrics = readAttributes(projectList.get(0));
        
        final double nccdLimit = 6.5; // TODO: Replace by configuration parameter
        double acd = projectMetrics.get(ACD).doubleValue();
        double nccd = projectMetrics.get(NCCD).doubleValue();

        Metric.Level alertLevel = Metric.Level.OK;
        
        if (nccd >= 1.5 * nccdLimit)
        {
            alertLevel = Metric.Level.ERROR;
        }
        else if (nccd  >= nccdLimit)
        {
            alertLevel = Metric.Level.WARN;
        }
        saveMeasure(SonarJMetrics.ACD, acd, alertLevel);
        saveMeasure(SonarJMetrics.NCCD, nccd, alertLevel);
        saveMeasure(INSTRUCTIONS, SonarJMetrics.INSTRUCTIONS);
        saveMeasure(TYPE_DEPENDENCIES, SonarJMetrics.TYPE_DEPENDENCIES);
        saveMeasure(JAVA_FILES, SonarJMetrics.JAVA_FILES);
        analyseCycleGroups(report, internalPackages);
        if (pluginHandler.isUsingArchitectureDescription())
        {
        	addArchitectureMeasures(project, report);
        }
    }
    
    public final void analyse(Project project, SensorContext sensorContext)
    {
        ReportContext report = readSonarjReport(pluginHandler.getReportFileName());
        
        if (report != null)
        {
        	analyse(project, sensorContext, report);        	
        }
    }
}
