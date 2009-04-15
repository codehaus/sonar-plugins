
package org.sonar.plugins.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationDefinition {
  String value() default "";
  
  String value2() default "";
}
