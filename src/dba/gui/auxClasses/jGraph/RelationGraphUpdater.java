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
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import dba.utils.ImageSize;
import dbaCore.data.Attribute;
import dbaCore.data.ForeignKeyConstraint;
import dbaCore.data.RelationSchema;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Updates / Draws a given RelationView-graph
 */
public class RelationGraphUpdater extends RelationUpdater {

  private mxGraph graph;
  private mxGraphComponent graphComponent;
  private Object parentPane;
  private ArrayList<RelationSchema> dbRelations;
  private ArrayList<ForeignKeyConstraint> foreignKeys;

  public RelationGraphUpdater(mxGraph graph, mxGraphComponent graphComponent, ArrayList<RelationSchema> relations,
                              ArrayList<ForeignKeyConstraint> foreignKeys) {
    super();
    dbRelations = relations;
    this.foreignKeys = foreignKeys;
    parentPane = graph.getDefaultParent();
    this.graphComponent = graphComponent;
    this.graph = graph;

    graph.setAutoOrigin(true);
    graph.setAutoSizeCells(true);
  }

  public void run() {
    graph.getModel().beginUpdate();
    try {
      display();
    } finally {
      graph.getModel().endUpdate();
    }
  }

  /**
   * Removes all Cells from the graph
   */
  private void removeAllRelations() {
    graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
  }

  /**
   * Returns a ArrayList containing all relations that participate in
   * a foreignKey-constraint
   *
   * @return ArrayList of relations involved in FK-Constraint
   */
  private ArrayList<String> getFkInvolvedRelations() {
    ArrayList<String> involvedRelations = new ArrayList<>();
    for (ForeignKeyConstraint fk : foreignKeys) {
      if (!involvedRelations.contains(fk.getSourceRelationName())) {
        involvedRelations.add(fk.getSourceRelationName());
      }
      if (!involvedRelations.contains(fk.getTargetRelationName())) {
        involvedRelations.add(fk.getTargetRelationName());
      }
    }

    return involvedRelations;
  }

  /**
   * Returns the y-coordinate of the outermost south cell
   *
   * @return the lowest y-coordinate of the graph
   */
  private int getLowestCellPoint() {
    int lowestCellPoint = 0;
    int cellPoint;

    mxCell cell;
    for (Object obj : graph.getChildVertices(graph.getDefaultParent())) {
      cell = (mxCell) obj;
      cellPoint = cell.getGeometry().getPoint().y + (int) cell.getGeometry().getHeight();
      if (cellPoint > lowestCellPoint) {
        lowestCellPoint = cellPoint;
      }
    }

    return lowestCellPoint;
  }

  /**
   * Displays all given relations
   */
  private void display() {
    // offset of 40 to compensate Header
    ArrayList<String> fkRelations = getFkInvolvedRelations();
    int offset = 40;
    mxCell relationCell;

    removeAllRelations();

    // add relations that occur in a foreignKey constraint
    for (RelationSchema relation : dbRelations) {
      if (fkRelations.contains(relation.getName())) {
        relationCell = (mxCell) insertRelation(graph, relation, offset);
        offset += relationCell.getGeometry().getHeight() + 20;
      }
    }

    // add foreign key Edges
    insertForeignKeyEdges();

    // update Layout in order to display fk-constraints nice
    updateLayout(graphComponent);

    // add the rest of the relations which don't occur in a foreignKey
    // constraint
    int lowestPoint= getLowestCellPoint();
    offset = lowestPoint != 0 ? lowestPoint + 25 : 0;
    for (RelationSchema relation : dbRelations) {
      if (!fkRelations.contains(relation.getName())) {
        relationCell = (mxCell) insertRelation(graph, relation, offset);
        offset += relationCell.getGeometry().getHeight() + 20;
      }
    }

  }

  /**
   * Inserts a given Relation in a graph
   *
   * @param graph    the graph as target for insertion
   * @param relation the relation that should be inserted
   * @param offset   the vertical ofset of the relation
   * @return the mxCell representing the Relation
   */
  private Object insertRelation(mxGraph graph, RelationSchema relation, int offset) {
    // Compensate big header
    int attributeOffset = 40;
    int width = relation.getName().length() * 15;
    ImageSize optimalImageSize =getImageSize(relation);

    mxCell relationVertex = (mxCell) graph.insertVertex(parentPane, relation.getName(), relation, 0, offset, width,
      40 + 1 + relation.getAttributes().size() * 25, "RELATION");

    double maxWidth = width;

    // Add attributes
    mxGeometry geo;
    for (Attribute attr : relation.getAttributes()) {
      mxCell attributeCell = (mxCell)graph.insertVertex(relationVertex, attr.getName(), attr, 1, attributeOffset, width - 2, 25,
        getAttributeStyle(attr,optimalImageSize));

      graph.updateCellSize(attributeCell);
      geo=attributeCell.getGeometry();

      if(geo.getWidth()>maxWidth){
        maxWidth=geo.getWidth();
      }
      attributeOffset += 25;
    }

    maxWidth += 5;
    geo=relationVertex.getGeometry();
    geo.setWidth(maxWidth);

    for(Object child : graph.getChildVertices(relationVertex)){
      if(child instanceof mxCell){
        mxCell cell = (mxCell)child;
        geo = cell.getGeometry();
        geo.setWidth(maxWidth-2);
        geo.setHeight(25);
      }
    }

    return relationVertex;
  }

  protected ImageSize getImageSize(RelationSchema relation){
    int pkANDfk=0;
    int pkORfk=0;

    for(Attribute attribute : relation.getAttributes()){
      if(attribute.getIsPrimaryKey() && attribute.getIsForeignKey()) pkANDfk++;
      else if(attribute.getIsPrimaryKey() || attribute.getIsForeignKey()) pkORfk++;
    }

    if(pkANDfk>0)return ImageSize.BIG;
    else if(pkORfk>0)return ImageSize.SMALL;
    else return ImageSize.NO;
  }



