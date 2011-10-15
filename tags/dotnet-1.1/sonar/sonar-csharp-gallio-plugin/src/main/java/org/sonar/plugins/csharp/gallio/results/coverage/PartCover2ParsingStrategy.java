/*
 * Sonar C# Plugin :: Gallio
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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
package org.sonar.plugins.csharp.gallio.results.coverage;

import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findAttributeValue;

import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PartCover2ParsingStrategy extends PartCoverParsingStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(PartCover2ParsingStrategy.class);

  protected PartCover2ParsingStrategy() {
    super();
  }

  @Override
  public String findAssemblyName(SMInputCursor typeTag) {
    return findAttributeValue(typeTag, "asm");
  }

  @Override
  public void saveAssemblyNamesById(SMInputCursor docsTag) {
    LOG.debug("Creating a map with assembly names would be useless with PartCover 2.x version");
  }

  @Override
  public void saveId(String id) {
    LOG.debug("Unused method for PartCover 2.x");
  }

  @Override
  public String getAssemblyReference() {
    LOG.debug("Unused method for PartCover 2.x, return \"\" to avoid null pointer");
    return "";
  }

  @Override
  public void setAssemblyReference(String asmRef) {
    LOG.debug("Unused method for PartCover 2.x");
  }

}
