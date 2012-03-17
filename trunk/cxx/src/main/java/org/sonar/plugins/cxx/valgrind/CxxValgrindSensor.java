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
package org.sonar.plugins.cxx.valgrind;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;
import org.sonar.plugins.cxx.CxxSensor;

public class CxxValgrindSensor extends CxxSensor {
  public static final String REPORT_PATH_KEY = "sonar.cxx.valgrind.reportPath";
  private static final String DEFAULT_REPORT_PATH = "valgrind-reports/valgrind-result-*.xml";
  private static Logger logger = LoggerFactory.getLogger(CxxValgrindSensor.class);
  
  private RuleFinder ruleFinder = null;
  private Configuration conf = null;
  
  public CxxValgrindSensor(RuleFinder ruleFinder, Configuration conf) {
    this.ruleFinder = ruleFinder;
    this.conf = conf;
  }

  public void analyse(Project project, SensorContext context) {
    File[] reports = getReports(conf, project.getFileSystem().getBasedir().getPath(),
                                REPORT_PATH_KEY, DEFAULT_REPORT_PATH);
    for (File report : reports) {
      parseReport(project, report, context);
    }
  }

  private void parseReport(final Project project, File xmlFile, final SensorContext context) {
    try {
      logger.info("parsing valgrind report '{}'", xmlFile);
      
      StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {

        public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
          try {
            Map<String, FileData> fileDataPerFilename = new HashMap<String, FileData>();
            rootCursor.advance();
            collectError(project, rootCursor.childElementCursor("error"), fileDataPerFilename, context);
            for (FileData d : fileDataPerFilename.values()) {
              d.saveMetric(project, context);
            }
          } catch (ParseException e) {
            e.printStackTrace();
            throw new XMLStreamException(e);
          }
        }
      });
      parser.parse(xmlFile);
    } catch (XMLStreamException e) {
      e.printStackTrace();
      throw new XmlParserException(e);
    }
  }

  private class Frame {

    private String fn = "";
    private String dir = "";
    private String file = "";
    private String line = "";

    public String getText() {
      return "Function :" + fn + " at line :" + line;
    }
  }

  private class ValgrindError {

    private List<Frame> stack = new ArrayList<Frame>();
    private List<String> comments = new ArrayList<String>();
    private String kind = "";
    private String unique = "";
    public Map<String, List<String>> LinesErrorPerFileKey = new HashMap<String, List<String>>();

    public String getText() {
      StringBuilder res = new StringBuilder();
      for (String comment : comments) {
        res.append(comment + "\n");
      }
      res.append("Stack trace : \n");
      for (Frame f : stack) {
        res.append(f.getText() + "\n");
      }
      return res.toString();
    }
  }

  private class FileData {

    FileData(org.sonar.api.resources.File f) {
      file = f;
    }
    
    private org.sonar.api.resources.File file;

    public void saveMetric(Project project, SensorContext context) {

      for (ValgrindError error : fileErrors.values()) {
        Rule rule = ruleFinder.findByKey(CxxValgrindRuleRepository.REPOSITORY_KEY, error.kind);
        if (rule != null) {
          List<String> Lines = error.LinesErrorPerFileKey.get(file.getKey());
          if (Lines != null) {
            List<String> Done = new ArrayList<String>();
            for (String line : Lines) {
              if ( !Done.contains(file.getKey() + error.unique + line)) {
                Object t[] = { file.getKey(), error.getText(), line };
                // logger.info("error (source={}) message={} found at line {}", t);
                Violation violation = Violation.create(rule, file);
                violation.setMessage(error.getText());
                violation.setLineId(Integer.parseInt(line));
                context.saveViolation(violation);
                Done.add(file.getKey() + error.unique + line);
              } else {
                Object t[] = { error.getText(), file.getKey(), line };
                logger.warn("error (message={}) in source={} at line {} already reported", t);
              }
            }
          } else {
            Object t[] = { error.getText(), file.getKey() };
            logger.warn("No Line for error (message={}) in source={} message={}", t);
          }
        } else {
          Object t[] = { error.getText(), file.getKey() };
          logger.warn("No rule for error (message={}) in source={}", t);
        }
      }
    }

    public Map<String, ValgrindError> fileErrors = new HashMap<String, ValgrindError>();
  }

  private void collectError(Project project, SMInputCursor error, Map<String, FileData> fileDataPerFilename, SensorContext context)
      throws ParseException, XMLStreamException {
    while (error.getNext() != null) {
      // logger.info("collectError nodename = {} {}", error.getPrefixedName(), error.getAttrCount());
      SMInputCursor child = error.childElementCursor();
      ValgrindError ValError = new ValgrindError();
      Map<String, org.sonar.api.resources.File> FileInvolved =
        new HashMap<String, org.sonar.api.resources.File>();
      while (child.getNext() != null) {
        if (child.getLocalName().equalsIgnoreCase("unique")) {
          ValError.unique = child.getElemStringValue();
        } else if (child.getLocalName().equalsIgnoreCase("kind")) {
          ValError.kind = child.getElemStringValue();
        } else if (child.getLocalName().matches(".*what.*")) {
          SMInputCursor text = child.childElementCursor("text");
          while (text.getNext() != null) {
            ValError.comments.add(text.getElemStringValue());
          }
        } else if (child.getLocalName().equalsIgnoreCase("stack")) {
          SMInputCursor frame = child.childElementCursor("frame");
          while (frame.getNext() != null) {
            SMInputCursor frameChild = frame.childElementCursor();
            Frame f = new Frame();
            while (frameChild.getNext() != null) {
              if (frameChild.getLocalName().equalsIgnoreCase("fn")) {
                f.fn = frameChild.getElemStringValue();
              } else if (frameChild.getLocalName().equalsIgnoreCase("dir")) {
                f.dir = frameChild.getElemStringValue();
              } else if (frameChild.getLocalName().equalsIgnoreCase("file")) {
                f.file = frameChild.getElemStringValue();
              } else if (frameChild.getLocalName().equalsIgnoreCase("line")) {
                f.line = frameChild.getElemStringValue();
              }
              org.sonar.api.resources.File cxxfile = null;
              if ( !StringUtils.isEmpty(f.file) && !StringUtils.isEmpty(f.dir)) {
                cxxfile = org.sonar.api.resources.File.fromIOFile(new File(f.dir + "/" + f.file), project);
              } else if ( !StringUtils.isEmpty(f.file)) {
                cxxfile = org.sonar.api.resources.File.fromIOFile(new File(f.file), project);
              }
              if (null != cxxfile && fileExist(context, cxxfile) && !StringUtils.isEmpty(f.line)) {
                List<String> Lines = ValError.LinesErrorPerFileKey.get(cxxfile.getKey());
                if (Lines == null) {
                  Lines = new ArrayList<String>();
                  ValError.LinesErrorPerFileKey.put(cxxfile.getKey(), Lines);
                }
                if ( -1 == Lines.indexOf(f.line)) {
                  Lines.add(f.line);
                }
                if (null == FileInvolved.get(cxxfile.getKey())) {
                  FileInvolved.put(cxxfile.getKey(), cxxfile);
                }
              }
            }
            ValError.stack.add(f);
          }
        }
      }
      for (org.sonar.api.resources.File curResource : FileInvolved.values()) {
        FileData data = fileDataPerFilename.get(curResource.getKey());
        if (data == null) {
          data = new FileData(curResource);
          fileDataPerFilename.put(curResource.getKey(), data);
        }
        data.fileErrors.put(ValError.unique, ValError);
      }
    }
  }

  private boolean fileExist(SensorContext context, org.sonar.api.resources.File file) {
    return context.getResource(file) != null;
  }
}
