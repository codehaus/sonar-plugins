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
        }});

        SonarJSensor sensor = new SonarJSensor(config, null, null);

        final SensorContext sensorContext = context.mock(SensorContext.class);
        
        context.checking(new Expectations() {{
            atLeast(1).of(sensorContext).saveMeasure(with(any(Measure.class)));
            atLeast(1).of(sensorContext).saveMeasure(with(any(Metric.class)), with(any(Double.class)));
            allowing(sensorContext).getResource(with(any(String.class))); will(returnValue(null)); 
        }});
        sensor.analyse(null, sensorContext, report);
        
        context.assertIsSatisfied();
    }
}
