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

package dba.gui.auxClasses;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import dba.gui.auxClasses.events.GraphicalExportRequested;
import dba.gui.auxClasses.events.GraphicalExportRequestedListener;
import dba.gui.auxClasses.jGraph.JGraphView;
import dba.gui.auxClasses.jGraph.RelationGraphUpdater;
import dba.gui.auxClasses.jGraph.XGraph;
import dba.options.Options;
import dbaCore.data.Database;
import dbaCore.data.ForeignKeyConstraint;
import dbaCore.data.RelationSchema;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class RelationView extends JGraphView {
  /**
   *
   */
  private static final long serialVersionUID = -6169070587706507110L;

  // private Thread displayThread;

  public RelationView() {
    super();
    initGraphics();
  }

  /**
   * Creates a new instance and registers this instance as a observer
   * of given GuiLogic
   *
   * @param logic the GUiLogic to observe
   */
  public RelationView(GuiLogic logic) {
    super();

    //logic.addObserver(this);
    logic.addGraphicalExportRequestedListener(new GraphicalExportRequestedListener() {
      @Override
      public void GraphicalExportRequested(GraphicalExportRequested exportRequest) {
        exportToPng(exportRequest.getPath(), "_export");
      }
    });
    initGraphics();
  }

  private void initGraphics() {
    setLayout(new BorderLayout());

    graph = new XGraph();

    graph.getModel().beginUpdate();
    try {
      // change Style
      initStyle();
    } finally {
      graph.getModel().endUpdate();
    }

    // Configure Graph
    graph.setCellsEditable(false);
    graph.setDropEnabled(false);
    graph.setAllowDanglingEdges(false);

    graphComponent = new mxGraphComponent(graph);
    super.initListeners();
    graphComponent.getViewport().setBackground(Color.decode(Options.getInstance().getBackgroundColor()));

    // Disable the user's ability to draw own connections
    graphComponent.setConnectable(false);


    add(graphComponent, BorderLayout.CENTER);

  }

  /**
   * Displays all relations of the given database
   *
   * @param database the database to display
   */
  public void display(Database database) {
    display(database.getDatabase(), database.getForeignKeys());
  }

  /**
   * Displays all given Relations with ForeignKeys
   *
   * @param relations Arraylist containing all relations
   */
  public void display(ArrayList<RelationSchema> relations, ArrayList<ForeignKeyConstraint> foreignKeys) {

    RelationGraphUpdater updater = new RelationGraphUpdater(graph, graphComponent, relations, foreignKeys);
    updater.run();
  }

  /**
   * Adds the Styles for Relations and Edges
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void initStyle() {
    super.initStyle();

    mxStylesheet stylesheet = graph.getStylesheet();

    // Define Style for Relations
    Hashtable<String, Object> style = new Hashtable<>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
    style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
    style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
    style.put(mxConstants.STYLE_FONTCOLOR, Options.getInstance().getFontColor());
    style.put(mxConstants.STYLE_FONTSIZE, 20);
    style.put(mxConstants.STYLE_FILLCOLOR, Options.getInstance().getRelationColor());
    style.put(mxConstants.STYLE_STROKECOLOR, Options.getInstance().getBorderColor());
    style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_STROKEWIDTH, 2);

    stylesheet.putCellStyle("RELATION", style);

    // Define Invisibility-Style for Edges
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_OPACITY, 0);
    style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
    stylesheet.putCellStyle("INVISIBLE_EDGE", style);

    // Define Invisibility-Style for Edges
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_MOVABLE, false);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_STROKECOLOR, Options.getInstance().getArrowFKColor());
    style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ELBOW);
    stylesheet.putCellStyle("FK_ARROW", style);
  }
}

