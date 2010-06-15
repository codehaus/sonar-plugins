/*
 * Sonar-SonarJ-Plugin
 * Open source plugin for Sonar
 * Copyright (C) 2009, 2010 hello2morrow GmbH
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

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.List;
import java.util.Arrays;

public final class SonarJMetrics implements Metrics
{
    public static final Metric ACD = new Metric("sonarj_acd", "ACD", "Average Component Dependency (John Lakos)", Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric NCCD = new Metric("sonarj_nccd", "NCCD", "Normalized Cummulative Component Dependency (John Lakos)", Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric CYCLICITY = new Metric("sonarj_cyclicity", "Cyclicity", "Cyclicity of Project on Package Level", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric BIGGEST_CYCLE_GROUP = new Metric("sonarj_biggest_cycle_group", "Biggest Cycle Group", "Number of Packages in Biggest Cycle Group", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric CYCLIC_PACKAGES = new Metric("sonarj_cyclic_packages", "Cyclic Packages", "Number of Cyclic Packages", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric RELATIVE_CYCLICITY = new Metric("sonarj_relative_cyclicity", "Relative Cyclicity", "Relative Cyclicity on Package Level", Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric INTERNAL_PACKAGES = new Metric("sonarj_packages", "Packages (SonarJ)", "Number of Internal Packages", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);
    
    public static final Metric INSTRUCTIONS = new Metric("sonarj_instructions", "Byte Code Instructions", "Number of Byte Code Instructions", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);
  
    public static final Metric CYCLE_GROUP_SIZE = new Metric("sonarj_cycle_group_size", "Cycle Group Size", "Number of Packages in Same Cycle Group", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric UNASSIGNED_TYPES = new Metric("sonarj_unassigned_types", "Unassigned Types (SonarJ)", "Number of Types not Assigned to any Architectural Artifacts", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric UNASSIGNED_TYPES_PERCENT = new Metric("sonarj_unassigned_types_percent", "Percentage of Unassigned Types (SonarJ)", "Percentage of Types not Assigned to any Architectural Artifacts", Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric EROSION_REFS = new Metric("sonarj_erosion_ref", "Structural Debt (References) ", "Number of References to Cut to Achive Zero Package Cyclicity", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);
    
    public static final Metric EROSION_TYPES = new Metric("sonarj_erosion_types", "Structural Debt (Type Dependencies)", "Number of Type Dependencies to Cut to Achive Zero Package Cyclicity", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric EROSION_COST = new Metric("sonarj_erosion_cost", "Structural Debt (US$)", "Estimated Cost to Repair Structural Erosion", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric EROSION_DAYS = new Metric("sonarj_erosion_days", "Structural Debt (Man Days)", "Estimated Effort to Repair Structural Erosion in Man Days", Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric EROSION_INDEX = new Metric("sonarj_erosion_index", "Structural Debt (Index)", "Structural Erosion Index", Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_COMPLEXITY);

    public static final Metric VIOLATING_TYPES = new Metric("sonarj_violating_types", "Violating Types (SonarJ)", "Number of Types with Outgoing Architecture Violations", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric VIOLATING_TYPES_PERCENT = new Metric("sonarj_violating_types_percent", "Percentage of Violating Types (SonarJ)", "Percentage of Types with Outgoing Architecture Violations", Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric INTERNAL_TYPES = new Metric("sonarj_internal_types", "Internal Types (SonarJ)", "Number of Internal Types", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);

    public static final Metric VIOLATING_DEPENDENCIES = new Metric("sonarj_violating_dependencies", "Violating Dependencies (SonarJ)", "Number of Violating Type Dependencies", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric ARCHITECTURE_VIOLATIONS = new Metric("sonarj_architecture_violations", "Architecture Violations (SonarJ)", "Number of Violating References", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric TYPE_DEPENDENCIES = new Metric("sonarj_type_dependencies", "Type Dependencies (SonarJ)", "Overall Number of Type Dependencies", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);

    public static final Metric JAVA_FILES = new Metric("sonarj_java_files", "Java Files (SonarJ)", "Number of Java Source Files", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_SIZE);
    
    public static final Metric TASKS = new Metric("sonarj_tasks", "Open Tasks (SonarJ)", "Number of Open Tasks", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric TASK_REFS = new Metric("sonarj_task_refs", "Open Tasks - Lines to Change (SonarJ)", "Number of Lines Associated with Open Tasks", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric THRESHOLD_WARNINGS = new Metric("sonarj_threshold_warnings", "Threshold Violations (SonarJ)", "Number of Threshold Violations", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);
    
    public static final Metric DUPLICATE_WARNINGS = new Metric("sonarj_duplicate_warnings", "Dulpicate Code Blocks (SonarJ)", "Number of Duplicate Code Blocks", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_DUPLICATION);

    public static final Metric WORKSPACE_WARNINGS = new Metric("sonarj_workspace_warnings", "Parser Warnings (SonarJ)", "Number of SonarJ Parser Warnings", Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric IGNORED_WARNINGS = new Metric("sonarj_ignored_warnings", "Ignored Warnings (SonarJ)", "Number of Ignord Warnings and Threshold Violations", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_RULES);

    public static final Metric IGNORED_VIOLATONS = new Metric("sonarj_ignored_violations", "Ignored Violations (SonarJ)", "Number of Ignored Type Dependency Violatins", Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
            CoreMetrics.DOMAIN_RULES);

    public List<Metric> getMetrics()
    {
        return Arrays.asList(ACD, NCCD, CYCLICITY, BIGGEST_CYCLE_GROUP, CYCLIC_PACKAGES, RELATIVE_CYCLICITY, INTERNAL_PACKAGES, INSTRUCTIONS, CYCLE_GROUP_SIZE,
        		UNASSIGNED_TYPES, VIOLATING_TYPES, VIOLATING_DEPENDENCIES, TYPE_DEPENDENCIES, JAVA_FILES, TASKS, EROSION_INDEX,
        		THRESHOLD_WARNINGS, WORKSPACE_WARNINGS, IGNORED_WARNINGS, IGNORED_VIOLATONS, ARCHITECTURE_VIOLATIONS, EROSION_REFS, 
        		EROSION_TYPES, EROSION_COST, EROSION_DAYS, INTERNAL_TYPES, TASK_REFS, UNASSIGNED_TYPES_PERCENT, VIOLATING_TYPES_PERCENT, DUPLICATE_WARNINGS);
    }
}
