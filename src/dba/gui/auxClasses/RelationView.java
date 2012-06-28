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

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import data.Database;
import data.ForeignKeyConstraint;
import data.RelationSchema;
import dba.gui.auxClasses.jGraph.JGraphView;
import dba.gui.auxClasses.jGraph.RelationGraphUpdater;
import dba.gui.auxClasses.jGraph.XGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

public class RelationView extends JGraphView implements Observer {
  /**
   *
   */
  private static final long serialVersionUID = -6169070587706507110L;
  // constants
  private final static String ATTRIBUTECOLOR = "#00FF00";
  private final static String RELATIONCOLOR = "#00CD00";
  private final static String BACKGROUNDCOLOR = "#A7E2FF";

  private mxCell selectedCell;
  private mxGraph graph;
  private mxGraphComponent graphComponent;

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

    logic.addObserver(this);
    initGraphics();
  }

  private void initGraphics() {
    setLayout(new BorderLayout());

    graph = new XGraph();
    super.setGraph(graph);

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

    graphComponent = new mxGraphComponent(graph);
    graphComponent.getViewport().setBackground(Color.decode(BACKGROUNDCOLOR));

    // Disable the user's ability to draw own connections
    graphComponent.setConnectable(false);

    graphComponent.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent event) {
        double scale = graph.getView().getScale();

        // Only scroll when Control is pressed
        if (!event.isControlDown()) {
          return;
        }

        if (event.getWheelRotation() < 0) {
          if (scale < 20) {
            graphComponent.zoomIn();
          }
        } else {
          if (scale > 0.1) {
            graphComponent.zoomOut();
          }
        }

        notifyObservers();

      }
    });

    graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener() {

      @Override
      public void invoke(Object arg0, mxEventObject arg1) {
        selectedCell = (mxCell) graph.getSelectionCell();

        if (selectedCell != null) {
          CustomTree.getInstance().setSelectedNode(selectedCell.getValue());
        } else {
          CustomTree.getInstance().setSelectedItem(0);
        }
      }
    });

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
   * Zooms to the given percentage
   *
   * @param factor the zoomFactor e.g. 100, 50%
   */
  public void zoom(String factor) {
    factor = factor.replace("%", "");

    Double newScale = Double.parseDouble(factor);
    if (newScale != null) {
      newScale /= 100;
      if (newScale != graph.getView().getScale()) {
        graphComponent.zoomTo(newScale, false);
        notifyObservers();
      }

    }
  }

  /**
   * Exports the current graph to Png
   *
   * @param path the path + the fileName
   */
  private void exportToPng(String path) {
    Dimension d = graphComponent.getGraphControl().getSize();
    BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    graphComponent.getGraphControl().paint(g);
    final File outputfile = new File(path.replace(".png", "_export.png"));
    try {
      ImageIO.write(image, "png", outputfile);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
    style.put(mxConstants.STYLE_FILLCOLOR, ATTRIBUTECOLOR);
    style.put(mxConstants.STYLE_FONTCOLOR, "black");
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

    // Define Style for Relations
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
    style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
    style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
    // style.put(mxConstants.STYLE_AUTOSIZE, true);
    style.put(mxConstants.STYLE_FONTCOLOR, "black");
    style.put(mxConstants.STYLE_FONTSIZE, 20);
    style.put(mxConstants.STYLE_FILLCOLOR, RELATIONCOLOR);
    style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
    style.put(mxConstants.STYLE_EDITABLE, false);
    style.put(mxConstants.STYLE_RESIZABLE, false);
    style.put(mxConstants.STYLE_STROKEWIDTH, 2);

    stylesheet.putCellStyle("RELATION", style);

    // Define Invisibility-Style for Edges
    style = new Hashtable<>();
    style.put(mxConstants.STYLE_OPACITY, 0);
    stylesheet.putCellStyle("INVISIBLE_EDGE", style);
  }

  // Observer methods
  @Override
  public void update(Observable o, Object arg) {
    if (arg instanceof String) {
      exportToPng((String) arg);
    }
  }
}
