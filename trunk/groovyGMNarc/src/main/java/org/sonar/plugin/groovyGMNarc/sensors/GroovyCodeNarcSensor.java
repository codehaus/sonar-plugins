/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Scott K.
 * mailto: skuph_marx@yahoo.com
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
 *
 */

package org.sonar.plugin.groovyGMNarc.sensors;

import org.sonar.plugin.groovyGMNarc.GroovyCodeNarcPlugin;
import org.sonar.plugin.groovyGMNarc.GroovyFile;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.maven.project.MavenProject;
import org.sonar.plugin.groovyGMNarc.Groovy;

import java.text.ParseException;
import java.util.*;
import java.io.File;

import groovy.util.ConfigSlurper;
import groovy.util.ConfigObject;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.rules.Violation;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.plugin.groovyGMNarc.groovy.*;


public class GroovyCodeNarcSensor implements Sensor {


    Logger logger = LoggerFactory.getLogger(getClass());
    RulesManager rulesManager = null;
    RulesProfile rulesProfile = null;
    List<CodeNarcViolation> lineNumResults = null;
    List<CodeNarcViolation> methodNumResults = null;
    List<CodeNarcViolation> methodRollupResults = null;
    List<GMetricMetric> gMetricMetrics = null;
    List<String> uniquePackageList = null;
    HashMap<String,Resource> currentResources = new HashMap();
    public static final int CODENARC = 1;
    public static final int GMETRICS = 2;

    public static final Metric MESSAGE = new Metric("message_key", "Message",
    "This is a metric to store a well known message", Metric.ValueType.STRING, -1, false,
    CoreMetrics.DOMAIN_GENERAL);

    private static HashMap<String,Metric> rulesCats = new HashMap();

    /**
     * This is the main entry point for the sensor
      * @param rulesManager
     * @param rulesProfile
     */
    public GroovyCodeNarcSensor(RulesManager rulesManager, RulesProfile rulesProfile) {
  
        this.rulesManager = rulesManager;
        this.rulesProfile = rulesProfile;

        rulesCats.put("Maintainability",CoreMetrics.MAINTAINABILITY);
        rulesCats.put("Reliability",CoreMetrics.RELIABILITY);
        rulesCats.put("Efficiency",CoreMetrics.EFFICIENCY);
        rulesCats.put("Portability",CoreMetrics.PORTABILITY);
        rulesCats.put("Usability",CoreMetrics.USABILITY);
    }

    /**
     *  Is this a groovy project?
     * @param project
     * @return
     */
  public boolean shouldExecuteOnProject(Project project) {
      boolean ret = false;
        if(project.getLanguageKey().equals(Groovy.KEY)) {
          ret = true;
          addSourceDirsToProject(project);
      }

    return ret;
  }

    /**
     *   Add a bunch of folders to the current project scope. These will be used when trying to find source files and such
     * @param project  The current project
     */
  private void addSourceDirsToProject(Project project)
  {
       MavenProject mp = project.getPom();
       if(mp != null) {
           String basePath = mp.getBasedir().getPath();
           project.getFileSystem().addSourceDir(new File(basePath + "/grails-app/domain"));
           project.getFileSystem().addSourceDir(new File(basePath + "/grails-app/controllers"));
           project.getFileSystem().addSourceDir(new File(basePath + "/grails-app/taglib"));
           project.getFileSystem().addSourceDir(new File(basePath + "/grails-app/utils"));
           project.getFileSystem().addSourceDir(new File(basePath + "/grails-app/services"));
           project.getFileSystem().addSourceDir(new File(basePath + "/grails-app/conf"));
           project.getFileSystem().addSourceDir(new File(basePath + "/src/groovy"));
           project.getFileSystem().addSourceDir(new File(basePath + "/src/java"));
       }
  }

