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

package dbaCore.data;

import dbaCore.data.events.Change;
import dbaCore.data.events.ChangeListener;
import dbaCore.data.events.Time;

import java.util.ArrayList;

/**
 * Class representing a Undo/Redo History
 *
 * @author Sebastian Theuermann
 */
public final class History extends HistoricObject {
  private ArrayList<HistoricObject> history;
  private HistoricObject currentElement;

  public History() {
    super();
    history = new ArrayList<>();
  }

  public History(HistoricObject historicElement) {
    this();
    initialize(historicElement);
  }

  /**
   * (Re-)Initializes the History
   *
   * @param currentElement the Database that is current in use
   */
  public void initialize(HistoricObject currentElement) {
    history.clear();
    this.currentElement = currentElement;
    // disconnect redundant listener
    disconnectChangeListener();

    history.add(currentElement);

    super.changeListener = new ChangeListener() {
      @Override
      public void Change(Change change) {
        switch (change.getTime()) {
          case BEFORECHANGE:
            purgeFuture();
            makeHistory();
            notifyOfCurrentElementChange();
            break;

          case AFTERCHANGE:
            changeSupport.fireChange(change.getTime());
            break;
        }
      }
    };

    connectChangeListener();
  }


  /**
   * Moves forward in history
   */
  public boolean travelForward() {
    boolean success = false;
    disconnectChangeListener();

    if (getForwardPossible()) {
      currentElement = history.get(history.indexOf(currentElement) + 1);
      success = true;
    }

    connectChangeListener();
    notifyOfCurrentElementChange();
    return success;
  }

  /**
   * Moves backward in history
   */
  public boolean travelBackward() {
    boolean success = false;
    disconnectChangeListener();

    if (getBackwardPossible()) {
      currentElement = history.get(history.indexOf(currentElement) - 1);
      success = true;
    }

    connectChangeListener();
    notifyOfCurrentElementChange();
    return success;
  }

  /**
   * Adds a PropertyChangedListener to the currentDatabase
   */
  private void connectChangeListener() {
    currentElement.addChangeListener(changeListener);
  }

  /**
   * Removes the PropertyChangedListener from the currentDatabase
   */
  private void disconnectChangeListener() {
    currentElement.removeChangeListener(changeListener);
  }

  /**
   * Removes all future-Versions of the currentDatabase
   */
  private void purgeFuture() {
    int futureDbCount = history.size() - 1 - history.indexOf(currentElement);
    for (int i = 0; i < futureDbCount; i++) {
      history.remove(history.size() - 1);
    }
  }

  /**
   * Adds the current Database to the history
   */
  private void makeHistory() {
    makeHistory((HistoricObject) currentElement.getClone());
  }

  /**
   * Adds a Database to the history
   *
   * @param oldElement the old element to add to the history
   */
  private void makeHistory(HistoricObject oldElement) {
    history.set(history.indexOf(currentElement), oldElement);
    history.add(currentElement);
    setDirty(true);
  }

  /**
   * Returns if you can navigate backward in history
   *
   * @return true if backward is possible, false if not
   */
  public boolean getBackwardPossible() {
    return !history.isEmpty() && history.indexOf(currentElement) != 0;
  }

  /**
   * Returns if you can navigate forward in history
   *
   * @return true ==> forward possible, false ==> forward impossible
   */
  public boolean getForwardPossible() {
    return !history.isEmpty() && (history.indexOf(currentElement) < (history.size() - 1));
  }

  /**
   * Adds a Element at the current index and erases all elements past
   * that index
   *
   * @param newObject the element to add to the History
   */
  public void addHistoricObject(HistoricObject newObject) {
    purgeFuture();
    makeHistory((HistoricObject) currentElement.getClone());

    // Replace currentElement with new one
    history.remove(currentElement);
    currentElement = newObject;
    history.add(currentElement);
    connectChangeListener();

    notifyOfCurrentElementChange();
  }

  /**
   * Returns the Database that is currently active
   *
   * @return the current Element
   */
  public HistoricObject getCurrentElement() {
    return currentElement;
  }

  /**
   * Returns the whole History
   *
   * @return the whole History
   */
  public ArrayList<HistoricObject> getHistory() {
    return history;
  }

  private void notifyOfCurrentElementChange() {
    // Notify listeners that currentDatabase has changed
    changeSupport.fireChange(Time.AFTERCHANGE);
  }

  @Override
  public Object getClone() {
    return null;
  }
}
