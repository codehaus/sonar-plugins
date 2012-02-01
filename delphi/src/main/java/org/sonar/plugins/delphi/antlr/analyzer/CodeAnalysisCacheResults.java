/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.antlr.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.plugins.delphi.core.language.ClassInterface;
import org.sonar.plugins.delphi.core.language.FunctionInterface;
import org.sonar.plugins.delphi.core.language.UnitInterface;
import org.sonar.plugins.delphi.core.language.impl.UnresolvedFunctionCall;

/**
 * Holds cached results in static variables
 */
public class CodeAnalysisCacheResults {

  private static final int INITIAL_CAPACITY = 100;
  protected static Set<UnitInterface> allUnits = new HashSet<UnitInterface>(INITIAL_CAPACITY); // all parsed units
  protected static Map<String, ClassInterface> allClasses = new HashMap<String, ClassInterface>(INITIAL_CAPACITY); // all parsed classes in
                                                                                                                   // // a project
  protected static Map<String, FunctionInterface> allFunctions = new HashMap<String, FunctionInterface>(INITIAL_CAPACITY); // all parsed
                                                                                                                           // functions in a
                                                                                                                           // project
  protected static Map<String, UnresolvedFunctionCall> unresolvedCalls = new HashMap<String, UnresolvedFunctionCall>(INITIAL_CAPACITY); // unresolved
                                                                                                                                        // calls

  /**
   * resets results chache
   */
  public static void resetCache() {
    allClasses.clear();
    allFunctions.clear();
    allUnits.clear();
    unresolvedCalls.clear();
  }

  /**
   * @return map of unresolved function calls
   */
  public Map<String, UnresolvedFunctionCall> getUnresolvedCalls() {
    return unresolvedCalls;
  }

  /**
   * Adds a unresolved function call
   * 
   * @param name
   *          unresolved function name
   * @param call
   *          the unresolved call
   */
  public void addUnresolvedCall(String name, UnresolvedFunctionCall call) {
    unresolvedCalls.put(name, call);
  }

  /**
   * @param className
   *          class name
   * @return cached class if found, null otherwise
   */
  public ClassInterface getCachedClass(String className) {
    return allClasses.get(className);
  }

  /**
   * @param funcName
   *          function name
   * @return cached function if found, null otherwise
   */
  public FunctionInterface getCachedFunction(String funcName) {
    return allFunctions.get(funcName);
  }

  /**
   * @param unit
   *          unit
   * @return true if unit was cached
   */
  public boolean hasCachedUnit(UnitInterface unit) {
    return allUnits.contains(unit);
  }

  /**
   * @return set of cached units
   */
  public Set<UnitInterface> getCachedUnits() {
    return allUnits;
  }

  /**
   * @return list of cached units
   */
  public List<UnitInterface> getCachedUnitsAsList() {
    List<UnitInterface> result = new ArrayList<UnitInterface>();
    result.addAll(allUnits);
    return result;
  }

  /**
   * add new unit to cache
   * 
   * @param unit
   *          unit to add
   */
  public void cacheUnit(UnitInterface unit) {
    allUnits.add(unit);
  }

  /**
   * add new class to cache
   * 
   * @param className
   *          class name
   * @param clazz
   *          class
   */
  public void cacheClass(String className, ClassInterface clazz) {
    allClasses.put(className, clazz);
  }

  /**
   * add new function to cache
   * 
   * @param funcName
   *          function name
   * @param function
   *          function
   */
  public void cacheFunction(String funcName, FunctionInterface function) {
    allFunctions.put(funcName, function);
  }

}
