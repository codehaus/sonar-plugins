/*
 * Sonar Standalone Runner
 * Copyright (C) 2011 SonarSource
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
package org.sonar.runner.bootstrapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.runner.bootstrapper.BootstrapClassLoader;

import static org.fest.assertions.Assertions.assertThat;

public class BootstrapClassLoaderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldRestrictLoadingFromParent() throws Exception {
    BootstrapClassLoader classLoader = new BootstrapClassLoader(getClass().getClassLoader(), "org.sonar.ant");
    assertThat(classLoader.canLoadFromParent("org.sonar.ant.Launcher")).isTrue();
    assertThat(classLoader.canLoadFromParent("org.objectweb.asm.ClassVisitor")).isFalse();
  }

  @Test
  public void use_isolated_system_classloader_when_parent_is_excluded() throws ClassNotFoundException {
    thrown.expect(ClassNotFoundException.class);
    thrown.expectMessage("org.junit.Test");
    ClassLoader parent = getClass().getClassLoader();
    BootstrapClassLoader classLoader = new BootstrapClassLoader(parent);

    // JUnit is available in the parent classloader (classpath used to execute this test) but not in the core JVM
    assertThat(classLoader.loadClass("java.lang.String", false)).isNotNull();
    classLoader.loadClass("org.junit.Test", false);
  }

  @Test
  public void find_in_parent_when_matches_unmasked_packages() throws ClassNotFoundException {
    ClassLoader parent = getClass().getClassLoader();
    BootstrapClassLoader classLoader = new BootstrapClassLoader(parent, "org.junit");

    // JUnit is available in the parent classloader (classpath used to execute this test) but not in the core JVM
    assertThat(classLoader.loadClass("org.junit.Test", false)).isNotNull();
  }
}
