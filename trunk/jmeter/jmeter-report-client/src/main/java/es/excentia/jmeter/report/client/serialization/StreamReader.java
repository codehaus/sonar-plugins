/*
 * JMeter Report Client
 * Copyright (C) 2010 eXcentia
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

package es.excentia.jmeter.report.client.serialization;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import es.excentia.jmeter.report.client.exception.SerializationException;

public abstract class StreamReader<T> {
  
  protected InputStream is;

  public StreamReader(InputStream is) {
    this.is = is;
  }
  
  public abstract T read();
  
  public final T readUntilEnd() {
    T obj = read();
    
    try {

      while (is.read()!=-1);
      
    } catch (EOFException e) {
      // Do nothing when end is reached
    } catch (IOException e) {
        throw new SerializationException(e);
    } 
    
    return obj;

  }
  
}
