/*
 * Sonar Basic-COCOMO Plugin
 * Copyright (C) 2010 Xup BV.
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

package org.sonar.plugins.cocomo;

import org.sonar.api.Plugin;
import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.ArrayList;
import java.util.List;

@Properties({
    @Property(key = BasicCOCOMOPlugin.CCM_COEFFICIENT_AB, defaultValue = BasicCOCOMOPlugin.CCM_COEFFICIENT_AB_DEFAULT, name = "Basic COCOMO Ab coefficient", description = ""),
    @Property(key = BasicCOCOMOPlugin.CCM_COEFFICIENT_BB, defaultValue = BasicCOCOMOPlugin.CCM_COEFFICIENT_BB_DEFAULT, name = "Basic COCOMO Bb coefficient", description = ""),
    @Property(key = BasicCOCOMOPlugin.CCM_COEFFICIENT_CB, defaultValue = BasicCOCOMOPlugin.CCM_COEFFICIENT_CB_DEFAULT, name = "Basic COCOMO Cb coefficient", description = ""),
    @Property(key = BasicCOCOMOPlugin.CCM_COEFFICIENT_DB, defaultValue = BasicCOCOMOPlugin.CCM_COEFFICIENT_DB_DEFAULT, name = "Basic COCOMO Db coefficient", description = ""),
    @Property(key = BasicCOCOMOPlugin.CCM_MONTHLY_RATE, defaultValue = BasicCOCOMOPlugin.CCM_MONTHLY_RATE_DEFAULT, name = "Monthly rate for a developer", description = "What's average monthly rate for a developer."),
    @Property(key = BasicCOCOMOPlugin.CCM_CURRENCY_SYMBOL, defaultValue = BasicCOCOMOPlugin.CCM_CURRENCY_SYMBOL_DEFAULT, name = "Currency symbol", description = "Currency symbol to be displayed for estimated costs.")
})

/** {@inheritDoc} */
public class BasicCOCOMOPlugin implements Plugin {
  public static final String CCM_COEFFICIENT_AB = "cocomo.coefficient.Ab";
  public static final String CCM_COEFFICIENT_AB_DEFAULT = "2.4";

  public static final String CCM_COEFFICIENT_BB = "cocomo.coefficient.Bb";
  public static final String CCM_COEFFICIENT_BB_DEFAULT = "1.05";

  public static final String CCM_COEFFICIENT_CB = "cocomo.coefficient.Cb";
  public static final String CCM_COEFFICIENT_CB_DEFAULT = "2.5";

  public static final String CCM_COEFFICIENT_DB = "cocomo.coefficient.Db";
  public static final String CCM_COEFFICIENT_DB_DEFAULT = "0.38";

  public static final String CCM_MONTHLY_RATE = "cocomo.monthly.rate";
  public static final String CCM_MONTHLY_RATE_DEFAULT = "10000";

  public static final String CCM_CURRENCY_SYMBOL = "cocomo.currency.symbol";
  public static final String CCM_CURRENCY_SYMBOL_DEFAULT = "€";

  /** {@inheritDoc} */
  public final String getDescription() {
	StringBuffer desc = new StringBuffer();
	desc.append( "<h3>Basic <b>CO</b>nstructive <b>CO</b>st <b>MO</b>del (COCOMO)</h3>" );
	desc.append( "<p>Calculate estimated project cost using the basic COCOMO algorithm.</p>" );
	desc.append( "	<script>" );
	desc.append( "	function changeCOCOMO( ab, bb, cb, db ) {" );
	desc.append( "	  document.getElementById('cocomo.coefficient.Ab').value=ab;" );
	desc.append( "	  document.getElementById('cocomo.coefficient.Bb').value=bb;" );
	desc.append( "	  document.getElementById('cocomo.coefficient.Cb').value=cb;" );
	desc.append( "	  document.getElementById('cocomo.coefficient.Db').value=db;" );
	desc.append( "	}" );
	desc.append( "	</script>" );
	desc.append( "	<table style=\"margin-left: 30px;\">" );
	desc.append( "	  <thead>" );
	desc.append( "	    <tr style=\"border-bottom: 1px solid silver; font-weight: bold;\">" );
	desc.append( "	      <td width=\"120\">Software project</td>" );
	desc.append( "	      <td width=\"35\" align=\"center\"><i>a<sub>b</sub></i></td>" );
	desc.append( "	      <td width=\"35\" align=\"center\"><i>b<sub>b</sub></i></td>" );
	desc.append( "	      <td width=\"35\" align=\"center\"><i>c<sub>b</sub></i></td>" );
	desc.append( "	      <td width=\"35\" align=\"center\"><i>d<sub>b</sub></i></td>" );
	desc.append( "	    </tr>" );
	desc.append( "	  </thead>" );
	desc.append( "	  <tbody>" );
	desc.append( "	    <tr class=\"odd\" onclick=\"changeCOCOMO('2.4', '1.05', '2.5', '0.38' );\">" );
	desc.append( "	      <td>Organic</td>" );
	desc.append( "	      <td align=\"center\">2.4</td>" );
	desc.append( "	      <td align=\"center\">1.05</td>" );
	desc.append( "	      <td align=\"center\">2.5</td>" );
	desc.append( "	      <td align=\"center\">0.38</td>" );
	desc.append( "	    </tr>" );
	desc.append( "	    <tr class=\"even\" onclick=\"changeCOCOMO('3.0', '1.12', '2.5', '0.35' );\">" );
	desc.append( "	      <td>Semi-detached</td>" );
	desc.append( "	      <td align=\"center\">3.0</td>" );
	desc.append( "	      <td align=\"center\">1.12</td>" );
	desc.append( "	      <td align=\"center\">2.5</td>" );
	desc.append( "	      <td align=\"center\">0.35</td>" );
	desc.append( "	    </tr>" );
	desc.append( "	    <tr class=\"odd\" onclick=\"changeCOCOMO('3.6', '1.20', '2.5', '0.32' );\">" );
	desc.append( "	      <td>Embedded</td>" );
	desc.append( "	      <td align=\"center\">3.6</td>" );
	desc.append( "	      <td align=\"center\">1.20</td>" );
	desc.append( "	      <td align=\"center\">2.5</td>" );
	desc.append( "	      <td align=\"center\">0.32</td>" );
	desc.append( "	    </tr>" );
	desc.append( "	  </tbody>" );
	desc.append( "	</table>" );
	desc.append( "<p>Click on the table to select a row</p>" );
	desc.append( "<p>For more information on basic COCOMO visit wikipedia. <a href=\"http://en.wikipedia.org/wiki/Cocomo\" target=\"WikiPedia\"><img class=\"png\" src='/images/help.png'></a>" );
    return desc.toString();
  }

  /** {@inheritDoc} */
  public final List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(BasicCOCOMOMetrics.class);
    list.add(BasicCOCOMODecorator.class);
    list.add(BasicCOCOMOWidget.class);
    return list;
  }

  /** {@inheritDoc} */
  public final String getKey() {
    return "cocomo";
  }

  /** {@inheritDoc} */
  public final String getName() {
    return "Basic COCOMO";
  }
  
  public static final String getCurrencySymbolConfigKey() {
	  return CCM_CURRENCY_SYMBOL;
  }
}
