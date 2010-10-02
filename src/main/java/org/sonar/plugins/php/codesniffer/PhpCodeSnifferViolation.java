package org.sonar.plugins.php.codesniffer;

/**
 * @author Akram Ben Aissi
 * 
 */
public class PhpCodeSnifferViolation {

  /**
   * The ruleKey of the violated rule.
   */
  private String ruleKey;

  /**
   * The ruleKey of the violated rule.
   */
  private String ruleName;

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
  private Integer comlumn;
  /**
     * 
     */
  protected String fileName;
  /**
   * 
   * 
   */
  protected String sourcePath;

  /**
   * @return
   */
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

  /**
   * @return
   */
  public String getLongMessage() {
    return longMessage;
  }

  /**
   * @return
   */
  public Integer getLine() {
    return line;
  }

  /**
   * @return
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @return
   */
  public String getSourcePath() {
    return sourcePath;
  }

  /**
   * @return
   */
  public String getSonarJavaFileKey() {
    if (fileName.indexOf('$') > -1) {
      return fileName.substring(0, fileName.indexOf('$'));
    }
    return fileName;
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
   * @param fileName
   *          the fileName to set
   */
  public void setFileName(String className) {
    this.fileName = className;
  }

  /**
   * @param sourcePath
   *          the sourcePath to set
   */
  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }

  /**
   * @return the ruleKey
   */
  public String getRuleKey() {
    return ruleKey;
  }

  /**
   * @param ruleKey
   *          the ruleKey to set
   */
  public void setRuleKey(String key) {
    this.ruleKey = key;
  }

  /**
   * @return the ruleName
   */
  public String getRuleName() {
    return ruleName;
  }

  /**
   * @param ruleName
   *          the ruleName to set
   */
  public void setRuleName(String sourceKey) {
    this.ruleName = sourceKey;
  }

  /**
   * @return the comlumn
   */
  public Integer getComlumn() {
    return comlumn;
  }

  /**
   * @param comlumn
   *          the comlumn to set
   */
  public void setComlumn(Integer comlumn) {
    this.comlumn = comlumn;
  }

}