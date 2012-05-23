/*
 * Sonar JIRA Reviews Plugin
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.reviews.jira;

import com.google.common.collect.Lists;
import org.sonar.api.Extension;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.reviews.jira.soap.JiraSOAPClient;

import java.util.List;

public final class JiraLinkReviewsPlugin extends SonarPlugin {

  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<Class<? extends Extension>> getExtensions() {
    List extensions = Lists.newLinkedList();
    extensions.add(JiraSOAPClient.class);
    extensions.add(LinkFunction.class);
    extensions.add(WorkflowBuilder.class);
    return extensions;
  }
}
