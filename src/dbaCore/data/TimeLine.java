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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Class representing the Undo/Redo History
 *
 * @author Sebastian Theuermann
 */
public final class TimeLine extends HistoricObject {
  private static TimeLine instance;
  private static History history;
  private PropertyChangeSupport changes = new PropertyChangeSupport( this );

  private TimeLine() {
    super();
    history = new History();

    super.changeListener = new ChangeListener() {
      @Override
      public void Change(Change change) {
        changeSupport.fireChange(change.getTime());
      }
    };
  }

  /**
   * Notifies Observers about change
   */
  public void notifyAboutChange() {
    changeSupport.fireChange(Time.AFTERCHANGE);
  }

  /**
   * Getter for the singleton options (thread-save)
   */
  public synchronized static TimeLine getInstance() {
    if (instance == null) {
      synchronized (TimeLine.class) {
        instance = new TimeLine();
      }
    }
    return instance;
  }

  /**
   * Resets the History and makes the given Element the first in the
   * History
   *
   * @param currentElement the first Element of the new History
   */
  public void initialize(HistoricObject currentElement) {
    history.removeChangeListener(super.changeListener);
    history.initialize(currentElement);
    history.addChangeListener(super.changeListener);
    notifyAboutChange();
  }

  /**
   * Gets the element that is currently selected
   *
   * @return the current element
   */
  public HistoricObject getCurrentElement() {
    return history.getCurrentElement();
  }

  /**
   * Move forward in the history
   *
   * @return true for success, false if not possible
   */
  public boolean travelForward() {
    return history.travelForward();
  }

  /**
   * Move backward in the history
   *
   * @return true for success, false if not possible
   */
  public boolean travelBackward() {
    return history.travelBackward();
  }

  @Override
  public boolean isDirty() {
    return history.isDirty();
  }

  @Override
  public void setDirty(boolean dirty) {
    boolean oldValue=history.isDirty();
    history.setDirty(dirty);
    changes.firePropertyChange("isDirty",oldValue,dirty);
  }

  /**
   * Returns if it is possible to move forward
   *
   * @return true ==> possible, false ==> mission impossible
   */
  public boolean getForwardPossible() {
    return history.getForwardPossible();
  }

  /**
   * Returns if it is possible to move backward
   *
   * @return true ==> possible, false ==> mission impossible
   */
  public boolean getBackwardPossible() {
    return history.getBackwardPossible();
  }

  /**
   * Adds a completely new Object to History (e.g. for load)
   *
   * @param newObject the new Element to be added
   */
  public void addHistoricObject(HistoricObject newObject) {
    history.addHistoricObject(newObject);
  }

  @Override
  public Object getClone() {
    return null;
  }

  public void addPropertyChangeListener( PropertyChangeListener listener )
  {
    changes.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( PropertyChangeListener listener )
  {
    changes.removePropertyChangeListener( listener );
  }

}
