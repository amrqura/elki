package de.lmu.ifi.dbs.elki.utilities.datastructures;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2011
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.List;

/**
 * Static adapter class to use a {@link java.util.List} in an array API.
 * 
 * @author Erich Schubert
 * 
 * @param <T> Data object type.
 */
public class ListArrayAdapter<T> implements ArrayAdapter<T, List<? extends T>> {
  /**
   * Static instance.
   */
  public static ListArrayAdapter<?> STATIC = new ListArrayAdapter<Object>();

  @Override
  public int size(List<? extends T> array) {
    return array.size();
  }

  @Override
  public T get(List<? extends T> array, int off) throws IndexOutOfBoundsException {
    return array.get(off);
  }

  /**
   * Cast the static instance.
   * 
   * @param dummy Dummy variable, for type inference
   * @return Static instance
   */
  @SuppressWarnings("unchecked")
  public static <T> ListArrayAdapter<T> getStatic(List<T> dummy) {
    return (ListArrayAdapter<T>) STATIC;
  }
}