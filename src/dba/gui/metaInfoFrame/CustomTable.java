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

package dba.gui.metaInfoFrame;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Class provide a custom JTable
 *
 * @author Andreas Freitag
 */
public class CustomTable extends JTable {

  /**
   *
   */
  private static final long serialVersionUID = -6377053654548068024L;

  public CustomTable(PersonTableModel model) {
    super(model);
  }

  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
    Component comp = super.prepareRenderer(renderer, row, col);
    JComponent jcomp = (JComponent) comp;
    jcomp.setToolTipText((String) getValueAt(row, col));
    return comp;
  }

}
