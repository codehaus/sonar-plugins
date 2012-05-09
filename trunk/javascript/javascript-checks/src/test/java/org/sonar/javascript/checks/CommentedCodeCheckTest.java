/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 Eriks Nukis
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
package org.sonar.javascript.checks;

import com.sonar.sslr.squid.checks.CheckMessagesVerifier;
import org.junit.Test;
import org.sonar.javascript.JavaScriptAstScanner;
import org.sonar.squid.api.SourceFile;

import java.io.File;

import static org.hamcrest.Matchers.containsString;

public class CommentedCodeCheckTest {

  @Test
  public void test() {
    SourceFile file = JavaScriptAstScanner.scanSingleFile(new File("src/test/resources/checks/commentedCode.js"), new CommentedCodeCheck());
    CheckMessagesVerifier.verify(file.getCheckMessages())
        .next().atLine(4).withMessageThat(containsString("Sections of code should not be \"commented out\"."))
        .next().atLine(11)
        .noMore();
  }

}
