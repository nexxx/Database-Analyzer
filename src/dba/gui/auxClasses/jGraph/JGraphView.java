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

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Observer;

public abstract class JGraphView extends JPanel {
  /**
   *
   */
  private static final long serialVersionUID = -8564817921313692339L;
  private ArrayList<Observer> observers;
  private mxGraph graph;
  protected boolean zoomEnabled;
  protected mxGraphComponent graphComponent;

  protected JGraphView() {
    super();
    observers = new ArrayList<>();
    graph = new mxGraph();
    zoomEnabled=true;
  }

  protected void setGraph(mxGraph graph) {
    this.graph = graph;
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

  public boolean isZoomEnabled() {
    return zoomEnabled;
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
    if(isZoomEnabled()){
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