    /**
     * Analyse the current project. This involves calling out to read in the CodeNarc file and the GMetrics.
     * I am amused that the British variation on the spelling is being used here. Don't know why, just am.
     *
     * @param project
     * @param sensorContext
     */
  public void analyse(Project project, SensorContext sensorContext) {
// Read in the groovy config file to get the name of the generated XML report we will need to read
    String reportPath = getReportName(project,CODENARC);
    String gMetricsReportPath =  getReportName(project,GMETRICS);

    try {
        loadCodeNarcResults(reportPath,sensorContext, project);
        loadGMetricsResults(gMetricsReportPath, sensorContext, project);
    } catch (ParseException e) {
        logger.error("Parse Exception parsing the CodeNarc/GMetrics results! "+e.getMessage());
    }

    saveLabelMeasure(sensorContext);
    saveNumericMeasure(sensorContext,project,this.currentResources);

//      logger.info("Inside analyse");
  }


    /**
     *  Compute and save measures like the number of lines, files,
     *  packages, methods, etc.
     * @param context
     * @param project
     * @param resources
     */
  private void saveNumericMeasure(SensorContext context,Project project, HashMap<String,Resource> resources) {
//      saveCodeNarcNumericMeasure(context, project);
      saveGMetricsNumericMeasure(context, project,resources);
  }




    /**
     *  Compute the LINES, FILES, & PACKAGES based on return values from
     * CodeNarc. Because we have added a new metric to CodeNarc each and every
     * file should be appropriately accounted for here.If theat new metric is not
     * included then only files that have violations will appear here.
     * @param context
     * @param project
     */
  private void saveCodeNarcNumericMeasure(SensorContext context, Project project)
    {
//      logger.info("Save Numeric Measure");
      ArrayList packages = new ArrayList();

//
// Add up all the line numbers and save the total number of lines
      double num=0;
      double numFiles = 0;

        for(CodeNarcViolation cnv: this.lineNumResults) {
            if(!cnv.getPath().startsWith("plugins")) {
               try {
                  num = num + ParsingUtils.parseNumber(cnv.getLineNum());
                  numFiles++;
                  if(!packages.contains(cnv.getPath())) {
                      packages.add(cnv.getPath());
                  }
               } catch (ParseException e) {
                   logger.error("Parse Exception parsing CodeNarc measures: "+e.getMessage());
               }
            }
        }
      context.saveMeasure(CoreMetrics.LINES, num);
      context.saveMeasure(CoreMetrics.FILES, numFiles);
      if(packages.size() > 0) {
          context.saveMeasure(CoreMetrics.PACKAGES,(double)packages.size());
      }

//     computeMethodMeasures(context,project);
  }

