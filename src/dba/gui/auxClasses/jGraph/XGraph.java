package dba.gui.auxClasses.jGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import data.Attribute;
import data.RelationSchema;

/**
 * mxGraph-extension to provide custom behaviour
 */
public class XGraph extends mxGraph {

  // Overrides method to provide a cell label in the display
  @Override
  public String convertValueToString(Object cell) {
	if (cell instanceof mxCell) {
	  mxCell xCell = (mxCell) cell;
	  Object value = xCell.getValue();

	  if (value instanceof RelationSchema) {
		// return only the name of the relation
		return ((RelationSchema) value).getName();
	  } else if (value instanceof Attribute) {
		// return a space (for pk/fk icons) and the name of the
		// attribute
		String result = "         ";

		result += ((Attribute) value).getName();
		return result;
	  }
	}

	return super.convertValueToString(cell);
  }

  /**
   * Disallow selection of Edges
   */
  @Override
  public boolean isCellSelectable(Object cell) {
	if (cell != null) {
	  if (cell instanceof mxCell) {
		mxCell myCell = (mxCell) cell;
		if (myCell.isEdge()) {
		  return false;
		}
	  }
	}
	return super.isCellSelectable(cell);
  }

}
