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

package dbaCore.data.events;

import java.util.ArrayList;

/**
 * Helper Class to handle Change Events
 *
 * @author Sebastian Theuermann
 */
public final class ChangeSupport {
  // ArrayList containing all ChangeListeners
  private ArrayList<ChangeListener> listeners = new ArrayList<>();

  /**
   * Adds a new ChangeListener to the collection
   *
   * @param listener the listener to add
   */
  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a ChangeListener from the collection
   *
   * @param listener the listener to remove
   */
  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a Change-Event indicating imminent change
   */
  public void fireBeforeChange() {
    distributeEvent(new Change(this, Time.BEFORECHANGE));
  }

  /**
   * Fires a Change-Event with the specified time
   *
   * @param time BeforeChange or AfterChange
   */
  public void fireChange(Time time) {
    distributeEvent(new Change(this, time));
  }

  /**
   * Fires a Change-Event indicating change has already happened
   */
  public void fireAfterChange() {
    distributeEvent(new Change(this, Time.AFTERCHANGE));
  }

  /**
   * Distributes the given ChangeEvent to all listeners
   *
   * @param changeEvent the changeEvent to broadcast
   */
  private void distributeEvent(Change changeEvent) {
    for (ChangeListener listener : listeners) {
      listener.Change(changeEvent);
    }
  }
}