    /**
     *  Save the GMetrics measures for SOnar to use
     * @param context
     * @param project
     * @param resources
     */
    private void saveGMetricsNumericMeasure(SensorContext context, Project project, HashMap<String,Resource>resources)
    {
        double dtmp = 0.0;
        double numMethods = 0;
        GMetricMetric m = null;
         //   context.saveMeasure(resource, measure.getMetric(), measure.getValue());
        Set<String> keys = resources.keySet();
        for(String theKey: keys) {
           Resource resource = resources.get(theKey);
//
// Find the GMetric for this particular resource and grab the complexity  .
// FOr the moment assuming 1 class per file so file complexity and class complexity are the same.
           m = findMetricByResource(resource);
           if(m != null) {
//
// I don't know why we need both CLASS_COMPLEXITY and COMPLEXITY, but if we don't put COMPLEXITY here then we don't get the
// distribution chart. It seems to be the distribution should be computed from the CLASS_COMPLEXITY, but apparently I don't understand what it is.
              try {
                  context.saveMeasure(resource, CoreMetrics.CLASS_COMPLEXITY, Double.parseDouble((String)m.getAbcTotal()));
                  context.saveMeasure(resource, CoreMetrics.COMPLEXITY, Double.parseDouble((String)m.getAbcTotal()));
                  context.saveMeasure(resource, CoreMetrics.FILE_COMPLEXITY, Double.parseDouble((String)m.getAbcTotal()));
                  context.saveMeasure(resource, CoreMetrics.LINES, Double.parseDouble((String)m.getNumLines()));
//
// Right now we have no way of knowing NCLOC so until CodeNarc or GMetrics supports that we just put in all the lines which will not be
// correct but it's as close as we can get right now.
                  context.saveMeasure(resource, CoreMetrics.NCLOC, Double.parseDouble((String)m.getNumLines()));
                  context.saveMeasure(resource, CoreMetrics.STATEMENTS, Double.parseDouble((String)m.getMethodLines()));
                  dtmp = Double.parseDouble(Integer.toString(m.getNumMethods()));
                  context.saveMeasure(resource, CoreMetrics.FUNCTIONS, dtmp);

                  context.saveMeasure(resource, CoreMetrics.FUNCTION_COMPLEXITY, Double.parseDouble((String)m.getAbcTotal()) / dtmp);
                  numMethods += dtmp;
              } catch (Exception e) {
                   logger.error("Exception Caught Computing GMetric Measures: "+e.getMessage());
              }
           }
        }

        m = findMetricByPackageName("PackageSummary");   // This name is set in GMetricsResults
//
// Total complexity and avg. complexity per class(also just set complexity per file to the same for now)
        double totalComp = Double.parseDouble((String)m.getAbcTotal());
        context.saveMeasure(CoreMetrics.COMPLEXITY,totalComp);
        context.saveMeasure(CoreMetrics.CLASS_COMPLEXITY,Double.parseDouble((String)m.getAvgAbc()));
        context.saveMeasure(CoreMetrics.FILE_COMPLEXITY,Double.parseDouble((String)m.getAvgAbc()));
        
//        context.saveMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION,Double.parseDouble((String)m.getAbcTotal()));
//
// Total number of statementes across the entire project
        context.saveMeasure(CoreMetrics.STATEMENTS, Double.parseDouble((String)m.getMethodLines()));
//
// Total number of lines across the entire project
        double totLines = Double.parseDouble((String)m.getNumLines());
		context.saveMeasure(CoreMetrics.LINES, totLines);
//
// If we take the total lines and avg. per class that should give us an approximation of the number of files (assuming 1 class per file)
// so it will be off a little if you have more than 1 class inside a file.
        double avgLinesPerClass = Double.parseDouble((String)m.getAvgLines());
        if(avgLinesPerClass > 0) {
           context.saveMeasure(CoreMetrics.FILES, totLines/avgLinesPerClass);
        }
//
//  Save the number of unique package names
        context.saveMeasure(CoreMetrics.PACKAGES,(double)this.uniquePackageList.size());

//
// We can estimate the avg. complexity per function here
        if(numMethods > 0.0) {
           context.saveMeasure(CoreMetrics.FUNCTION_COMPLEXITY, totalComp /numMethods );
        } else {
            logger.error("saveGMetricsNumericMeasure: numMethods is ZERO and should not be. ");
        }

    // Can compute overall function_complexity here as total complexity / total_num_methods
    // maybe can move the above into the decorator and set in the Groovy instace the metrics list and pull values out when decorating?

    }

    /**
     *
     * @param resource
     * @return
     */
    private GMetricMetric findMetricByResource(Resource resource) {
         GMetricMetric ret=null;
         for(GMetricMetric metric: this.gMetricMetrics) {
            if(resource.getLongName().equals(metric.getLongName())) {
                 ret = metric;
                 break;
            }
        }
        return ret;
    }

    /**
     * Given a package name, find the saved resource for it from the GMetrics stuff
     * @param pkg The package to look for
     * @return
     */
    private GMetricMetric findMetricByPackageName(String pkg) {
         GMetricMetric ret=null;
         for(GMetricMetric metric: this.gMetricMetrics) {
            if(pkg.equals(metric.getPackageName())) {
                 ret = metric;
                 break;
            }
        }
        return ret;
    }

