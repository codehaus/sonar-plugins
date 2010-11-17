package org.sonar.plugins.technicaldebt;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class TechnicalDebtWidgetTest {

  @Test
  public void defineWidget() {
    TechnicalDebtWidget widget = new TechnicalDebtWidget();
    assertThat(widget.getId(), notNullValue());
    assertThat(widget.getTitle(), notNullValue());
    assertThat(getClass().getResource(widget.getTemplatePath()), notNullValue());
  }

}
