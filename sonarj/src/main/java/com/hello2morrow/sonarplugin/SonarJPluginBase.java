package com.hello2morrow.sonarplugin;

public final class SonarJPluginBase
{
    public final static String PLUGIN_KEY = "SonarJ";
    public final static String ARCH_RULE_KEY = "sonarj.architecture";
    public final static String THRESHOLD_RULE_KEY = "sonarj.threshold";
    public final static String TASK_RULE_KEY = "sonarj.open_task";
    
    private SonarJPluginBase()
    {
        // Don't instantiate
    }
}