    /**
     *  Compute the size of functions in a project
     * @param context
     * @param project
     */
  private void computeMethodMeasures(SensorContext context,Project project)
  {

     for(CodeNarcViolation val:this.methodRollupResults)   {
         Resource resource = toResource(val.getFileName(),val.getPath(),project);
         if(resource != null) {
//            logger.info("adding measure "+val.getValue() + " for resource: "+resource.getLongName());
            context.saveMeasure(resource, CoreMetrics.FUNCTIONS, (double)val.getValue());
         }  else {
             logger.error("ComputeMethodMeasures: null Resource for "+val.getFileName());
         }
     }


  }

  private void saveLabelMeasure(SensorContext context) {
 //   Measure measure = new Measure(this.MESSAGE, "Hello World!");
 //   context.saveMeasure(measure);
  }

    /**
     * Get the file name of the CodeNarc report file
     * @param project
     * @param type
     * @return
     */
  private String getReportName(Project project, int type) {
      String path = "";
      String codeNarcFile = "";

// If we require the codeNarcFile name in the POM this will work
//      Properties props = project.getPom().getProperties();
//      String codeNarcFile = props.getProperty("sonar.codeNarcFile");
//      logger.info("codeNarcFile="+codeNarcFile);

//
// Read the codeNarcFile name from the Groovy Config.groovy file, that way the user can name it whatever they want
// and not have to worry about it elsewhere.
      MavenProject mp = project.getPom();
      String basePath = mp.getBasedir().getPath();

      CodeNarcReport cnr = new CodeNarcReport();
      if(type == CODENARC) {
        codeNarcFile = cnr.getReportName(basePath);
      } else {
        codeNarcFile = cnr.getGMetricsReportName(basePath);
      }

      return codeNarcFile;
  }


    /**
     *  Call out to Groovy to read in the codenarc
     *  report and return a list of violations. Then
     *  loop through the violations and set anything
     *  that is active
     * @param reportPath
     * @param sensorContext
     * @param project
     * @throws ParseException
     */
  private void loadCodeNarcResults(String reportPath,SensorContext sensorContext, Project project) throws ParseException
  {
 
      Rule rule=null;
      ActiveRule activeRule = null;
      CodeNarcResults cnr = new CodeNarcResults();
      List<CodeNarcViolation> violations = cnr.parseCodeNarcFile(reportPath,true);
      this.lineNumResults = (List<CodeNarcViolation>)cnr.lineNumResults;
      this.methodNumResults = (List<CodeNarcViolation>)cnr.methodLineNumResults;
      this.methodRollupResults = (List<CodeNarcViolation>)cnr.methodRollupResults;

      for(CodeNarcViolation cnv: violations) {
          if(rulesManager != null) {
            rule = this.rulesManager.getPluginRule(GroovyCodeNarcPlugin.KEY, cnv.getRuleName());
          }
          if(rulesProfile != null) {
            activeRule = rulesProfile.getActiveRule(GroovyCodeNarcPlugin.KEY, cnv.getRuleName());
          }
//
// If the rule is active then setup the violation. Ignore package violations that say no package is bad as there are many like that in Groovy (the conf folder, e.g.)
          if(activeRule != null) {
              Resource r = toResource(cnv,project);
              if(!checkIgnorePackageViolation((GroovyFile)r,cnv.getRuleName())) {
                   registerViolation(sensorContext,cnv.getLineNum(),rule,r);
              }


          }
      }
  }


    /**
     * Call out to Groovy to read in the codenarc
     * report and return a list of violations. Then
     * loop through the violations and set anything
     * that is active
     * @param reportPath
     * @param sensorContext
     * @param project
     * @throws ParseException
     */
  private void loadGMetricsResults(String reportPath,SensorContext sensorContext, Project project) throws ParseException
 {
      Rule rule=null;
      ActiveRule activeRule = null;
      GMetricsResults gmr = new GMetricsResults();
      this.gMetricMetrics = gmr.parseGMetricsFile(reportPath,true);
      this.uniquePackageList = gmr.getUniquePackageNames();
  }


