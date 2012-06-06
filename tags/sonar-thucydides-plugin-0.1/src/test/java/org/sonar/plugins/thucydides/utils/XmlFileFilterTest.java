/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
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
package org.sonar.plugins.thucydides.utils;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Papapetrou Patroklos
 */
public class XmlFileFilterTest {
  
  private final XmlFileFilter xmlFilefilter = new XmlFileFilter();
  
  @Test
  public void acceptShouldReturnTrue() {
    System.out.println("acceptShouldReturnTrue");
    File pathname = new File("/test/resources/sampleReport.xml");
    assertEquals(true, xmlFilefilter.accept(pathname));
  }

  @Test
  public void acceptShouldReturnFalse() {
    System.out.println("acceptShouldReturnFalse");
    File pathname = new File("/test/resources/sampleReport.txt");
    assertEquals(false, xmlFilefilter.accept(pathname));
  }
}
