import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class SimpleClass {
  private final int count;

  // These lists hold objects to illustate memory profiling
  private List<Double> sines;
  private List<Double> squareRoots;
  private List<Double> logs;

  public SimpleClass(int count) {
    this.count = count;
    sines = new ArrayList<Double>(count);
    squareRoots = new ArrayList<Double>(count);
    logs = new ArrayList<Double>(count);
  }

  public void calculateSines() {
    double increment = (Math.PI / 2) / count;
    for (int i = 0; i < count; i++) {
      sines.add(Math.sin(increment * i));
    }
  }

  public void calculateSquareRoots() {
    for (int i = 0; i < count; i++) {
      squareRoots.add(Math.sqrt(i));
    }
  }

  public void calculateLogs() {
    for (int i = 0; i < count; i++) {
      logs.add(Math.log(i + 1));
    }
  }

  public List<Double> getSines() {
    return sines;
  }

  public List<Double> getSquareRoots() {
    return squareRoots;
  }

  public List<Double> getLogs() {
    return logs;
  }
}