    /**
     *  We ignore certain PackageName violations where the package name is non-existant
     * because this is really fine in parts ofGrails apps.  It may change later or perhaps this should be configurable.
     * @param r   The GroovyFile that will have a package name.
     * @param violation
     * @return
     */
  private boolean checkIgnorePackageViolation(GroovyFile r, String violation)
  {
      boolean val = false;
      if(violation.equals("PackageName") && (r.getPackageKey() == null || r.getPackageKey().equals("[default]")) ) {
           val = true;
      }

      return val;
  }


    /**
     * Translate this fileName and path to a
     * resource that Sonar can use. If grails-app
     * or src/groovy, src/java appear in the path we are going
     * to remove that part so it doesn't think that those
     * are part of the package name.
     * @param fileName    File name
     * @param filePath    File path
     * @param project     Current project
     * @return
     */
  private Resource toResource(String fileName, String filePath,Project project) {
     String pathAppend = "/grails-app";
     Resource resource = null;
     MavenProject mp = project.getPom();
     String tmp = StringUtils.substringAfter(filePath, "grails-app/");   // everything after "grails-app/"
     if(tmp == null || tmp == "") {
         tmp = StringUtils.substringAfter(filePath,"src/groovy/");
         pathAppend = "/src/groovy";
         if(tmp == null || tmp == "") {
             tmp = StringUtils.substringAfter(filePath,"src/java/");
             pathAppend = "/src/java";
         }
     }
     String path = tmp + "/" + fileName;
     String basePath = "";
     if(mp != null) {
         basePath = mp.getBasedir().getPath() + pathAppend;
         path = basePath + "/" + path;
     }
//
// See if we already created this resource and just reuse it
     resource = currentResources.get(path);

     if(resource == null) {
         resource = GroovyFile.fromAbsolutePath(path,project.getFileSystem().getSourceDirs(), false);
         this.currentResources.put(path,resource);
     }
     return resource;

  }

  private Resource toResource(CodeNarcViolation cnv,Project project) {

    return toResource(cnv.getFileName(), cnv.getPath(),  project);

  }

    /**
     * Registre a violation for the given resource
     * @param context
     * @param violationLineNumber
     * @param rule
     * @param groovyFile
     */
  private void registerViolation(SensorContext context, String violationLineNumber, Rule rule, Resource groovyFile) {
    try {
      context.saveViolation(new Violation(rule, groovyFile).setMessage(rule.getDescription()).setLineId((int) ParsingUtils.parseNumber(violationLineNumber)));
//      setViolationCountForResource(groovyFile,rule,context);                   // rule.priority.name
    } catch (ParseException e) {
      throw new SonarException("Unable to parse number '" + violationLineNumber + "' in file", e);
    }
  }

    /**
     * For a given resource and rule we will set either MAJOR or MINOR count. TODO: Add other types. Currently not used
     * @param groovyFile
     * @param rule
     * @param context
     */
  private void setViolationCountForResource(Resource groovyFile, Rule rule,SensorContext context) {
      String type = rule.getPriority().toString();
      Metric met = null;
      if("MAJOR".equals(type)) {
          met = CoreMetrics.MAJOR_VIOLATIONS;
      } else if("MINOR".equals(type)) {
          met = CoreMetrics.MINOR_VIOLATIONS;
      }

      addToMeasure(groovyFile, met, context);

      type = rule.getRulesCategory().getName();
      met = this.rulesCats.get(type);
      if(met != null) {
         addToMeasure(groovyFile,met,context);
      }

//      met = CoreMetrics.VIOLATIONS;
//      addToMeasure(groovyFile,met,context);
  }
    
    private void addToMeasure(Resource groovyFile, Metric met, SensorContext context) {

      Measure mval = context.getMeasure(groovyFile,met);
      if(mval != null) {
         double cval = mval.getValue();
         mval.setValue(mval.getValue() + 1.0);
      } else {
          context.saveMeasure(groovyFile, met,1.0);
      }
  }

}
