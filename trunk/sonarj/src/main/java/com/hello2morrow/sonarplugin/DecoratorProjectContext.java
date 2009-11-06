package com.hello2morrow.sonarplugin;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

public class DecoratorProjectContext implements ProjectContext
{
    private DecoratorContext context;
    
    public DecoratorProjectContext(DecoratorContext context)
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
