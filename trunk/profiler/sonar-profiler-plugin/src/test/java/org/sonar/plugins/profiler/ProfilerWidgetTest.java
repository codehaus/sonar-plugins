package org.sonar.plugins.profiler;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class ProfilerWidgetTest {
  private ProfilerWidget widget;

  @Before
  public void setUp() {
    widget = new ProfilerWidget();
  }

  @Test
  public void test() throws Exception {
    assertThat(widget.getId(), notNullValue());
    assertThat(widget.getTitle(), notNullValue());

    String path = widget.getTemplatePath();
    assertThat(getClass().getResource(path), notNullValue());
  }
}
