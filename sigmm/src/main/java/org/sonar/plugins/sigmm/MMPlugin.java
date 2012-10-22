/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.sigmm;

import org.sonar.api.Extension;
import org.sonar.api.SonarPlugin;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class MMPlugin extends SonarPlugin {

  /**
   * {@inheritDoc}
   */
  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(MMMetrics.class);
    list.add(MMDecorator.class);
    list.add(MMDistributionDecorator.class);
    list.add(MMWidget.class);

    return list;
  }

  protected static boolean shouldPersistMeasures(Resource resource) {
    if (!ResourceUtils.isFile(resource) && !ResourceUtils.isClass(resource)) {
      return true;
    }
    return false;
  }
}
