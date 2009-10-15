package com.hello2morrow.sonarplugin;

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.List;
import java.util.Arrays;

public final class SonarJMetrics implements Metrics
{
    public static final Metric ACD = new Metric("sonarj.acd", "ACD", "Average Component Dependency (John Lakos)", Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric NCCD = new Metric("sonarj.nccd", "NCCD", "Normalized Cummulative Component Dependency (John Lakos)", Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric CYCLICITY = new Metric("sonarj.cyclicity", "Cyclicity", "Cyclicity of Project on Package Level", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric BIGGEST_CYCLE_GROUP = new Metric("sonarj.biggest_cycle_group", "Biggest Cycle Group", "Number of Packages in Biggest Cycle Group", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric CYCLIC_PACKAGES = new Metric("sonarj.cyclic_packages", "Cyclic Packages", "Number of Cyclic Packages", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric RELATIVE_CYCLICITY = new Metric("sonarj.relative_cyclicity", "Relative Cyclicity", "Relative Cyclicity on Package Level", Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric INTERNAL_PACKAGES = new Metric("sonarj.packages", "Packages (SonarJ)", "Number of Internal Packages", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);
    
    public static final Metric INSTRUCTIONS = new Metric("sonarj.instructions", "Byte Code Instructions", "Number of Byte Code Instructions", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);
  
    public static final Metric CYCLE_GROUP_SIZE = new Metric("sonarj.cycle_group_size", "Cycle Group Size", "Number of Packages in Same Cycle Group", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric CYCLE_GROUP_ID = new Metric("sonarj.cycle_group_id", "Cycle Group Id", "Id of Cycle Group", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric UNASSIGNED_TYPES = new Metric("sonarj.unassigned_types", "Unassigned Types (SonarJ)", "Number of Types not Assigned to any Architectural Artifacts", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric VIOLATING_TYPES = new Metric("sonarj.violating_types", "Violating Types (SonarJ)", "Number of Types with Outgoing Architecture Violations", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric VIOLATING_DEPENDENCIES = new Metric("sonarj.violating_dependencies", "Violating Dependencies (SonarJ)", "Number of Violating Type Dependencies", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric ARCHITECTURE_VIOLATIONS = new Metric("sonarj.architecture_violations", "Architecture Violations (SonarJ)", "Number of Violating Artifact Dependencies", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric CYCLIC_ARTIFACTS = new Metric("sonarj.cyclic_artifacts", "Cyclic Architecture Artifacts (SonarJ)", "Number of Architectural Artifacts Involved in Cyclic Dependencies", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric TYPE_DEPENDENCIES = new Metric("sonarj.type_dependencies", "Type Dependencies (SonarJ)", "Overall Number of Type Dependencies", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);

    public static final Metric JAVA_FILES = new Metric("sonarj.java_files", "Java Files (SonarJ)", "Number of Java Source Files", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);
    
    public static final Metric TASKS = new Metric("sonarj.tasks", "Open Tasks (SonarJ)", "Number of Open Tasks", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric THRESHOLD_WARNINGS = new Metric("sonarj.threshold_warnings", "Threshold Violations (SonarJ)", "Number of Threshold Violations", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric WORKSPACE_WARNINGS = new Metric("sonarj.workspace_warnings", "Parser Warnings (SonarJ)", "Number of SonarJ Parser Warnings", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric CONSISTENCY_WARNINGS = new Metric("sonarj.consistency_warnings", "Architecture Consistency Warnings (SonarJ)", "Number of Architecture Cnsistency Warnings", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric IGNORED_WARNINGS = new Metric("sonarj.ignored_warnings", "Ignored Warnings (SonarJ)", "Number of Ignord Warnings and Threshold Violations", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric IGNORED_VIOLATONS = new Metric("sonarj.ignored_violations", "Ignored Violations (SonarJ)", "Number of Ignored Type Dependency Violatins", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_RULES);

    public List<Metric> getMetrics()
    {
        return Arrays.asList(ACD, NCCD, CYCLICITY, BIGGEST_CYCLE_GROUP, CYCLIC_PACKAGES, RELATIVE_CYCLICITY, INTERNAL_PACKAGES, INSTRUCTIONS, CYCLE_GROUP_SIZE,
        		CYCLE_GROUP_ID, UNASSIGNED_TYPES, VIOLATING_TYPES, VIOLATING_DEPENDENCIES, CYCLIC_ARTIFACTS, TYPE_DEPENDENCIES, JAVA_FILES, TASKS,
        		THRESHOLD_WARNINGS, WORKSPACE_WARNINGS, IGNORED_WARNINGS, IGNORED_VIOLATONS, ARCHITECTURE_VIOLATIONS, CONSISTENCY_WARNINGS);
    }
}
