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
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import dba.gui.CustomTree;
import dba.options.Options;
import dba.utils.Localization;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Observer;

public abstract class JGraphView extends JPanel {
  /**
   *
   */
  private static final long serialVersionUID = -8564817921313692339L;
  private ArrayList<Observer> observers;
  protected mxGraph graph;
  protected boolean zoomEnabled;
  protected mxGraphComponent graphComponent;
  protected mxCell selectedCell;
  protected Localization locale;

  protected JGraphView() {
    super();
    observers = new ArrayList<>();
    graph = new mxGraph();
    locale = Localization.getInstance();
    zoomEnabled = true;
  }

  /**
   * Initializes the Listeners for the graph and the graphcomponent
   */
  protected void initListeners() {

    //Manages the click on the graphComponent when it has the focus
    graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {

      @Override
      public void invoke(Object arg0, mxEventObject arg1) {
        updateSelectedCell();
      }
    });

    //Manages the clicks on the graphComponent when it hasn't the focus
    graphComponent.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent focusEvent) {
        updateSelectedCell();
      }

      @Override
      public void focusLost(FocusEvent focusEvent) {
      }
    });

    //Notice when user zooms via the outline-tab
    graph.getView().addListener("scale", new mxEventSource.mxIEventListener() {
      @Override
      public void invoke(Object sender, mxEventObject evt) {
        notifyObservers();
      }
    });

    //Uses Mouswheel+Control to zoom zoom zoom
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
          if (scale > 0.15) {
            graphComponent.zoomOut();
          }
        }

        notifyObservers();

      }
    });
  }

  /**
   * Returns the mxGraphComponent used in this View
   *
   * @return the currently used mxGraphComponent
   */
  public mxGraphComponent getGraphComponent() {
    return graphComponent;
  }

  /**
   * Returns all available zoomFactors
   *
   * @return a array containing the available zoomfactors
   */
  public String[] getZoomFactors() {
    ArrayList<String> factors = new ArrayList<>();
    String[] fixedFactors = new String[]{"25%", "50%", "75%", "100%", "125%", "150%", locale.getString("Width"),
      locale.getString("Page")};

    factors.add((int) (graph.getView().getScale() * 100) + "%");
    factors.addAll(Arrays.asList(fixedFactors));

    return factors.toArray(new String[factors.size()]);
  }


  public void setZoomEnabled(boolean zoomEnabled) {
    this.zoomEnabled = zoomEnabled;
  }

  /**
   * Zooms to the given percentage
   *
   * @param factor the zoomFactor e.g. 100, 50%
   */
  public void zoom(String factor) {
    if (zoomEnabled) {
      if (factor.equals(locale.getString("Width"))) {
        fitWidth();
      } else if (factor.equals(locale.getString("Page"))) {
        fitPage();
      } else {
        setZoomFactor(Double.parseDouble(factor.replace("%", "")));
      }
    }
  }

  /**
   * Changes the ZoomFactor in order to display all horizontal content
   */
  private void fitWidth() {
    double difference = (graphComponent.getWidth() - 25) / graph.getGraphBounds().getWidth();
    setZoomFactor((graph.getView().getScale() * 100) * difference);
  }

  /**
   * Changes the ZoomFactor in order to display all horizonal and vertical content
   */
  private void fitPage() {
    double horizontalDelta = (graphComponent.getWidth() - 25) / graph.getGraphBounds().getWidth();
    double verticalDelta = (graphComponent.getHeight() - 25) / graph.getGraphBounds().getHeight();

    double delta = horizontalDelta < verticalDelta ? horizontalDelta : verticalDelta;

    setZoomFactor((graph.getView().getScale() * 100) * delta);
  }

  /**
   * Changes the zoomFactor of the GraphComponent
   *
   * @param factor the zoomFactor
   */
  private void setZoomFactor(Double factor) {
    if (factor != null) {
      factor /= 100;
      if (factor != graph.getView().getScale()) {
        graphComponent.zoomTo(factor, false);
        notifyObservers();
      }
    }
  }

  /**
   * Updates the selected Item of the Tree when the user click on a Cell
   */
  private void updateSelectedCell() {
    selectedCell = (mxCell) graph.getSelectionCell();

    if (selectedCell != null) {
      CustomTree.getInstance().setSelectedNode(selectedCell.getValue());
    } else {
      CustomTree.getInstance().setSelectedItem(0);
    }
  }

  /**
   * Exports the graph to a .png-file
   *
   * @param path the target-location for the .png-file
   * @param name the name of the .png-file
   */
  public void exportToPng(String path, String name) {
    Dimension d = graphComponent.getGraphControl().getSize();
    BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    graphComponent.getGraphControl().paint(g);
    final File outputfile = new File(path.replace(".png", name + ".png"));
    try {
      ImageIO.write(image, "png", outputfile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Defines Stylesheets for the Attributes
   */
  protected void initStyle() {
    mxStylesheet stylesheet = graph.getStylesheet();

    // Define Style for Attribute with no Image
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
    style.put(mxConstants.STYLE_STROKEWIDTH, 1);
    style.put(mxConstants.STYLE_IMAGE_HEIGHT, 22);
    stylesheet.putCellStyle("ATTRIBUTE_NOIMAGE", style);

    // Define Style for Primary-Key Attribute represented by a small "space"-image
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_SPACING_LEFT, 21); //16 + 5 Pixel space
    stylesheet.putCellStyle("ATTRIBUTE_SPACE_SMALL", style);

    // Define Style for Primary-Key Attribute represented by a big "space"-image
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_SPACING_LEFT, 38);    //33 + 5 Pixel space
    stylesheet.putCellStyle("ATTRIBUTE_SPACE_BIG", style);

    // Define Style for Primary-Key/Foreign-Key Attribute
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE_WIDTH, 33);
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pkfk.png");
    stylesheet.putCellStyle("ATTRIBUTE_PKFK", style);

    // Define Style for Primary-Key Attribute represented by a big image
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE_WIDTH, 16);
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pk.png");
    stylesheet.putCellStyle("ATTRIBUTE_PK_BIG", style);

    // Define Style for Foreign-Key Attribute represented by a big image
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_fk.png");
    stylesheet.putCellStyle("ATTRIBUTE_FK_BIG", style);

    // Define Style for Primary-Key Attribute represented by a small image
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_SPACING_LEFT, 21); //16 + 5 Pixel space
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pk.png");
    stylesheet.putCellStyle("ATTRIBUTE_PK_SMALL", style);

    // Define Style for Foreign-KEy Attribute represented by a small image
    style = (Hashtable<String, Object>) style.clone();
    style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_fk.png");
    stylesheet.putCellStyle("ATTRIBUTE_FK_SMALL", style);

  }


  // Observer methods

  /**
   * Add a Observer to the Collection
   *
   * @param observer the observer to add
   * @return true for success
   */
  public boolean addObserver(Observer observer) {
    return observers.add(observer);
  }

  /**
   * Notifies Observers about change
   */
  protected void notifyObservers() {
    for (Observer stalker : observers) {
      stalker.update(null, this);
    }
  }
}