  /**
   * Resets the actual Layout of the graph
   *
   * @param graphComponent component
   */
  private void updateLayout(mxGraphComponent graphComponent) {
    HierarchicalRelationLayout layout = new HierarchicalRelationLayout(graph, SwingConstants.WEST);
    layout.setDisableEdgeStyle(false); //Use the specified EdgeStyle
    Object cell = graphComponent.getGraph().getDefaultParent();
    layout.execute(cell);
  }

  /**
   * Inserts all Relations that participate in a FK-Constraint
   */
  private void insertForeignKeyEdges() {
    ArrayList<mxCell> fkCells;

    if (foreignKeys == null) {
      return;
    }

    for (ForeignKeyConstraint fk : foreignKeys) {
      // Relations
      fkCells = findFkRelationCells(fk);

      insertFkEdge(fkCells, false);

      // Attributes
      fkCells = findFkAttributeCells(fk, fkCells);
      insertFkEdge(fkCells, true);

    }
  }

  /**
   * Inserts a edge between given cells
   *
   * @param fkCells the cells to connect by a edge
   * @param visible determines if the edge should be visible
   */
  private void insertFkEdge(ArrayList<mxCell> fkCells, boolean visible) {

    if (fkCells.size() == 2) {
      if (visible) {
        mxCell cell = (mxCell)graph.insertEdge(parentPane, null, fkCells.get(0).getValue(), fkCells.get(0), fkCells.get(1), "FK_ARROW");
        handleSelfReference(cell,fkCells.get(0),fkCells.get(1));
      } else {
        graph.insertEdge(parentPane, null, "", fkCells.get(0), fkCells.get(1), "INVISIBLE_EDGE");
      }
    }
  }

  /**
   * Moves a Edge that references the same Relation outside of the Relation-Area
   * @param edge the ForeignKey-mxCell to work with
   * @param firstCell  the first cell to work with
   * @param secondCell the second cell to work with
   */
  private void handleSelfReference(mxCell edge,mxCell firstCell,mxCell secondCell){
    if(firstCell.getParent() == secondCell.getParent()){
      mxGeometry edgeGeo = graph.getModel().getGeometry(edge);
      List<mxPoint> points = edgeGeo.getPoints();
      if(points==null) points=new ArrayList<>();

      points.add(new mxPoint(edgeGeo.getX()+graph.getModel().getGeometry(firstCell).getWidth()+20,
        graph.getModel().getGeometry(firstCell).getCenterY()));

      points.add(new mxPoint(edgeGeo.getX()+graph.getModel().getGeometry(firstCell).getWidth()+20,
        graph.getModel().getGeometry(secondCell).getCenterY()));

      edgeGeo.setPoints(points);
      graph.getModel().setGeometry(edge,edgeGeo);
    }
  }

  /**
   * Returns the Attribute-Cells of the given name
   *
   * @param fk            the ForeignKeyConstraint with the name of the values
   * @param relationCells the RelationCells containing the Attributes
   * @return a ArrayList containing a Source- and a TargetAttribute
   */
  private ArrayList<mxCell> findFkAttributeCells(ForeignKeyConstraint fk, ArrayList<mxCell> relationCells) {
    ArrayList<mxCell> fkCells = new ArrayList<>();
    if (relationCells.size() == 2) {
      mxCell sourceCell;
      mxCell targetCell;

      sourceCell = getAttributeCell(relationCells.get(0), fk.getSourceAttributeName());
      targetCell = getAttributeCell(relationCells.get(1), fk.getTargetAttributeName());

      if (sourceCell != null) {
        fkCells.add(sourceCell);
      }
      if (targetCell != null) {
        fkCells.add(targetCell);
      }

    }
    return fkCells;
  }

  /**
   * Returns a Attribute of a Relation by name
   *
   * @param relationCell  the parent - RelationCell
   * @param attributeName the name of the Attribute to look for
   * @return the cell representing the given attribute
   */
  private mxCell getAttributeCell(mxCell relationCell, String attributeName) {
    mxCell resultCell = null;
    mxCell cell;

    for (int index = 0; index < relationCell.getChildCount(); index++) {
      cell = (mxCell) relationCell.getChildAt(index);
      if (cell.getValue() instanceof Attribute) {
        if (((Attribute) cell.getValue()).getName().equals(attributeName)) {
          resultCell = cell;
        }
      }
    }

    return resultCell;
  }

  /**
   * Returns the Relation-Cells of the given name
   *
   * @param fk the fk with the names of the relations
   * @return A ArrayList containing the two relation of the FK
   */
  private ArrayList<mxCell> findFkRelationCells(ForeignKeyConstraint fk) {
    ArrayList<mxCell> fkCells = new ArrayList<>();
    String relationName;
    mxCell cell;
    mxCell sourceCell = null;
    mxCell targetCell = null;

    for (Object obj : graph.getChildVertices(graph.getDefaultParent())) {
      if (sourceCell != null && targetCell != null) {
        break;
      }
      cell = (mxCell) obj;
      if (cell.getValue() instanceof RelationSchema) {
        relationName = ((RelationSchema) cell.getValue()).getName();

        if (relationName.equals(fk.getSourceRelationName())) {
          sourceCell = cell;
        }
        if (relationName.equals(fk.getTargetRelationName())) {
            targetCell = cell;
        }
      }
    }

    if (sourceCell != null) {
      fkCells.add(sourceCell);
    }
    if (targetCell != null) {
      fkCells.add(targetCell);
    }

    return fkCells;
  }
}
