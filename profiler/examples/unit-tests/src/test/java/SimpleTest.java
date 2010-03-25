import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.profiler.Profiler;
import org.sonar.plugins.profiler.ProfilerFactory;
import org.sonar.plugins.profiler.junit.ProfilerWatchman;

/**
 * @author Evgeny Mandrikov
 */
public class SimpleTest {

  @SuppressWarnings({"UnusedDeclaration"})
  @Rule
  public ProfilerWatchman profilerWatchman = new ProfilerWatchman();

  private static final int COUNT = 100000;

  private SimpleClass obj;

  @Before
  public void setUp() {
    obj = new SimpleClass(COUNT);
  }

  @Test
  public void testCalculateSines() {
    obj.calculateSines();
  }

  @Test
  public void testCalculateSquareRoots() {
    obj.calculateSquareRoots();
  }

  @Test
  public void testCalculateLogs() {
    obj.calculateLogs();
  }

  @Test
  public void testAll() {
    obj.calculateSines();
    obj.calculateSquareRoots();
    obj.calculateLogs();
  }
}
