/*
 * Sonar Sonargraph Plugin
 * Copyright (C) 2009, 2010, 2011 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.hello2morrow.sonarplugin.api;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.SonarPlugin;


/**
 * @author Ingmar
 *
 */
public class SonargraphPluginTest {

  /**
   * Test method for {@link com.hello2morrow.sonarplugin.api.SonargraphPlugin#getExtensions()}.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testGetExtensions() {
    SonarPlugin plugin = new SonargraphPlugin();
    Assert.assertNotNull(plugin.getExtensions());
    Assert.assertTrue(plugin.getExtensions().size() > 0);
  }

}
