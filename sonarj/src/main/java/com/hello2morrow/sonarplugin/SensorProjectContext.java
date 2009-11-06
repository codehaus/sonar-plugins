package com.hello2morrow.sonarplugin;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

public final class SensorProjectContext implements ProjectContext
{
    private SensorContext context;
    
    public SensorProjectContext(SensorContext context)
    {
        this.context = context;
    }
    
    public Measure getMeasure(Metric metric)
    {
        return context.getMeasure(metric);
    }

    public void saveMeasure(Measure measure)
    {
        context.saveMeasure(measure);
    }
}
