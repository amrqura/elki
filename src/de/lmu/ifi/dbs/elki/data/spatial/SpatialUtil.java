package de.lmu.ifi.dbs.elki.data.spatial;

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

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;
import de.lmu.ifi.dbs.elki.data.ModifiableHyperBoundingBox;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.elki.utilities.datastructures.ArrayAdapter;

/**
 * Utility class with spatial functions.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.landmark
 * 
 * @apiviz.uses SpatialComparable oneway - - «compares»
 * @apiviz.has HyperBoundingBox oneway - - «creates»
 */
// IMPORTANT NOTE: when editing this class, bear in mind that the dimension
// numbers start at 1 or 0 depending on the context!
public final class SpatialUtil {
  /**
   * Copy the MBR of an instance.
   * 
   * @param obj Object to copy
   * @param adapter Adapter
   * @return (Modifiable) bounding box
   */
  public static <E> ModifiableHyperBoundingBox copyMBR(E obj, SpatialAdapter<? super E> adapter) {
    final int dim = adapter.getDimensionality(obj);
    double[] min = new double[dim];
    double[] max = new double[dim];
    for(int i = 0; i < dim; i++) {
      min[i] = adapter.getMin(obj, i);
      max[i] = adapter.getMax(obj, i);
    }
    return new ModifiableHyperBoundingBox(min, max);
  }

  /**
   * Compute the volume (area) of the union of two MBRs
   * 
   * @param r1 First object
   * @param a1 Adapter for first object
   * @param r2 Second object
   * @param a2 Adapter for second object
   * @return
   */
  public static <E1, E2> double volumeUnion(E1 r1, SpatialAdapter<? super E1> a1, E2 r2, SpatialAdapter<? super E2> a2) {
    final int dim1 = a1.getDimensionality(r1);
    final int dim2 = a2.getDimensionality(r2);
    assert (!LoggingConfiguration.DEBUG || dim1 == dim2);
    double volume = 1.0;
    for(int i = 0; i < dim1; i++) {
      final double min = Math.min(a1.getMin(r1, i), a2.getMin(r2, i));
      final double max = Math.max(a1.getMax(r1, i), a2.getMax(r2, i));
      volume *= (max - min);
    }
    return volume;
  }

  /**
   * Returns a clone of the minimum hyper point.
   * 
   * @return the minimum hyper point
   */
  public static double[] getMin(SpatialComparable box) {
    final int dim = box.getDimensionality();
    double[] min = new double[dim];
    for(int i = 0; i < dim; i++) {
      min[i] = box.getMin(i + 1);
    }
    return min;
  }

  /**
   * Returns a clone of the maximum hyper point.
   * 
   * @return the maximum hyper point
   */
  public static double[] getMax(SpatialComparable box) {
    final int dim = box.getDimensionality();
    double[] max = new double[dim];
    for(int i = 0; i < dim; i++) {
      max[i] = box.getMax(i + 1);
    }
    return max;
  }

