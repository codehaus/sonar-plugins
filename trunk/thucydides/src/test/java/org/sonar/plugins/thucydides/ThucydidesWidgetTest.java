/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 OTS SA
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

package org.sonar.plugins.thucydides;

import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ThucydidesWidgetTest {

  private final ThucydidesWidget widget = new ThucydidesWidget();

  @Test
  public void testGetId() {
    assertThat(widget.getId(), notNullValue());
    assertThat(widget.getId(), equalTo("thucydides"));
  }

  @Test
  public void testGetTitle() {
    assertThat(widget.getTitle(), notNullValue());
    assertThat(widget.getTitle(), equalTo("Thucydides Web Testing Plugin"));
  }

  @Test
  public void testGetTemplatePath() {
    assertThat(widget.getTemplatePath(), notNullValue());
    assertThat(widget.getTemplatePath(), equalTo("/org/sonar/plugins/thucydides/thucydides_widget.html.erb"));
  }
}
