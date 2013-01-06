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

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: theuers
 * Date: 12/11/12
 * Time: 3:06 PM
 */
public class HierarchicalRelationLayout extends mxHierarchicalLayout {
  /**
   * Constructs a hierarchical layout that ignores every Cell inside Relation-Cells
   *
   * @param graph       the graph to lay out
   * @param orientation <code>SwingConstants.NORTH, SwingConstants.EAST, SwingConstants.SOUTH</code> or <code>
   *                    SwingConstants.WEST
  </code>
   */
  public HierarchicalRelationLayout(mxGraph graph, int orientation) {
    super(graph, orientation);
  }

  /**
   * Creates a set of descendant cells, Attribute-Cells get ignored
   *
   * @param cell The cell whose descendants are to be calculated
   * @return the descendants of the cell (not the cell)
   */
  public Set<Object> filterDescendants(Object cell) {
    mxIGraphModel model = graph.getModel();
    Set<Object> result = new LinkedHashSet<>();

    if (model.isVertex(cell) && cell != this.parent && model.isVisible(cell)) {
      result.add(cell);
    }

    if (((mxCell) cell).getStyle() != "RELATION" && (this.traverseAncestors || cell == this.parent && model.isVisible
      (cell))) {
      int childCount = model.getChildCount(cell);

      for (int i = 0; i < childCount; i++) {
        Object child = model.getChildAt(cell, i);
        result.addAll(filterDescendants(child));
      }
    }

    return result;
  }

}
