/*
 * Sonar C-Rules Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.c.checks;

import java.util.Set;
import java.util.regex.Pattern;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squid.recognizer.CodeRecognizer;
import org.sonar.squid.recognizer.ContainsDetector;
import org.sonar.squid.recognizer.Detector;
import org.sonar.squid.recognizer.EndWithDetector;
import org.sonar.squid.recognizer.KeywordsDetector;
import org.sonar.squid.recognizer.LanguageFootprint;

import com.google.common.collect.Sets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.CommentedCode", name = "Sections of code should not be \"commented out\".", priority = Priority.BLOCKER,
    description = "<p>Sections of code should not be \"commented out\".</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.BLOCKER)
public class CommentedCodeCheck extends CCheck {

  private static final double THRESHOLD = 0.9;

  private final CodeRecognizer codeRecognizer = new CodeRecognizer(THRESHOLD, new CRecognizer());
  private final Pattern regexpToDivideStringByLine = Pattern.compile("(\r?\n)|(\r)");

  private static class CRecognizer implements LanguageFootprint {

    public Set<Detector> getDetectors() {
      Set<Detector> detectors = Sets.newHashSet();

      detectors.add(new EndWithDetector(0.95, '}', ';', '{')); // NOSONAR Magic number is suitable in this case
      detectors.add(new KeywordsDetector(0.7, "||", "&&")); // NOSONAR
      detectors.add(new KeywordsDetector(0.3, "continue", "break", "return", "struct", "enum", "union", // NOSONAR
          "include", "static", "void", "long", "int", "float", "double", "true", "false", "case:", "default:"));
      detectors.add(new ContainsDetector(0.95, "++", "for(", "while(", "do{", "switch(", "if(", "elseif(", "else{"));// NOSONAR

      return detectors;
    }

  }

  @Override
  public void leaveFile(AstNode node) {
    for (Token comment : getComments()) {
      String lines[] = regexpToDivideStringByLine.split(extractComment(comment.getValue()));

      for (int lineOffset = 0; lineOffset < lines.length; lineOffset++) {
        if (codeRecognizer.isLineOfCode(lines[lineOffset])) {
          log("Sections of code should not be \"commented out\".", comment.getLine() + lineOffset);
          break;
        }
      }
    }
  }

  private static String extractComment(String string) {
    return "/*".equals(string.substring(0, 2)) ? string.substring(2, string.length() - 2) : string.substring(2);
  }
}
