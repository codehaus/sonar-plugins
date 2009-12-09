package org.codehaus.sonar.plugins.testability.client.model;

/**
 * Interfaces for classes that have Data on Complexity, LoD, ...
 * @author cedric.lamalle
 *
 */
public interface HasCostData {

  int getCyclomaticComplexity();

  int getGlobal();

  int getLawOfDemeter();

  int getOverall();

}