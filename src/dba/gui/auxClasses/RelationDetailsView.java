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
import dba.gui.auxClasses.jGraph.RelationDetailsGraphUpdater;
import dba.gui.auxClasses.jGraph.XGraph;
import dba.options.Options;
import dbaCore.data.Database;
import dbaCore.data.RelationSchema;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Displays the relations and their functional dependencies
 */
public class RelationDetailsView extends JGraphView{
  /**
   *
   */
  private static final long serialVersionUID = -6169070587706507110L;

  public RelationDetailsView() {
    super();
    initGraphics();
  }

  /**
   * Creates a new instance and registers this instance as a observer
   * of given GuiLogic
   *
   * @param logic the GUiLogic to observe
   */
  public RelationDetailsView(GuiLogic logic) {
    super();

    logic.addGraphicalExportRequestedListener(new GraphicalExportRequestedListener() {
      @Override
      public void GraphicalExportRequested(GraphicalExportRequested exportRequest) {
        exportToPng(exportRequest.getPath(), "_export_fd");
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
    display(database.getDatabase());
  }

  /**
   * Displays all given Relations
   */
  public void display(ArrayList<RelationSchema> relations) {

    RelationDetailsGraphUpdater updater = new RelationDetailsGraphUpdater(graph, relations);
    updater.run();
  }


  /**
   * Adds the Styles for Attributes and Relations
   */
  @SuppressWarnings("unchecked")
  private void initStyle() {
    mxStylesheet stylesheet = graph.getStylesheet();

    // Define Style for Attribute with no Key
    Hashtable<String, Object> style = new Hashtable<>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_LABEL);
    style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
    style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
    style.put(mxConstants.STYLE_AUTOSIZE, true);
    style.put(mxConstants.STYLE_FILLCOLOR, Options.getInstance().getAttributeColor());
    style.put(mxConstants.STYLE_FONTCOLOR, Options.getInstance().getFontColor());
    style.put(mxConstants.STYLE_FONTSIZE, 15);
    style.put(mxConstants.STYLE_MOVABLE, false);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_IMAGE_HEIGHT, 32);
    style.put(mxConstants.STYLE_IMAGE_WIDTH, 32);
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_nokey.png");
    style.put(mxConstants.STYLE_STROKEWIDTH, 1);

    stylesheet.putCellStyle("ATTRIBUTE_NOKEY", style);

    // Define Style for Primary-Key Attribute
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pk.png");
    stylesheet.putCellStyle("ATTRIBUTE_PK", style);

    // Define Style for Foreign-Key Attribute
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_fk.png");
    stylesheet.putCellStyle("ATTRIBUTE_FK", style);

    // Define Style for Primary-Key/Foreign-Key Attribute
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pkfk.png");
    stylesheet.putCellStyle("ATTRIBUTE_PKFK", style);

    // Define Style for Relation-Header
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_OPACITY, 0);
    style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
    style.put(mxConstants.STYLE_FONTCOLOR, Options.getInstance().getFontColor());
    style.put(mxConstants.STYLE_MOVABLE, false);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_FONTSIZE, 20);
    style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
    stylesheet.putCellStyle("RELATION_HEADER", style);

    // Define Style for FD-Nodes
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_MOVABLE, false);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_OPACITY, 0);
    stylesheet.putCellStyle("NODE", style);

    // Define Invisibility-Style for Edges
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_MOVABLE, false);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_STROKEWIDTH, 2);
    style.put(mxConstants.STYLE_STROKECOLOR, Options.getInstance().getArrowFDColor());
    stylesheet.putCellStyle("EDGE_ARROW", style);

    // Define Style for plain Edges
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
    stylesheet.putCellStyle("EDGE_PLAIN", style);
  }
}
