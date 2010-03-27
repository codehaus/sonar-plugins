/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. ¬†See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA ¬†02
 */

package org.sonar.replay;

import com.perforce.p4java.core.file.P4JFileSpec;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInterval;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO: Add documentation for ReplayConfig
 *
 * @author <a href="mailto:jaxzin@codehaus.org">Brian R. Jackson</a>
 * @version $Change$
 */
@Data
public class ReplayConfig {

    public static final DateTime DEFAULT_INTERVAL_START = new DateTime(Long.MIN_VALUE);

    public static final DateTime DEFAULT_INTERVAL_END = new DateTime(Long.MAX_VALUE);

    /**
     * Optional sonar connection information since this can also be defined in the
     * Maven settings.xml.
     */
    @Nullable
    private SonarConfig sonarConfig;

    /**
     * Perforce connection information.
     */
    @Nonnull
    private PerforceConfig perforceConfig;

    /**
     * The location of the project in Perforce.  This is a list of file specs as some project locations
     * are more complex than just one path.
     */
    @Nonnull
    private List<P4JFileSpec> projectFileSpecs;

    /**
     * An optional regular expression to match label names against.
     * The default value is '.*' so all labels will be included.
     */
    @Nonnull
    private Pattern labelFilter = Pattern.compile(".*");


    /**
     * An optional window of time to limit the replay to.  By default it is unlimited;
     */
    @Nonnull
    private ReadableInterval interval = new Interval(DEFAULT_INTERVAL_START, DEFAULT_INTERVAL_END);

    /**
     * A regular expression that extracts the version from the label as group 1.
     * The default value is '.*-(.*)' which will take everything after the first '-'.
     * Example: 'example-1.0' will match the pattern and group 1 will contain '1.0'
     */
    @Nonnull
    private Pattern labelVersionFilter = Pattern.compile(".*-(.+)");
}
