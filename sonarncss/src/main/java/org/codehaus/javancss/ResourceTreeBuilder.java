package org.codehaus.javancss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.codehaus.javancss.Resource.Type;

public class ResourceTreeBuilder {

	private final Resource root;
	private final Stack<Resource> resourcesStack = new Stack<Resource>();

	public ResourceTreeBuilder(Resource root) {
		this.root = root;
		resourcesStack.add(root);
	}

	public Resource getRoot() {
		return root;
	}

	public void addChild(Resource child) {
		peek().addChild(child);
		resourcesStack.add(child);
	}

	public void pop() {
		resourcesStack.pop();
	}

	public Resource peek() {
		return resourcesStack.peek();
	}

	public void processTree() {
		Set<Resource> roots = new HashSet<Resource>();
		roots.add(root);
		processTreeChild(roots);
	}

	private void processTreeChild(Set<Resource> children) {
		for (Resource child : children) {
			if (child.getChildren() != null) {
				processTreeChild(child.getChildren());
				aggregateChildrenMetrics(child);
			}
		}
	}

	private void aggregateChildrenMetrics(Resource parent) {
		for (Resource child : parent.getChildren()) {
			parent.loc += child.loc;
			parent.ncloc += child.ncloc;
			parent.cc += child.cc;
			parent.ncss += child.ncss;
			parent.javadocBlocks += child.javadocBlocks;
			parent.commentLines += child.commentLines;
			parent.javadocLines += child.javadocLines;
			parent.methods += child.methods;
			parent.classes += child.classes;
			parent.files += child.files;
			parent.packages += child.packages;
			parent.blankLines += child.blankLines;
		}

	}

	public static List<Resource> findSubChildren(Resource parent, Type childType) {
		List<Resource> classChildren = new ArrayList<Resource>();
		for (Resource res : parent.getChildren()) {
			if (res.getType() == childType) {
				classChildren.add(res);
			}
			classChildren.addAll(findSubChildren(res, childType));
		}
		return classChildren;
	}

}
