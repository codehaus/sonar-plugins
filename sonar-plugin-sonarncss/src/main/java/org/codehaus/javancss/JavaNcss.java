/*
Copyright (C) 2001 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS

JavaNCSS is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

JavaNCSS is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with JavaNCSS; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.  */
package org.codehaus.javancss;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.javancss.checkstyle.CheckstyleJavaNcssBridge;
import org.codehaus.javancss.checkstyle.CheckstyleLauncher;
import org.codehaus.javancss.metrics.ASTVisitor;
import org.codehaus.javancss.metrics.CcCounter;
import org.codehaus.javancss.metrics.ClassCounter;
import org.codehaus.javancss.metrics.CommentCounter;
import org.codehaus.javancss.metrics.JavaDocCounter;
import org.codehaus.javancss.metrics.MethodCounter;
import org.codehaus.javancss.metrics.NcssCounter;
import org.codehaus.javancss.metrics.PackageCounter;

public class JavaNcss {

  private final ResourceTreeBuilder resourceTree;
	private final List<File> filesToAnalyse;

	private final List<ASTVisitor> javaNcssVisitors = Arrays.asList(new PackageCounter(), new ClassCounter(),
			new MethodCounter(), new NcssCounter(), new CcCounter(), new CommentCounter(), new JavaDocCounter());

	public JavaNcss(File dirToAnalyse) {
		this(traverse(dirToAnalyse));
	}

	public JavaNcss(List<File> filesToAnalyse) {
		this.filesToAnalyse = filesToAnalyse;
		Resource project = new Resource("Project", Resource.Type.PROJECT);
		resourceTree = new ResourceTreeBuilder(project);
		for (ASTVisitor visitor : javaNcssVisitors) {
			visitor.setResourcesStack(resourceTree);
		}
		CheckstyleJavaNcssBridge.setJavaNcssASTVisitors(javaNcssVisitors);
	}

	public Resource analyseSources() {
		CheckstyleLauncher.launchCheckstyleEngine(filesToAnalyse);
		resourceTree.processTree();
		return resourceTree.getRoot();
	}

	/**
	 * Traverses a specified node looking for files to check. Found files are
	 * added to a specified list. Subdirectories are also traversed.
	 * 
	 * @param aNode
	 *            the node to process
	 * @param aFiles
	 *            list to add found files to
	 */
	private static List<File> traverse(File aNode) {
		List<File> files = new ArrayList<File>();
		if (aNode.canRead()) {
			if (aNode.isDirectory()) {
				final File[] nodes = aNode.listFiles();
				for (File node : nodes) {
					files.addAll(traverse(node));
				}
			} else if (aNode.isFile()) {
				files.add(aNode);
			}
		}
		return files;
	}

}
