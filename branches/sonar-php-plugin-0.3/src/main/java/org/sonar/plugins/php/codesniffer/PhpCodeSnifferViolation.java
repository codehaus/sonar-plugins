package org.sonar.plugins.php.codesniffer;

/**
 * @author Akram Ben Aissi
 * 
 */
public class PhpCodeSnifferViolation {

  /**
     * 
     */
  private String type;
  /**
     * 
     */
  private String longMessage;
  /**
     * 
     */
  private Integer line;

  /**
     * 
     */
  protected String className;
  /**
     * 
     */
  protected String sourcePath;

  public String getType() {
    return type;
  }

  public void parseStart(String attrValue) {
    try {
      line = Integer.parseInt(attrValue);
    } catch (NumberFormatException e) {
      line = null;
    }
  }

  public String getLongMessage() {
    return longMessage;
  }

  public Integer getStart() {
    return line;
  }

  public String getClassName() {
    return className;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public String getSonarJavaFileKey() {
    if (className.indexOf('$') > -1) {
      return className.substring(0, className.indexOf('$'));
    }
    return className;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @param longMessage
   *          the longMessage to set
   */
  public void setLongMessage(String longMessage) {
    this.longMessage = longMessage;
  }

  /**
   * @param line
   *          the line to set
   */
  public void setLine(Integer line) {
    this.line = line;
  }

  /**
   * @param className
   *          the className to set
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * @param sourcePath
   *          the sourcePath to set
   */
  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }

}