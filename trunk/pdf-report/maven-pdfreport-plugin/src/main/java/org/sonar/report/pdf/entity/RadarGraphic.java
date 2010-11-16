/*
 * Sonar PDF Plugin, open source plugin for Sonar
 *
 * Copyright (C) 2009 GMV-SGI
 * Copyright (C) 2010 klicap - ingenieria del puzle
 *
 * Sonar PDF Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.report.pdf.entity;

import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

import org.sonar.report.pdf.util.Logger;

public class RadarGraphic {

  private String efficiency;
  private String maintainability;
  private String portability;
  private String reliavility;
  private String usability;
  private String sonarUrl;

  public RadarGraphic(String efficiency, String maintainability, String portability, String reliavility,
      String usability, String sonarUrl) {
    this.efficiency = efficiency;
    this.maintainability = maintainability;
    this.portability = portability;
    this.reliavility = reliavility;
    this.usability = usability;
    this.sonarUrl = sonarUrl;
  }

  public Image getGraphic() {
    Image image = null;
    try {
      String requestUrl = sonarUrl + "/chart?ck=xradar&w=210&h=110&c=777777|F8A036&m=100&g=0.25&" + "l=Eff.("
          + efficiency + "%25),Mai.(" + maintainability + "%25),Por.(" + portability + "%25),Rel.(" + reliavility
          + "%25),Usa.(" + usability + "%25)&" + "v=" + efficiency + "," + maintainability + "," + portability + ","
          + reliavility + "," + usability;
      Logger.debug("Getting radar graphic: " + requestUrl);
      image = Image.getInstance(requestUrl);
      image.setAlignment(Image.ALIGN_MIDDLE);
    } catch (BadElementException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return image;
  }

}
