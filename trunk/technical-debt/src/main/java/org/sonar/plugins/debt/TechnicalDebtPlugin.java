/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.debt;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.api.Extension;
import org.sonar.plugins.api.Plugin;
import org.sonar.plugins.api.EditableProperty;
import org.sonar.plugins.api.EditableProperties;


@EditableProperties({
    @EditableProperty(key = TechnicalDebtPlugin.TD_DAILY_RATE, defaultValue = TechnicalDebtPlugin.TD_DAILY_RATE_DEFAULT, name = "Daily rate of a developer (in $)", description = ""),
    @EditableProperty(key = TechnicalDebtPlugin.TD_COST_COMP_CLASS, defaultValue = TechnicalDebtPlugin.TD_COST_COMP_CLASS_DEFAULT, name = "Average time to split a class that has a too high complexity (in hours)", description = ""),
    @EditableProperty(key = TechnicalDebtPlugin.TD_COST_COMP_METHOD, defaultValue = TechnicalDebtPlugin.TD_COST_COMP_METHOD_DEFAULT, name = "Average time to split a method that has a too high complexity (in hours)", description = ""),
    @EditableProperty(key = TechnicalDebtPlugin.TD_COST_DUPLI_BLOCK, defaultValue = TechnicalDebtPlugin.TD_COST_DUPLI_BLOCK_DEFAULT, name = "Average time to fix one block duplication block (in hours)", description = ""),
    @EditableProperty(key = TechnicalDebtPlugin.TD_COST_VIOLATION, defaultValue = TechnicalDebtPlugin.TD_COST_VIOLATION_DEFAULT, name = "Average time to fix a coding violation (in hours)", description = ""),
    @EditableProperty(key = TechnicalDebtPlugin.TD_COST_UNCOVERED_COMPLEXITY, defaultValue = TechnicalDebtPlugin.TD_COST_UNCOVERED_COMPLEXITY_DEFAULT, name = "Average time to cover complexity of one (in hours)", description = ""),
    @EditableProperty(key = TechnicalDebtPlugin.TD_COST_UNDOCUMENTED_API, defaultValue = TechnicalDebtPlugin.TD_COST_UNDOCUMENTED_API_DEFAULT, name = "Average time to document 1 API (in hours)", description = "")
})

public class TechnicalDebtPlugin implements Plugin {
    public final static String TD_DAILY_RATE = "techdebt.daily.rate";
    public final static String TD_DAILY_RATE_DEFAULT = "500";

    public final static String TD_COST_COMP_CLASS = "techdebt.split.class";
    public final static String TD_COST_COMP_CLASS_DEFAULT = "4";

    public final static String TD_COST_COMP_METHOD = "techdebt.split.meth";
    public final static String TD_COST_COMP_METHOD_DEFAULT = "0.5";

    public final static String TD_COST_DUPLI_BLOCK = "techdebt.dupli.blocks";
    public final static String TD_COST_DUPLI_BLOCK_DEFAULT = "2";

    public final static String TD_COST_VIOLATION = "techdebt.violation";
    public final static String TD_COST_VIOLATION_DEFAULT = "0.08";

    public final static String TD_COST_UNCOVERED_COMPLEXITY = "techdebt.uncovered.complexity";
    public final static String TD_COST_UNCOVERED_COMPLEXITY_DEFAULT = "0.3";

    public final static String TD_COST_UNDOCUMENTED_API = "techdebt.undocumented.api";
    public final static String TD_COST_UNDOCUMENTED_API_DEFAULT = "0.08";

    public String getDescription() {
        return "Calculate a technical debt and display it on the project dashboard.";
    }

    public List<Class<? extends Extension>> getExtensions() {
        List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
        list.add(TechnicalDebtMetrics.class);
        list.add(TechnicalDebtJob.class);
        list.add(TechnicalDebtWidget.class);
        return list;
    }

    public String getKey() {
        return "technical-debt";
    }

    public String getName() {
        return "Technical Debt";
    }
}
