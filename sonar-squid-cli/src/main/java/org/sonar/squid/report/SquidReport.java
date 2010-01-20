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
package org.sonar.squid.report;

import org.sonar.squid.report.entries.AbstractReportEntry;
import org.sonar.squid.report.entries.PackageReportEntry;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;


/**
 * 
 * <p>This classes contains all the data gathered by Squid
 * and worth of being reported.</p>
 * 
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
@XStreamAlias("project")
public class SquidReport extends AbstractReportEntry {
    
    @XStreamImplicit
    private List<PackageReportEntry> packages;

    public SquidReport(String name) {
        super(name);
    }

    public List<PackageReportEntry> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageReportEntry> packages) {
        this.packages = packages;
    }

}
