/******************************************************************************
 * Copyright: GPL v3                                                          *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package dba.gui.auxClasses.jGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import dbaCore.data.Attribute;
import dbaCore.data.FunctionalDependency;
import dbaCore.data.RelationSchema;

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
      } else if(value instanceof FunctionalDependency){
        return "";
      }
    }

    return super.convertValueToString(cell);
  }

  /**
   * Disallow selection of invisible Cells
   */
  @Override
  public boolean isCellSelectable(Object cell) {
    if (cell != null) {
      if (cell instanceof mxCell) {
        if(((mxCell)cell).getStyle().contains("INVISIBLE")){
          return false;
        }
      }
    }
    return super.isCellSelectable(cell);
  }

  @Override
  public boolean  isValidSource(Object cell){
    return false;
  }

  @Override
  public boolean  isValidTarget(Object cell){
    return false;
  }

}
