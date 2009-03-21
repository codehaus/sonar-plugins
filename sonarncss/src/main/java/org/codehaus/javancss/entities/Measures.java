package org.codehaus.javancss.entities;

public class Measures {
	private long loc = 0;
	private long ncloc = 0;
	private long blankLines = 0;
	private long statements = 0;
	private long commentLines = 0;
	private long complexity = 0;
	private long branches = 0;
	private long methods = 0;
	private long classes = 0;
	private long files = 0;
	private long packages = 0;
	private long javadocLines = 0;
	private long javadocBlocks = 0;
	private boolean javadoc = false;

	public Measures() {
	}

	public long getLoc() {
		return loc;
	}

	public void setLoc(long loc) {
		this.loc = loc;
	}

	public long getNcloc() {
		return ncloc;
	}

	public void setNcloc(long ncloc) {
		this.ncloc = ncloc;
	}

	public long getBlankLines() {
		return blankLines;
	}

	public void setBlankLines(long blankLines) {
		this.blankLines = blankLines;
	}

	public long getStatements() {
		return statements;
	}

	public void addstatement() {
		statements++;
	}

	public long getCommentLines() {
		return commentLines;
	}

	public void setCommentLines(long commentLines) {
		this.commentLines = commentLines;
	}

	public long getComplexity() {
		return complexity;
	}

	public void setComplexity(long complexity) {
		this.complexity = complexity;
	}

	public long getBranches() {
		return branches;
	}

	public void addBranch() {
		branches++;
	}

	public long getMethods() {
		return methods;
	}

	public void setMethods(long methods) {
		this.methods = methods;
	}

	public long getClasses() {
		return classes;
	}

	public void setClasses(long classes) {
		this.classes = classes;
	}

	public long getFiles() {
		return files;
	}

	public void setFiles(long files) {
		this.files = files;
	}

	public long getPackages() {
		return packages;
	}

	public void setPackages(long packages) {
		this.packages = packages;
	}

	public long getJavadocLines() {
		return javadocLines;
	}

	public void setJavadocLines(long javadocLines) {
		this.javadocLines = javadocLines;
	}

	public long getJavadocBlocks() {
		return javadocBlocks;
	}

	public void setJavadocBlocks(long javadocBlocks) {
		this.javadocBlocks = javadocBlocks;
	}

	public boolean hasJavadoc() {
		return javadoc;
	}

	public void setJavadoc(boolean javadoc) {
		this.javadoc = javadoc;
	}

	public void addMeasures(Measures measures) {
		loc += measures.getLoc();
		ncloc += measures.getNcloc();
		complexity += measures.getComplexity();
		branches += measures.getBranches();
		statements += measures.getStatements();
		javadocBlocks += measures.getJavadocBlocks();
		commentLines += measures.getCommentLines();
		javadocLines += measures.getJavadocLines();
		methods += measures.getMethods();
		classes += measures.getClasses();
		files += measures.getFiles();
		packages += measures.getPackages();
		blankLines += measures.getBlankLines();
	}
}