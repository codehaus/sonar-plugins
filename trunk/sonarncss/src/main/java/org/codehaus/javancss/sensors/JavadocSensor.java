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
package org.codehaus.javancss.sensors;

import java.util.Arrays;
import java.util.List;

import org.codehaus.javancss.entities.Resource;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class JavadocSensor extends ASTSensor {

	@Override
	public List<Integer> getWantedTokens() {
		return Arrays
				.asList(TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
	}

	@Override
	public void visitToken(DetailAST ast) {
		Resource currentResource = peekResource();
		TextBlock javadoc = getFileContents().getJavadocBefore(ast.getLineNo());
		if (javadoc != null) {
			currentResource.measures.setJavadocLines(countRealCommentLines(javadoc));
			currentResource.measures.setJavadocBlocks(1);
			currentResource.measures.setJavadoc(true);
		} else {
			peekResource();
		}
	}

	private long countRealCommentLines(TextBlock javadoc) {
		int cCommentsLines = 0;
		for (int i = 0; i < javadoc.getText().length; i++) {
			String commentLine = javadoc.getText()[i];
			commentLine = commentLine.replace('*', ' ').replace('/', ' ').trim();
			if (commentLine.length() != 0) {
				cCommentsLines++;
			}

		}

		return cCommentsLines;
	}
}
