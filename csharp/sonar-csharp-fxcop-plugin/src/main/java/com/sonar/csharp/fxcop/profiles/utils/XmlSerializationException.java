/*
 * Sonar C# Plugin :: FxCop
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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

package com.sonar.csharp.fxcop.profiles.utils;

/**
 * An exception for xml serialization errors.
 */
public class XmlSerializationException extends RuntimeException {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -544645686195710683L;

  /**
   * Constructs a @link{XmlSerializationException}.
   */
  public XmlSerializationException() {
  }

  /**
   * Constructs a @link{XmlSerializationException}.
   * 
   * @param message
   */
  public XmlSerializationException(String message) {
    super(message);

  }

  /**
   * Constructs a @link{XmlSerializationException}.
   * 
   * @param cause
   */
  public XmlSerializationException(Throwable cause) {
    super(cause);

  }

  /**
   * Constructs a @link{XmlSerializationException}.
   * 
   * @param message
   * @param cause
   */
  public XmlSerializationException(String message, Throwable cause) {
    super(message, cause);

  }

}