  /**
   * Returns true if the two SpatialComparables intersect, false otherwise.
   * 
   * @param box1 the first SpatialComparable
   * @param box2 the first SpatialComparable
   * @return true if the SpatialComparables intersect, false otherwise
   */
  public static boolean intersects(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("The spatial objects do not have the same dimensionality!");
    }
    boolean intersect = true;
    for(int i = 1; i <= dim; i++) {
      if(box1.getMin(i) > box2.getMax(i) || box1.getMax(i) < box2.getMin(i)) {
        intersect = false;
        break;
      }
    }
    return intersect;
  }

  /**
   * Returns true if the two spatial objects intersect (overlap), false
   * otherwise.
   * 
   * @param box1 the first object
   * @param a1 Spatial adapter for first object
   * @param box2 the second object
   * @param a2 Spatial adapter for second object
   * @return true if the spatial objects intersect, false otherwise
   */
  public static <E1, E2> boolean intersects(E1 box1, SpatialAdapter<? super E1> a1, E2 box2, SpatialAdapter<? super E2> a2) {
    final int dim1 = a1.getDimensionality(box1);
    assert (!LoggingConfiguration.DEBUG || dim1 == a2.getDimensionality(box2));
    for(int i = 0; i < dim1; i++) {
      if(a1.getMin(box1, i) > a2.getMax(box2, i) || a1.getMax(box1, i) < a2.getMin(box2, i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if the first SpatialComparable contains the second
   * SpatialComparable, false otherwise.
   * 
   * @param box1 the outer SpatialComparable
   * @param box2 the inner SpatialComparable
   * @return true if the first SpatialComparable contains the second
   *         SpatialComparable, false otherwise
   */
  public static boolean contains(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("The spatial objects do not have the same dimensionality!");
    }

    boolean contains = true;
    for(int i = 1; i <= dim; i++) {
      if(box1.getMin(i) > box2.getMin(i) || box1.getMax(i) < box2.getMax(i)) {
        contains = false;
        break;
      }
    }
    return contains;
  }

  /**
   * Returns true if this SpatialComparable contains the given point, false
   * otherwise.
   * 
   * @param point the point to be tested for containment
   * @return true if this SpatialComparable contains the given point, false
   *         otherwise
   */
  public static boolean contains(SpatialComparable box, double[] point) {
    final int dim = box.getDimensionality();
    if(dim != point.length) {
      throw new IllegalArgumentException("This HyperBoundingBox and the given point need same dimensionality");
    }

    boolean contains = true;
    for(int i = 0; i < dim; i++) {
      if(box.getMin(i + 1) > point[i] || box.getMax(i + 1) < point[i]) {
        contains = false;
        break;
      }
    }
    return contains;
  }

  /**
   * Returns true if the first spatial object contains the second spatial
   * object, false otherwise.
   * 
   * @param box1 the outer object
   * @param a1 Spatial adapter for first object
   * @param box2 the inner object
   * @param a2 Spatial adapter for second object
   * @return true if the first object contains the second object, false
   *         otherwise
   */
  public static <E1, E2> boolean contains(E1 box1, SpatialAdapter<? super E1> a1, E2 box2, SpatialAdapter<? super E2> a2) {
    final int dim1 = a1.getDimensionality(box1);
    assert (!LoggingConfiguration.DEBUG || dim1 == a2.getDimensionality(box2));

    for(int i = 0; i < dim1; i++) {
      if(a1.getMin(box1, i) > a2.getMin(box2, i) || a1.getMax(box1, i) < a2.getMax(box2, i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Computes the volume of this SpatialComparable
   * 
   * @return the volume of this SpatialComparable
   */
  public static double volume(SpatialComparable box) {
    double vol = 1;
    final int dim = box.getDimensionality();
    for(int i = 1; i <= dim; i++) {
      vol *= box.getMax(i) - box.getMin(i);
    }
    return vol;
  }

  /**
   * Computes the perimeter of this SpatialComparable.
   * 
   * @return the perimeter of this SpatialComparable
   */
  public static double perimeter(SpatialComparable box) {
    double perimeter = 0;
    final int dim = box.getDimensionality();
    for(int i = 1; i <= dim; i++) {
      perimeter += box.getMax(i) - box.getMin(i);
    }
    return perimeter;
  }

  /**
   * Computes the volume of the overlapping box between two SpatialComparables
   * and return the relation between the volume of the overlapping box and the
   * volume of both SpatialComparable.
   * 
   * @param box1 the first SpatialComparable
   * @param box2 the second SpatialComparable
   * @return the overlap volume in relation to the singular volumes.
   */
  public static double relativeOverlap(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("This HyperBoundingBox and the given HyperBoundingBox need same dimensionality");
    }

    // the maximal and minimal value of the overlap box.
    double omax, omin;

    // the overlap volume
    double overlap = 1.0;

    for(int i = 1; i <= dim; i++) {
      // The maximal value of that overlap box in the current
      // dimension is the minimum of the max values.
      omax = Math.min(box1.getMax(i), box2.getMax(i));
      // The minimal value is the maximum of the min values.
      omin = Math.max(box1.getMin(i), box2.getMin(i));

      // if omax <= omin in any dimension, the overlap box has a volume of zero
      if(omax <= omin) {
        return 0.0;
      }

      overlap *= omax - omin;
    }

    return overlap / (volume(box1) + volume(box2));
  }

  /**
   * Computes the union HyperBoundingBox of two SpatialComparables.
   * 
   * @param box1 the first SpatialComparable
   * @param box2 the second SpatialComparable
   * @return the union HyperBoundingBox of this HyperBoundingBox and the given
   *         HyperBoundingBox
   */
  public static HyperBoundingBox union(SpatialComparable box1, SpatialComparable box2) {
    final int dim = box1.getDimensionality();
    if(dim != box2.getDimensionality()) {
      throw new IllegalArgumentException("This HyperBoundingBox and the given HyperBoundingBox need same dimensionality");
    }

    double[] min = new double[dim];
    double[] max = new double[dim];

    for(int i = 1; i <= dim; i++) {
      min[i - 1] = Math.min(box1.getMin(i), box2.getMin(i));
      max[i - 1] = Math.max(box1.getMax(i), box2.getMax(i));
    }
    return new HyperBoundingBox(min, max);
  }

  /**
   * Returns the union of the two specified MBRs. Tolerant of "null" values.
   * 
   * @param mbr1 the first MBR
   * @param mbr2 the second MBR
   * @return the union of the two specified MBRs
   */
  public static HyperBoundingBox unionTolerant(SpatialComparable mbr1, SpatialComparable mbr2) {
    if(mbr1 == null && mbr2 == null) {
      return null;
    }
    if(mbr1 == null) {
      // Clone - intentionally
      return new HyperBoundingBox(mbr2);
    }
    if(mbr2 == null) {
      // Clone - intentionally
      return new HyperBoundingBox(mbr1);
    }
    return union(mbr1, mbr2);
  }

  /**
   * Compute the union of two objects.
   * 
   * @param o1 First object
   * @param a1 Spatial adapter for first object
   * @param o2 Second object
   * @param a2 Spatial adapter for second object
   * @return Modifiable Hyper Bounding Box
   */
  public static <A, B> ModifiableHyperBoundingBox union(A o1, SpatialAdapter<A> a1, B o2, SpatialAdapter<B> a2) {
    final int dim = a1.getDimensionality(o1);
    double[] min = new double[dim];
    double[] max = new double[dim];
    for(int d = 0; d < dim; d++) {
      min[d] = Math.min(a1.getMin(o1, d), a2.getMin(o2, d));
      max[d] = Math.max(a1.getMax(o1, d), a2.getMax(o2, d));
    }
    return new ModifiableHyperBoundingBox(min, max);
  }

  /**
   * Compute the union of a number of objects.
   * 
   * @param data Object
   * @param getter Array adapter
   * @param adapter Spatial adapter
   * @return Modifiable Hyper Bounding Box
   */
  public static <E, A> ModifiableHyperBoundingBox union(A data, ArrayAdapter<E, A> getter, SpatialAdapter<E> adapter) {
    final int num = getter.size(data);
    assert (num > 0);
    final E first = getter.get(data, 0);
    final int dim = adapter.getDimensionality(first);
    double[] min = new double[dim];
    double[] max = new double[dim];
    for(int d = 0; d < dim; d++) {
      min[d] = adapter.getMin(first, d);
      max[d] = adapter.getMax(first, d);
    }
    for(int i = 1; i < num; i++) {
      E next = getter.get(data, i);
      for(int d = 0; d < dim; d++) {
        min[d] = Math.min(min[d], adapter.getMin(next, d));
        max[d] = Math.max(max[d], adapter.getMax(next, d));
      }
    }
    return new ModifiableHyperBoundingBox(min, max);
  }

  /**
   * Compute the union of two objects as a flat MBR (low-level, for index
   * structures)
   * 
   * @param o1 First object
   * @param a1 Spatial adapter for first object
   * @param o2 Second object
   * @param a2 Spatial adapter for second object
   * @return Flat MBR
   */
  public static <A, B> double[] unionFlatMBR(A o1, SpatialAdapter<A> a1, B o2, SpatialAdapter<B> a2) {
    final int dim = a1.getDimensionality(o1);
    double[] mbr = new double[2 * dim];
    for(int d = 0; d < dim; d++) {
      mbr[d] = Math.min(a1.getMin(o1, d), a2.getMin(o2, d));
      mbr[dim + d] = Math.max(a1.getMax(o1, d), a2.getMax(o2, d));
    }
    return mbr;
  }

  /**
   * Compute the union of a number of objects as a flat MBR (low-level, for
   * index structures)
   * 
   * @param data Object
   * @param getter Array adapter
   * @param adapter Spatial adapter
   * @return Flat MBR
   */
  public static <E, A> double[] unionFlatMBR(A data, ArrayAdapter<E, A> getter, SpatialAdapter<E> adapter) {
    final int num = getter.size(data);
    assert (num > 0) : "Cannot compute MBR of empty set.";
    final E first = getter.get(data, 0);
    final int dim = adapter.getDimensionality(first);
    double[] mbr = new double[2 * dim];
    for(int d = 0; d < dim; d++) {
      mbr[d] = adapter.getMin(first, d);
      mbr[dim + d] = adapter.getMax(first, d);
    }
    for(int i = 1; i < num; i++) {
      E next = getter.get(data, i);
      for(int d = 0; d < dim; d++) {
        mbr[d] = Math.min(mbr[d], adapter.getMin(next, d));
        mbr[dim + d] = Math.max(mbr[dim + d], adapter.getMax(next, d));
      }
    }
    return mbr;
  }

  /**
   * Returns the centroid of this SpatialComparable.
   * 
   * @param obj Spatial object to process
   * @return the centroid of this SpatialComparable
   */
  public static double[] centroid(SpatialComparable obj) {
    final int dim = obj.getDimensionality();
    double[] centroid = new double[dim];
    for(int d = 1; d <= dim; d++) {
      centroid[d - 1] = (obj.getMax(d) + obj.getMin(d)) / 2.0;
    }
    return centroid;
  }

  /**
   * Returns the centroid of the specified values of this SpatialComparable.
   * 
   * @param obj Spatial object to process
   * @param start the start dimension to be considered
   * @param end the end dimension to be considered
   * @return the centroid of the specified values of this SpatialComparable
   */
  public static double[] centroid(SpatialComparable obj, int start, int end) {
    double[] centroid = new double[end - start + 1];
    for(int d = start - 1; d < end; d++) {
      centroid[d - start + 1] = (obj.getMax(d + 1) + obj.getMin(d + 1)) / 2.0;
    }
    return centroid;
  }

  /**
   * Test two SpatialComparables for equality.
   * 
   * @param box1 First bounding box
   * @param box2 Second bounding box
   * @return true when the boxes are equal
   */
  public static boolean equals(SpatialComparable box1, SpatialComparable box2) {
    if(box1.getDimensionality() != box2.getDimensionality()) {
      return false;
    }
    for(int i = 1; i <= box1.getDimensionality(); i++) {
      if(box1.getMin(i) != box2.getMin(i)) {
        return false;
      }
      if(box1.getMax(i) != box2.getMax(i)) {
        return false;
      }
    }
    return true;
  }
}