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

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Custom Cell Renderer to provide a Combobox support for Attribute
 * type changes
 */
public class CustomTableComboboxRenderer extends JComboBox<String> implements
    TableCellRenderer {

  /**
   * 
   */
  private static final long serialVersionUID = -8508891862656940969L;

  public CustomTableComboboxRenderer(String[] items) {
	super(items);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
	  boolean isSelected, boolean hasFocus, int row, int column) {
	if (isSelected) {
	  setForeground(table.getSelectionForeground());
	  super.setBackground(table.getSelectionBackground());
	} else {
	  setForeground(table.getForeground());
	  setBackground(table.getBackground());
	}

	// Select the current value
	setSelectedItem(value);
	return this;
  }
}
