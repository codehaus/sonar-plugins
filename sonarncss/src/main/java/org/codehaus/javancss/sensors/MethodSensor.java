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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehaus.javancss.entities.Resource;
import org.codehaus.javancss.entities.Resource.Type;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MethodSensor extends ASTSensor {

	@Override
	public List<Integer> getWantedTokens() {
		return Arrays.asList(TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF);
	}

	@Override
	public void visitToken(DetailAST ast) {
		String methodName = extractMethodName(ast);
		Resource methodRes = new Resource(methodName, Type.METHOD);
		addResource(methodRes);
	}

	public void leaveToken(DetailAST ast) {
		popResource();
	}

	private String extractMethodName(DetailAST ast) {
		StringBuilder methodName = new StringBuilder(ast.findFirstToken(TokenTypes.IDENT).getText());
		methodName.append("(");
		List<String> parameters = extractMethodParameters(ast);
		for (String param : parameters) {
			methodName.append(param).append(" ");
		}
		methodName.append(")");
		return methodName.toString();
	}

	private List<String> extractMethodParameters(DetailAST ast) {
		DetailAST paramAst = ast.findFirstToken(TokenTypes.PARAMETERS);
		if (paramAst.getChildCount() == 0) {
			return Collections.emptyList();
		}
		List<String> parameters = new ArrayList<String>();
		DetailAST paramDefAst = paramAst.findFirstToken(TokenTypes.PARAMETER_DEF);
		parameters.add(paramDefAst.findFirstToken(TokenTypes.TYPE).getLastChild().getText());
		while ((paramDefAst = (DetailAST) paramDefAst.getNextSibling()) != null) {
			if (paramDefAst.getType() == TokenTypes.PARAMETER_DEF) {
				parameters.add(paramDefAst.findFirstToken(TokenTypes.TYPE).getLastChild().getText());
			}
		}
		return parameters;
	}
}
