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

import org.sonar.api.resources.Project;


public final class ProjectDelegate implements IProject
{
    private final Project project;

    public ProjectDelegate(Project project)
    {
        this.project = project;
    }

    public String getName()
    {
        return project.getName();
    }

    public String getArtifactId()
    {
        return project.getArtifactId();
    }

    public String getGroupId()
    {
        return project.getGroupId();
    }
}
