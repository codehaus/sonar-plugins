/**
 * 
 */
package com.echosource.ada;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.resources.AbstractLanguage;

/**
 * @author Akram Ben Aissi
 * 
 */
public class Ada extends AbstractLanguage {

  private Configuration configuration;

  /** The language name */
  public static final String LANGUAGE_NAME = "Ada";

  /** The php language key. */
  public static final String LANGUAGE_KEY = "ada";

  public static final String[] ADA_RESERVED_KEYWORDS = { "abort", "abs", "abstract", "accept", "access", "aliased", "all", "and", "array",
    "at", "begin", "body", "case", "constant", "declare", "delay", "delta", "digits", "do", "else", "elsif", "end of line", "end", "entry",
    "exception", "exit", "for", "function", "generic", "goto", "if", "in", "is", "limited", "loop", "mod", "new", "not", "null", "of",
    "or", "other control function", "others", "out", "package", "pair of quotation mark", "pragma", "private", "procedure", "protected",
    "quotation mark", "raise", "range", "record", "rem", "renames", "requeue", "return", "reverse", "select", "separate", "subtype",
    "tagged", "task", "terminate", "then", "type", "until", "use", "when", "while", "with", "xor", };

  public static final String[] ADA_RESERVED_VARIABLES = {};

  /** The language instance. */
  public static Ada INSTANCE = new Ada();

  private static final String FILE_SUFFIXES_KEY = "sonar.ada.file.suffixes";

  private static final String[] DEFAULT_SUFFIXES = { "adb", "ads" };

  /**
   * Default constructor.
   */
  public Ada() {
    super(LANGUAGE_KEY, LANGUAGE_NAME);
  }

  /**
   * Allows to know if the given file name has a valid suffix.
   * 
   * @param fileName
   *          String representing the file name
   * @return boolean <code>true</code> if the file name's suffix is known, <code>false</code> any other way
   */
  public boolean hasValidSuffixes(String fileName) {
    String pathLowerCase = StringUtils.lowerCase(fileName);
    for (String suffix : INSTANCE.getFileSuffixes()) {
      if (pathLowerCase.endsWith("." + StringUtils.lowerCase(suffix))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the file suffixes.
   * 
   * @return the file suffixes
   * @see org.sonar.api.resources.Language#getFileSuffixes()
   */

  public String[] getFileSuffixes() {
    String[] suffixes = DEFAULT_SUFFIXES;
    if (configuration != null) {
      suffixes = configuration.getStringArray(FILE_SUFFIXES_KEY);
    }
    return suffixes;
  }

}
