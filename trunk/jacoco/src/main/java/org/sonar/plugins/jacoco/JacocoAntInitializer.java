/*
 * Sonar JaCoCo Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco;

import java.util.Hashtable;

import org.apache.tools.ant.*;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Initializer;
import org.sonar.api.batch.SupportedEnvironment;
import org.sonar.api.resources.Project;

@SupportedEnvironment("ant")
public class JacocoAntInitializer extends Initializer implements CoverageExtension {

  private org.apache.tools.ant.Project antProject;
  private JacocoConfiguration configuration;

  public JacocoAntInitializer(org.apache.tools.ant.Project antProject, JacocoConfiguration configuration) {
    this.antProject = antProject;
    this.configuration = configuration;
  }

  @Override
  public boolean shouldExecuteOnProject(org.sonar.api.resources.Project project) {
    return project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC);
    // TODO return project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC) && project.getFileSystem().hasTestFiles(Java.INSTANCE);
  }

  @Override
  public void execute(org.sonar.api.resources.Project project) {
    Hashtable<String, Target> hastable = antProject.getTargets();

    Target target = hastable.get("test");
    JavaLikeTaskEnhancer enhancer = new JavaLikeTaskEnhancer("junit");
    for (Task task : target.getTasks()) {
      if (enhancer.supportsTask(task.getTaskName())) {
        enhancer.enhanceTask(task);
      }
    }

    target.execute();
  }

  /**
   * Basic task enhancer that can handle all 'java like' tasks. That is, tasks
   * that have a top level fork attribute and nested jvmargs elements
   * 
   * @TODO copied from JaCoCo
   */
  private class JavaLikeTaskEnhancer implements TaskEnhancer {

    private final String supportedTaskName;

    public JavaLikeTaskEnhancer(final String supportedTaskName) {
      this.supportedTaskName = supportedTaskName;
    }

    public boolean supportsTask(final String taskname) {
      return taskname.equals(supportedTaskName);
    }

    public void enhanceTask(final Task task) {
      final RuntimeConfigurable configurableWrapper = task.getRuntimeConfigurableWrapper();

      final String forkValue = (String) configurableWrapper.getAttributeMap().get("fork");

      if (forkValue == null || !org.apache.tools.ant.Project.toBoolean(forkValue)) {
        throw new BuildException("Coverage can only be applied on a forked VM");
      }

      addJvmArgs((UnknownElement) task);
    }

    public void addJvmArgs(final UnknownElement task) {
      final UnknownElement el = new UnknownElement("jvmarg");
      el.setTaskName("jvmarg");
      el.setQName("jvmarg");

      final RuntimeConfigurable runtimeConfigurableWrapper = el.getRuntimeConfigurableWrapper();
      runtimeConfigurableWrapper.setAttribute("value", configuration.getJvmArgument());

      task.getRuntimeConfigurableWrapper().addChild(runtimeConfigurableWrapper);

      task.addChild(el);
    }
  }

  /**
   * @TODO copied from JaCoCo
   */
  private interface TaskEnhancer {
    /**
     * @param taskname
     *          Task type to enhance
     * @return <code>true</code> iff this enhancer is capable of enhacing
     *         the requested task type
     */
    public boolean supportsTask(String taskname);

    /**
     * Attempt to enhance the supplied task with coverage information. This
     * operation may fail if the task is being executed in the current VM
     * 
     * @param task
     *          Task instance to enhance (usually an {@link UnknownElement})
     * @throws BuildException
     *           Thrown if this enhancer can handle this type of task, but
     *           this instance can not be enhanced for some reason.
     */
    public void enhanceTask(Task task) throws BuildException;
  }

}
