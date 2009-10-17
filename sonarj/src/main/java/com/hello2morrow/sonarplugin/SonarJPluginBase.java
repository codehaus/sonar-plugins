package com.hello2morrow.sonarplugin;

public final class SonarJPluginBase
{
    public final static String PLUGIN_KEY = "SonarJ";
    public final static String ARCH_RULE_KEY = "sonarj.architecture";
    public final static String THRESHOLD_RULE_KEY = "sonarj.threshold";
    public final static String TASK_LOW_RULE_KEY = "sonarj.low_rule";
    public final static String TASK_MEDIUM_RULE_KEY = "sonarj.medium_rule";
    public final static String TASK_HIGH_RULE_KEY = "sonarj.high_rule";
    
    private SonarJPluginBase()
    {
        // Don't instantiate
    }
}
