/**
 * Copyright (c) 2010 Compuware Corp.
 * Sonar Plugin JaCoCo, open source software Sonar plugin.
 * mailto:anthony.dahanne@compuware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, this permission notice and the below disclaimer shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. WITHOUT LIMITING THE FOREGOING, COMPUWARE MAKES NO REPRESENTATIONS OR WARRANTIES CONCERNING THE COMPLETENESS, ACCURACY OR OPERATION OF THE SOFTWARE.  CLIENT SHALL HAVE THE SOLE RESPONSIBILITY FOR ADEQUATE PROTECTION AND BACKUP OF ITS DATA USED IN CONNECTION WITH THE SOFTWARE.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sonar.plugins.jacoco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.ArrayList;
import java.util.List;

@Properties({
    @Property(
        key = JaCoCoCoreProperties.JACOCO_REPORT_PATH_PROPERTY,
        name = "Report path",
        description = "Path (absolute or relative) to JaCoCo xml report file.",
        project = true,
        global = false)
})
public class JaCoCoPlugin implements Plugin {
  Logger logger = LoggerFactory.getLogger(getClass());

  public JaCoCoPlugin() {
    super();
  }

  public String getKey() {
    return JaCoCoCoreProperties.JACOCO_PLUGIN;
  }

  public String getName() {
    return "JaCoCo";
  }

  public String getDescription() {
    return "JaCoCo is a tool that calculates the percentage of code accessed by tests. It can be used to identify which parts of Java program are lacking test coverage. You can find more by going to the <a href='http://www.eclemma.org/jacoco/'>JaCoCo web site</a>.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(JaCoCoSensor.class);
    list.add(JaCoCoMavenPluginHandler.class);
    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }
}
