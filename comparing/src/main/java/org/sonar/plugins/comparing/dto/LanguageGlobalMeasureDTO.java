/*
 * Sonar Comparing Plugin
 * Copyright (C) 2012 David FRANCOIS
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
package org.sonar.plugins.comparing.dto;

public class LanguageGlobalMeasureDTO {

  private String languageName;

  private Long nbLOCs;

  private Integer nbProjects;

  public LanguageGlobalMeasureDTO(String languageName, Long nbLOCs, Integer nbProjects) {
    this.languageName = languageName;
    this.nbLOCs = nbLOCs;
    this.nbProjects = nbProjects;
  }

  /**
   * @return the languageName
   */
  public String getLanguageName() {
    return languageName;
  }

  /**
   * @param languageName the languageName to set
   */
  public void setLanguageName(String languageName) {
    this.languageName = languageName;
  }

  /**
   * @return the nbLOCs
   */
  public Long getNbLOCs() {
    return nbLOCs;
  }

  /**
   * @param nbLOCs the nbLOCs to set
   */
  public void setNbLOCs(Long nbLOCs) {
    this.nbLOCs = nbLOCs;
  }

  /**
   * @return the nbProjects
   */
  public Integer getNbProjects() {
    return nbProjects;
  }

  /**
   * @param nbProjects the nbProjects to set
   */
  public void setNbProjects(Integer nbProjects) {
    this.nbProjects = nbProjects;
  }

}
