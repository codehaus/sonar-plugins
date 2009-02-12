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
package org.codehaus.javancss.metrics;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.javancss.Resource;
import org.codehaus.javancss.ResourceTreeBuilder;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;

public abstract class ASTVisitor {

	ResourceTreeBuilder resourceTree;
	FileContents fileContents;

	public final void setFileContents(FileContents fileContents) {
		this.fileContents = fileContents;
	}

	public List<Integer> getWantedTokens() {
		return new ArrayList<Integer>();
	}

	public final void setResourcesStack(ResourceTreeBuilder resourceTree) {
		this.resourceTree = resourceTree;
	}
	
	public Resource getCurrentResource(){
		return resourceTree.peek();
	}

	public void beginTree(DetailAST ast) {
	}

	public void visitToken(DetailAST ast) {
	}

	public void leaveToken(DetailAST ast) {
	}

	public void finishTree(DetailAST ast) {
	}
}
