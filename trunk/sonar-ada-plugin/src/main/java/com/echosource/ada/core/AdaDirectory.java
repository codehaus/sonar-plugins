package com.echosource.ada.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.WildcardPattern;

import com.echosource.ada.Ada;

public class AdaDirectory extends Resource {

  public static final String DEFAULT_PACKAGE_NAME = "[default]";

  public AdaDirectory(String key) {
    super();
    setKey(StringUtils.defaultIfEmpty(StringUtils.trim(key), DEFAULT_PACKAGE_NAME));
  }

  public boolean isDefault() {
    return StringUtils.equals(getKey(), DEFAULT_PACKAGE_NAME);
  }

  public boolean matchFilePattern(String antPattern) {
    String patternWithoutFileSuffix = StringUtils.substringBeforeLast(antPattern, ".");
    WildcardPattern matcher = WildcardPattern.create(patternWithoutFileSuffix, ".");
    return matcher.match(getKey());
  }

  public String getDescription() {
    return null;
  }

  public String getScope() {
    return Resource.SCOPE_SPACE;
  }

  public String getQualifier() {
    return Resource.QUALIFIER_DIRECTORY;
  }

  public String getName() {
    return getKey();
  }

  public Resource<?> getParent() {
    return null;
  }

  public String getLongName() {
    return null;
  }

  public Language getLanguage() {
    return Ada.INSTANCE;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("key", getKey()).toString();
  }
}