package org.sonar.plugins.profiler;

import java.io.File;
import java.lang.reflect.Method;

/**
 * This class was developed for JProfiler 5.2.4 and inspired by idea from
 * <a href="http://www.sonatype.com/people/2009/09/profiling-maven-tests/">Sonatype Blog - Profiling Maven Tests</a>.
 *
 * @author Evgeny Mandrikov
 */
final class JProfiler implements Profiler {
  private Class controllerClass;
  private Method saveSnapshot;
  private Method startCPURecording;
  private Method stopCPURecording;
  private Method startAllocRecording;
  private Method stopAllocRecording;

  public JProfiler() {
    try {
      // Controller
      controllerClass = Class.forName("com.jprofiler.api.agent.Controller");
      startCPURecording = getMethod("startCPURecording", boolean.class);
      stopCPURecording = getMethod("stopCPURecording");
      startAllocRecording = getMethod("startAllocRecording", boolean.class);
      stopAllocRecording = getMethod("stopAllocRecording");
      saveSnapshot = getMethod("saveSnapshot", File.class);
    } catch (Exception e) {
      // ignore - profiler not present
      controllerClass = null;
    }
  }

  private Method getMethod(String name, Class<?>... parameterTypes) {
    if (controllerClass == null) {
      // Should never happen
      return null;
    }
    try {
      return controllerClass.getMethod(name, parameterTypes);
    } catch (Exception e) {
      throw new ProfilerException("Profiler was active, but failed due: " + e.getMessage(), e);
    }
  }

  public void start() {
    // On startup, JProfiler does not record any data. The various recording subsystems have to be
    // switched on programatically.
    if (controllerClass != null) {
      // Controller.startCPURecording(true);
      invokeStatic(startCPURecording, true);
      //Controller.startAllocRecording(true);
      invokeStatic(startAllocRecording, true);
    }
  }

  public void stop() {
    // You can switch off recording at any point. Recording can be switched on again.
    if (controllerClass != null) {
      // Controller.stopCPURecording();
      invokeStatic(stopCPURecording);
      // Controller.stopAllocRecording();
      invokeStatic(stopAllocRecording);
    }
  }

  public void saveSnapshot(String filename) {
    if (controllerClass != null) {
      File file = new File(filename + ".jps");
      //noinspection ResultOfMethodCallIgnored
      file.getParentFile().mkdirs();
      // Controller.saveSnapshot(file);
      invokeStatic(saveSnapshot, file);
    }
  }

  private static void invokeStatic(Method method, Object... args) {
    if (method == null) {
      // Should never happen
      return;
    }
    try {
      method.invoke(null, args);
    } catch (Exception e) {
      throw new ProfilerException("Profiler was active, but failed due: " + e.getMessage(), e);
    }
  }
}
