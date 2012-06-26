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
import data.Attribute;
import data.FunctionalDependency;
import data.RelationSchema;

import java.util.ArrayList;

/**
 * Updates / Draws the RelationDetailsView
 */
public class RelationDetailsGraphUpdater implements Runnable {

  private mxGraph graph;
  private Object parentPane;
  private ArrayList<RelationSchema> dbRelations;

  public RelationDetailsGraphUpdater(mxGraph graph,
                                     ArrayList<RelationSchema> relations) {
    dbRelations = relations;
    parentPane = graph.getDefaultParent();
    this.graph = graph;

  }

  @Override
  public void run() {
    graph.getModel().beginUpdate();
    try {
      display();
    } finally {
      graph.getModel().endUpdate();
    }

  }

  /**
   * Removes all cells from the Graph
   */
  private void removeAllRelations() {
    graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
  }

  /**
   * Displays all relations with their functional dependencies
   */
  public void display() {
    int offset = 25;
    mxCell relationCell;

    removeAllRelations();

    for (RelationSchema relation : dbRelations) {
      relationCell = (mxCell) insertRelation(graph, relation, offset);
      offset += relationCell.getGeometry().getHeight()
              + (50 + 30 * relation.getFunctionalDependencies().size());

    }
  }

  /**
   * Inserts a given Relation in a graph
   *
   * @param graph          the graph as target for insertion
   * @param relation       the relation that should be inserted
   * @param verticalOffset the vertical offset of the relation
   * @return the mxCell representing the Relation
   */
  private Object insertRelation(mxGraph graph, RelationSchema relation,
                                int verticalOffset) {
    int attributeOffset;
    int horizontalOffset = 0;
    int attributeWidth;
    ArrayList<mxCell> attributeCells = new ArrayList<>();

    mxCell relationVertex = (mxCell) graph.insertVertex(parentPane,
            relation.getName(), relation, horizontalOffset, verticalOffset,
            getRequiredWidth(relation), 25, "RELATION_HEADER");

    attributeOffset = (int) (relationVertex.getGeometry().getY() + 25);

    // Add attributes
    for (Attribute attr : relation.getAttributes()) {
      attributeWidth = getRequiredWidth(attr);
      attributeCells.add((mxCell) graph.insertVertex(parentPane,
              attr.getName(), attr, horizontalOffset, attributeOffset,
              attributeWidth, 25, getAttributeStyle(attr)));
      horizontalOffset += attributeWidth;
    }

    drawFunctionalDependencies(relation.getAttributes(), attributeCells,
            relation.getFunctionalDependencies());

    return relationVertex;
  }

  /**
   * Returns the required width for a AttributeCell
   *
   * @param attribute the attribute to work with
   * @return the required width
   */
  private int getRequiredWidth(Attribute attribute) {
    return 80 + 8 * attribute.getName().length();
  }

  /**
   * Returns the required width for a RelationCell
   *
   * @param relation the relation to work with
   * @return the required width
   */
  private int getRequiredWidth(RelationSchema relation) {
    return 80 + 12 * relation.getName().length();
  }

  /**
   * Gets the right style for the pk/fk constallation
   *
   * @param attribute attribute to get the style for
   * @return a String representing the Style for the attribute
   */
  private String getAttributeStyle(Attribute attribute) {

    if (attribute.getIsPrimaryKey() && attribute.getIsForeignKey()) {
      return "ATTRIBUTE_PKFK";
    } else if (attribute.getIsPrimaryKey()) {
      return "ATTRIBUTE_PK";
    } else if (attribute.getIsForeignKey()) {
      return "ATTRIBUTE_FK";
    } else {
      return "ATTRIBUTE_NOKEY";
    }
  }

  /**
   * draws all given functionalDependencies
   *
   * @param attributes     the attributes of the relation
   * @param attributeCells the mxCells containing the attributes
   * @param fds            the functionalDependencies to display
   */
  private void drawFunctionalDependencies(ArrayList<Attribute> attributes,
                                          ArrayList<mxCell> attributeCells, ArrayList<FunctionalDependency> fds) {
    int offset = 2;

    for (FunctionalDependency fd : fds) {
      drawFunctionalDependency(attributes, attributeCells, fd, offset);
      offset += 25;
    }

  }

  /**
   * draws a single functionalDependency
   *
   * @param attributes     the attributes of the relation
   * @param attributeCells the mxCells containing the attributes
   * @param fd             the functionalDependency to display
   * @param verticalOffset the offset between two functionalDependencies
   */
  private void drawFunctionalDependency(ArrayList<Attribute> attributes,
                                        ArrayList<mxCell> attributeCells, FunctionalDependency fd,
                                        int verticalOffset) {
    // draws all the near nodes
    ArrayList<mxCell> nearNodes = drawNodes(attributeCells, verticalOffset + 2);

    // draws all the far nodes
    ArrayList<mxCell> farNodes = drawNodes(attributeCells, verticalOffset + 20);

    // draws all the arrows between the nodes
    drawAttributeArrows(attributes, fd, nearNodes, farNodes);
  }

  /**
   * Draws a node beneath each Attribute-mxCell (horizontal-Center)
   *
   * @param attributes the cells to draw a node beneath them
   * @param offset     the vertical offset between the cell and the node
   * @return returns a ArrayList containing all inserted nodes
   */
  private ArrayList<mxCell> drawNodes(ArrayList<mxCell> attributes, int offset) {
    ArrayList<mxCell> nodes = new ArrayList<>();

    for (mxCell cell : attributes) {
      nodes.add((mxCell) graph.insertVertex(parentPane, null, null, cell
              .getGeometry().getCenterX(), getCellLowestYPoint(cell) + offset, 1,
              1, "NODE"));
    }

    return nodes;
  }

  /**
   * Returns the lower end of the cell (max y-coordinate)
   *
   * @param cell the cell to work with
   * @return the y-coordinate
   */
  private double getCellLowestYPoint(mxCell cell) {
    return cell.getGeometry().getY() + cell.getGeometry().getHeight();
  }

  /**
   * Draws the arrows between the nodes
   *
   * @param attributes the attributes of the relation
   * @param fd         the functionalDependency to display
   * @param nearNodes  the Array of Nodes which are closer to the
   *                   attribute-Nodes
   * @param farNodes   the Array of Nodes which are further away of the
   *                   attribute-Nodes
   */
  private void drawAttributeArrows(ArrayList<Attribute> attributes,
                                   FunctionalDependency fd, ArrayList<mxCell> nearNodes,
                                   ArrayList<mxCell> farNodes) {
    int index;
    ArrayList<Integer> indices = new ArrayList<>();

    // draw arrows for the SourceAttributes
    for (Attribute sourceAttr : fd.getSourceAttributes()) {
      index = attributes.indexOf(sourceAttr);
      graph.insertEdge(parentPane, null, "", nearNodes.get(index),
              farNodes.get(index), "EDGE_PLAIN");
      indices.add(index);
    }

    // draw arrows for the TargetAttributes
    for (Attribute targetAttr : fd.getTargetAttributes()) {
      index = attributes.indexOf(targetAttr);
      graph.insertEdge(parentPane, null, "", farNodes.get(index),
              nearNodes.get(index), "EDGE_ARROW");
      indices.add(index);
    }

    // Connect arrows
    java.util.Collections.sort(indices);
    graph.insertEdge(parentPane, null, "", farNodes.get(indices.get(0)),
            farNodes.get(indices.get(indices.size() - 1)), "EDGE_PLAIN");
  }
}
