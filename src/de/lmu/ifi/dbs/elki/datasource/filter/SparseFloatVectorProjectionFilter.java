package de.lmu.ifi.dbs.elki.datasource.filter;

import java.util.BitSet;
import java.util.Collections;
import java.util.Map;

import de.lmu.ifi.dbs.elki.data.SparseFloatVector;
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.data.type.VectorFieldTypeInformation;
import de.lmu.ifi.dbs.elki.utilities.Util;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * <p>
 * Parser to project the ParsingResult obtained by a suitable base parser onto a
 * selected subset of attributes.
 * </p>
 * 
 * @author Arthur Zimek
 * 
 */
public class SparseFloatVectorProjectionFilter extends AbstractFeatureSelectionFilter<SparseFloatVector> {
  /**
   * Constructor.
   * 
   * @param selectedAttributes
   */
  public SparseFloatVectorProjectionFilter(BitSet selectedAttributes) {
    super(selectedAttributes);
  }

  @Override
  protected SparseFloatVector filterSingleObject(SparseFloatVector obj) {
    return Util.project(obj, getSelectedAttributes());
  }

  @Override
  protected SimpleTypeInformation<? super SparseFloatVector> getInputTypeRestriction() {
    return TypeUtil.SPARSE_FLOAT_FIELD;
  }

  @SuppressWarnings("unused")
  @Override
  protected SimpleTypeInformation<? super SparseFloatVector> convertedType(SimpleTypeInformation<SparseFloatVector> in) {
    final Map<Integer, Float> emptyMap = Collections.emptyMap();
    return new VectorFieldTypeInformation<SparseFloatVector>(SparseFloatVector.class, getDimensionality(), new SparseFloatVector(emptyMap, getDimensionality()));
  }

  /**
   * Parameterization class.
   *
   * @author Erich Schubert
   *
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractFeatureSelectionFilter.Parameterizer<SparseFloatVector> {
    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
    }

    @Override
    protected SparseFloatVectorProjectionFilter makeInstance() {
      return new SparseFloatVectorProjectionFilter(selectedAttributes);
    }
  }
}