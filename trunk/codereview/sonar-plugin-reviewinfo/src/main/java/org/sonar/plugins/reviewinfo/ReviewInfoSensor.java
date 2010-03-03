
package org.sonar.plugins.reviewinfo;


import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.utils.XmlParserException;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Measure;

import javax.xml.stream.XMLStreamException;
import com.thoughtworks.xstream.XStreamException;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.sonar.plugins.reviewinfo.xml.Info;
import org.sonar.plugins.reviewinfo.xml.Entry;
import org.sonar.plugins.reviewinfo.ReviewInfoMetrics;
import org.sonar.plugins.reviewinfo.xml.ReviewInfo;
import org.sonar.plugins.reviewinfo.xml.ReviewEntry;


public class ReviewInfoSensor implements Sensor, DependsUponMavenPlugin {

  private RulesProfile profile;
  private RulesManager rulesManager;
  private ReviewInfoMavenPluginHandler pluginHandler;

  private List<Entry>       svninfoList;
  private List<ReviewEntry> reviewStatsList;


  public ReviewInfoSensor(RulesProfile profile, RulesManager rulesManager, ReviewInfoMavenPluginHandler pluginHandler) {
    this.profile = profile;
    this.rulesManager = rulesManager;
    this.pluginHandler = pluginHandler;
  }


  public void analyse(Project project, SensorContext context) {

    // Process the svn info data
    try {
      File svnInfo = new File(project.getFileSystem().getBuildDir(), "svninfo.xml");
      SvnInfoXMLHandler svnParser = new SvnInfoXMLHandler();
      Info info = svnParser.parse(svnInfo);

      svninfoList = info.getContents();

      //DEBUG
      //System.out.println("ReviewInfoSensor: Printing size of svninfoList" + svninfoList.size());


      // Process the reviewstats data
      // if a file is marked as "reviewed", compare its current version from the svn info above, 
      // with the version of file that has been reviewed.
      // If the svn revision is later, it means that the file has changed after it was reviewed. 

      File reviewStatsFile = new File(project.getFileSystem().getBuildDir(), "reviewstats.xml");
      ReviewStatsXMLHandler statsParser = new ReviewStatsXMLHandler();
      ReviewInfo reviewInfo = statsParser.parse(reviewStatsFile);

      reviewStatsList = reviewInfo.getContents();

      //DEBUG
      //System.out.println("ReviewInfoSensor: Printing size of reviewStatsList" + reviewStatsList.size());

 
      for (Entry entry : svninfoList) {
         if (entry.isJavaFile()) {
            String reviewstats = getReviewStatsForFile(entry, reviewStatsList);

            //DEBUG
            /*
            System.out.println("ReviewInfoSensor: Before SaveMeasure");
            System.out.println("project [" + project + "] " +
                               "context [" + context + "] " + 
                               "entry [" +  entry + "] " +
                               "reviewstats [" + reviewstats + "] " );
            */
            saveMeasure(project, context, entry, reviewstats);
         }
      }      
 
    } 
    catch (XStreamException e) {
      throw new XmlParserException(new XMLStreamException("Error"));
    }


  }


  private void saveMeasure(Project project, SensorContext context, Entry entry, String reviewstats) {

     //Create Resource 
     File dir = new File(entry.getJavaParentDir());    
     Resource javaFile = JavaFile.fromAbsolutePath(entry.getPath(), Arrays.asList(dir), entry.isJavaTest());

     // The resource key must match with the one that sonar uses for all the files.  The format of the key is 
     // project group id:project artifact id:package.class

     //Create measure
     String review_data = entry.getUrl() + "|" + entry.getChecksum() + "|" + entry.getRev() + "|" + reviewstats;
     Measure measure_review = new Measure(ReviewInfoMetrics.REVIEW_INFO, review_data);


     if (javaFile == null) {
        System.out.println("ERROR: Resource javaFile could not be created. Metric review_info has not been saved.");
        System.out.println("Debugging Information:"); 
        System.out.println("   project [" + project + "] " +
                           "context [" + context + "] " + 
                           "entry [" +  entry + "] " +
                           "reviewstats [" + reviewstats + "] " );
        System.out.println("   javaParentDir=[" + entry.getJavaParentDir() + "]");
        System.out.println("   File dir = [" + dir + "]");
        System.out.println("   entry.isJavaTest = [" + entry.isJavaTest() + "]");
        System.out.println("   review_data: " + review_data);

        return;
     }

     //TODO Remove Debug
     //System.out.println("javaParentDir=[" + entry.getJavaParentDir() + "]");
     //System.out.println("File dir = [" + dir + "]");
     //System.out.println("entry.isJavaTest = [" + entry.isJavaTest() + "]");
     //System.out.println("Resource: " + javaFile.toString());
     //System.out.println("review_data: " + review_data);
    

     //Save measures
     context.saveMeasure(javaFile, measure_review);
  }


 
  private String getReviewStatsForFile(Entry svnEntry, List<ReviewEntry> reviewStats) {
     String stats = "";

     for (ReviewEntry entry : reviewStats) {
        if (entry.getFileName().equals(svnEntry.getPath())) {
           if ( ! entry.getRevision().equals(svnEntry.getRev())) {
              entry.setFileReviewStatus(false);
           }
           stats = entry.toString();
           break;
        }
     }   

     return stats;  
  }


  public boolean shouldExecuteOnProject(Project project) {
    return project.getFileSystem().hasJavaSourceFiles();        
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    return pluginHandler;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
