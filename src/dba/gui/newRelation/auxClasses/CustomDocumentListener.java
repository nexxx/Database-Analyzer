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

package dba.gui.newRelation.auxClasses;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Custom Document listener for monitoring changes in Textfield with
 * Relationname
 * 
 * @author Andreas Freitag
 * 
 */
public class CustomDocumentListener implements DocumentListener {

  private JButton button;

  /**
   * Constructor
   * 
   * @param b
   *          Button which will be disabled
   */
  public CustomDocumentListener(JButton b) {
	button = b;
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
	disableIfEmpty(e);
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
	disableIfEmpty(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
	disableIfEmpty(e);
  }

  /**
   * Disables button if textfield is empty
   * 
   * @param e
   *          Event
   */
  public void disableIfEmpty(DocumentEvent e) {
	button.setEnabled(e.getDocument().getLength() > 0);
  }

}
