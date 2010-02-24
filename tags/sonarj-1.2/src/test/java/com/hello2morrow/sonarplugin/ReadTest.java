/*
 * Sonar-SonarJ-Plugin
 * Open source plugin for Sonar
 * Copyright (C) 2009 hello2morrow GmbH
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

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

import com.hello2morrow.sonarplugin.xsd.ReportContext;

public class ReadTest extends TestCase
{
    private Mockery context = new Mockery();
    
    public void testAnalyse()
    {
        ReportContext report = SonarJSensor.readSonarjReport("src/test/resources/sonarj-report.xml");
        
        assertNotNull(report);

        final Configuration config = context.mock(Configuration.class);
        
        context.checking(new Expectations() {{
            oneOf(config).getString(SonarJSensor.LICENSE_FILE_NAME); will(returnValue(null));
            oneOf(config).getDouble(SonarJSensor.DEVELOPER_COST_PER_HOUR, 70.0); will(returnValue(70.0));
        }});

        SonarJSensor sensor = new SonarJSensor(config, null, null);

        final SensorContext sensorContext = context.mock(SensorContext.class);
        
        context.checking(new Expectations() {{
            atLeast(1).of(sensorContext).saveMeasure(with(any(Measure.class)));
            allowing(sensorContext).getResource(with(any(String.class))); will(returnValue(null)); 
            allowing(sensorContext).getMeasure(with(any(Metric.class))); will(returnValue(null)); 
        }});
        sensor.analyse(sensorContext, report);
        
        context.assertIsSatisfied();
    }
}
