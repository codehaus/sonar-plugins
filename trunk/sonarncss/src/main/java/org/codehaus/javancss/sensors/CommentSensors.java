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

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;

public class CommentSensors extends ASTSensor {

	public void visitFile(DetailAST ast) {
		long commentLines = getFileContents().getCppComments().size() + calculateCCommentsLines();
		peekResource().setCommentLines(commentLines);
	}

	private long calculateCCommentsLines() {
		int cCommentsLines = 0;
		for (Object objBlocks : getFileContents().getCComments().values()) {
			List commentBlocks = (List) objBlocks;
			for (Object objBlock : commentBlocks) {
				TextBlock commentBlock = (TextBlock) objBlock;
				if (commentBlock.getStartLineNo() == 1) {
					// skip file header
					continue;
				}
				for (int i = 0; i < commentBlock.getText().length; i++) {
					String commentLine = commentBlock.getText()[i];
					commentLine = commentLine.replace('*', ' ').replace('/', ' ').trim();
					if (commentLine.length() != 0) {
						cCommentsLines++;
					}

				}
			}
		}
		return cCommentsLines;
	}
}
