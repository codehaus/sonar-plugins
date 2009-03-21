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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.codehaus.javancss.entities.Resource;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;

public abstract class AbstractSensor {

	private Stack<Resource> resourcesStack;
	private FileContents fileContents;

	public final void setFileContents(FileContents fileContents) {
		this.fileContents = fileContents;
	}
	
	public final FileContents getFileContents(){
		return fileContents;
	}

	public List<Integer> getWantedTokens() {
		return new ArrayList<Integer>();
	}

	public final void setResourcesStack(Stack<Resource> resourcesStack) {
		this.resourcesStack = resourcesStack;
	}

	public final void addResource(Resource child) {
		peekResource().addChild(child);
		resourcesStack.add(child);
	}

	public final void popResource() {
		resourcesStack.pop();
	}

	public final Resource peekResource() {
		return resourcesStack.peek();
	}

	public void visitFile(DetailAST ast) {
	}

	public void visitToken(DetailAST ast) {
	}

	public void leaveToken(DetailAST ast) {
	}

	public void leaveFile(DetailAST ast) {
	}
}
