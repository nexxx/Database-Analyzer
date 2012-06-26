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

import javax.swing.DefaultListSelectionModel;

/**
 * Class to provide our needed List Selection Model - Multiselection
 * without CTRL
 * 
 * @author Andreas Freitag
 * 
 */
public class MultiListSelectionModel extends DefaultListSelectionModel {
  /**
   * 
   */
  private static final long serialVersionUID = 3579713089677925078L;

  @Override
  public void setSelectionInterval(int index0, int index1) {
	if (super.isSelectedIndex(index0)) {
	  super.removeSelectionInterval(index0, index1);
	} else {
	  super.addSelectionInterval(index0, index1);
	}
  }

}
