/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
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
package org.sonar.plugins.jlint;

import java.util.HashMap;
import java.util.Map;


public final class Category {
  private final static Map<String, String> jlintToSonar = new HashMap<String, String>();

  static {
    jlintToSonar.put("BOUNDS" , "BOUNDS");
    jlintToSonar.put("DEADLOCK" , "DEADLOCK");
    jlintToSonar.put("DOMAIN" , "DOMAIN");
    jlintToSonar.put("FIELD_REDEFINED" , "FIELD_REDEFINED");
    jlintToSonar.put("INCOMP_CASE" , "INCOMP_CASE");
    jlintToSonar.put("NOT_OVERRIDDEN" , "NOT_OVERRIDDEN");
    jlintToSonar.put("NULL_REFERENCE" , "NULL_REFERENCE");
    jlintToSonar.put("OVERFLOW" , "OVERFLOW");
    jlintToSonar.put("RACE_CONDITION" , "RACE_CONDITION");
    jlintToSonar.put("REDUNDANT" , "REDUNDANT");
    jlintToSonar.put("SHADOW_LOCAL" , "SHADOW_LOCAL");
    jlintToSonar.put("SHORT_CHAR_CMP" , "SHORT_CHAR_CMP");
    jlintToSonar.put("STRING_CMP" , "STRING_CMP");
    jlintToSonar.put("SUPER_FINALIZE" , "SUPER_FINALIZE");
    jlintToSonar.put("TRUNCATION" , "TRUNCATION");
    jlintToSonar.put("WAIT_NOSYNC" , "WAIT_NOSYNC");
    jlintToSonar.put("WEAK_CMP" , "WEAK_CMP");
    jlintToSonar.put("ZERO_OPERAND" , "ZERO_OPERAND");
    jlintToSonar.put("ZERO_RESULT" , "ZERO_RESULT");
  
	/*
	 * TODO: REMOVE this code
	jlintToSonar.put("BAD_PRACTICE", "Bad practice");
    jlintToSonar.put("CORRECTNESS", "Correctness");
    jlintToSonar.put("MT_CORRECTNESS", "Multithreaded correctness");
    jlintToSonar.put("I18N", "Internationalization");
    jlintToSonar.put("EXPERIMENTAL", "Experimental");
    jlintToSonar.put("MALICIOUS_CODE", "Malicious code");
    jlintToSonar.put("PERFORMANCE", "Performance");
    jlintToSonar.put("SECURITY", "Security");
    jlintToSonar.put("STYLE", "Style");
	*/
  }


  public static String jlintToSonar(String jlintCategKey) {
    return jlintToSonar.get(jlintCategKey);
  }
}