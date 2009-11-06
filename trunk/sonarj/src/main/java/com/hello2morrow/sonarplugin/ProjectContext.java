package com.hello2morrow.sonarplugin;

import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

public interface ProjectContext
{
    Measure getMeasure(Metric metric);
    
    void saveMeasure(Measure measure);
}
