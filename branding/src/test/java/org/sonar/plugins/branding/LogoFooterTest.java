/*
 * Sonar Branding Plugin
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

package org.sonar.plugins.branding;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

public class LogoFooterTest {

  private Configuration conf;
  private LogoFooter footer;

  @Before
  public void setUp() {
    conf = new BaseConfiguration();
    footer = new LogoFooter(conf);
  }

  @Test
  public void shouldNotCreateFooterIfNoImage() {
    assertThat(footer.getHtml(), is(""));
  }

  @Test
  public void shouldCreateFooterDefaultLocation() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    String html = footer.getHtml();
    assertThat(html, containsString("div[id=\"error\"]"));
    assertThat(html, containsString("http://example.org/logo.png"));
  }

  @Test
  public void shouldCreateFooterTopLocation() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    conf.setProperty(BrandingPlugin.LOGO_LOCATION_PROPERTY, "TOP");
    assertThat(footer.getHtml(), containsString("div[id=\"error\"]"));
  }

  @Test
  public void shouldCreateFooterMenuLocation() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    conf.setProperty(BrandingPlugin.LOGO_LOCATION_PROPERTY, "MENU");
    assertThat(footer.getHtml(), containsString("img[title=\"Embrace Quality\"]"));
  }

  @Test
  public void shouldCreateFooterInvalidLocation() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://example.org/logo.png");
    conf.setProperty(BrandingPlugin.LOGO_LOCATION_PROPERTY, "foo");
    assertThat(footer.getHtml(), containsString("div[id=\"error\"]"));
  }

  @Test
  public void shouldCreateCompanyLogoWithLink() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    conf.setProperty(BrandingPlugin.LINK_PROPERTY, "http://example.org/");
    String html = footer.getHtml();
    assertThat(html, containsString("companyUrl"));
    assertThat(html, containsString("http://example.org/"));
  }

  @Test
  public void shouldSetImageHeight() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    conf.setProperty(BrandingPlugin.IMAGE_HEIGHT, 20);
    String html = footer.getHtml();
    assertThat(html, containsString("height"));
    assertThat(html, containsString("20"));
  }
  
  @Test
  public void shouldSetImageWieght() {
    conf.setProperty(BrandingPlugin.IMAGE_PROPERTY, "http://images.example.org/logo.png");
    conf.setProperty(BrandingPlugin.IMAGE_WIDTH, 80);
    String html = footer.getHtml();
    assertThat(html, containsString("width"));
    assertThat(html, containsString("80"));
  }

}
