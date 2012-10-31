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
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import dba.gui.CustomTree;

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

  protected JGraphView() {
    super();
    observers = new ArrayList<>();
    graph = new mxGraph();
    zoomEnabled=true;
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
          if (scale > 0.1) {
            graphComponent.zoomOut();
          }
        }

        notifyObservers();

      }
    });
  }

  /**
   * Returns all available zoomFactors
   *
   * @return a array containing the available zoomfactors
   */
  public String[] getZoomFactors() {
    ArrayList<String> factors = new ArrayList<>();
    String[] fixedFactors = new String[]{"25%", "50%", "75%", "100%", "125%", "150%"};

    factors.add((int) (graph.getView().getScale() * 100) + "%");

    for (String factor : fixedFactors) {
      if (!factors.contains(factor)) {
        factors.add(factor);
      }
    }

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
    if(zoomEnabled){
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
  }

  /**
   * Updates the selected Item of the Tree when the user click on a Cell
   */
  private void updateSelectedCell(){
    selectedCell = (mxCell) graph.getSelectionCell();

    if (selectedCell != null) {
      CustomTree.getInstance().setSelectedNode(selectedCell.getValue());
    } else {
      CustomTree.getInstance().setSelectedItem(0);
    }
  }

  /**
   * Exports the graph to a .png-file
   * @param path the target-location for the .png-file
   * @param name the name of the .png-file
   */
  public void exportToPng(String path,String name) {
    Dimension d = graphComponent.getGraphControl().getSize();
    BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    graphComponent.getGraphControl().paint(g);
    final File outputfile = new File(path.replace(".png", name+".png"));
    try {
      ImageIO.write(image, "png", outputfile);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
