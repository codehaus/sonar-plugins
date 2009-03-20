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
package org.codehaus.javancss.checkstyle;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.codehaus.javancss.metrics.ASTVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class CheckstyleJavaNcssBridge extends Check {

	private static Logger logger = LoggerFactory.getLogger(CheckstyleJavaNcssBridge.class);

	private static List<ASTVisitor> javaNcssVisitors;

	private static int[] allTokens;

	public static void setJavaNcssASTVisitors(List<ASTVisitor> javaNcssVistors) {
		CheckstyleJavaNcssBridge.javaNcssVisitors = javaNcssVistors;
		SortedSet<Integer> sorter = new TreeSet<Integer>();
		for (ASTVisitor visitor : javaNcssVistors) {
			sorter.addAll(visitor.getWantedTokens());
			allTokens = new int[sorter.size()];
			int i = 0;

			for (Integer itSorted : sorter) {
				allTokens[i++] = itSorted;
			}
		}
	}

	@Override
	public int[] getDefaultTokens() {
		return allTokens;
	}

	public void beginTree(DetailAST ast) {
		try {
			for (ASTVisitor visitor : javaNcssVisitors) {
				visitor.setFileContents(getFileContents());
				visitor.visitFile(ast);
			}
		} catch (RuntimeException e) {
			// Exception are not propagated by Checkstyle engine
			logger.error("Error occurs when analysing :" + getFileContents().getFilename(), e);
		}
	}

	public void visitToken(DetailAST ast) {
		try {
			for (ASTVisitor visitor : javaNcssVisitors) {
				if (visitor.getWantedTokens().contains(ast.getType())) {
					visitor.visitToken(ast);
				}
			}
		} catch (RuntimeException e) {
			// Exception are not propagated by Checkstyle engine
			logger.error("Error occurs when analysing :" + getFileContents().getFilename(), e);
		}
	}

	public void leaveToken(DetailAST ast) {
		try {
			for (int i = javaNcssVisitors.size() - 1; i >= 0; i--) {
				ASTVisitor visitor = javaNcssVisitors.get(i);
				if (visitor.getWantedTokens().contains(ast.getType())) {
					visitor.leaveToken(ast);
				}
			}
		} catch (RuntimeException e) {
			// Exception are not propagated by Checkstyle engine
			logger.error("Error occurs when analysing :" + getFileContents().getFilename(), e);
		}
	}

	public void finishTree(DetailAST ast) {
		try {
			for (int i = javaNcssVisitors.size() - 1; i >= 0; i--) {
				ASTVisitor visitor = javaNcssVisitors.get(i);
				visitor.leaveFile(ast);
			}
		} catch (RuntimeException e) {
			// Exception are not propagated by Checkstyle engine
			logger.error("Error occurs when analysing :" + getFileContents().getFilename(), e);
		}
	}
}
