package org.sonar.plugin.groovyGMNarc.decorators;

import org.sonar.plugin.groovyGMNarc.Groovy;
import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.CodeColorizer;
import org.sonar.colorizer.Tokenizer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: skessler
 * Date: Feb 2, 2010
 * Time: 1:42:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyCodeColorizer extends CodeColorizerFormat {


  public GroovyCodeColorizer() {
    super(Groovy.KEY);
  }

  @Override
  public List<Tokenizer> getTokenizers() {
    return CodeColorizer.Format.GROOVY.getTokenizers();
  }


}
