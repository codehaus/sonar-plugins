/*
 * Sonar C# Plugin :: FxCop
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

package com.sonar.csharp.fxcop.profiles.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * SaveMessages option for FxCop
 */
@XmlType(name = "SaveMessages")
public class SaveMessages {

  @XmlElement(name = "Project")
  private Entity project;
  @XmlElement(name = "Report")
  private Entity report;

  /**
   * Returns the project.
   * 
   * @return The project to return.
   */
  public Entity getProject() {
    return this.project;
  }

  /**
   * Sets the project.
   * 
   * @param project
   *          The project to set.
   */
  public void setProject(Entity project) {
    this.project = project;
  }

  /**
   * Returns the report.
   * 
   * @return The report to return.
   */
  public Entity getReport() {
    return this.report;
  }

  /**
   * Sets the report.
   * 
   * @param report
   *          The report to set.
   */
  public void setReport(Entity report) {
    this.report = report;
  }

  /**
   * Constructs the option;
   */
  public SaveMessages() {
    project = new Entity();
    report = new Entity();
    project.setStatus("Active, Excluded");
  }
}
