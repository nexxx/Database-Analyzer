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

package dba.gui.DatatypeMappingFrame;

import dba.utils.Localization;
import dbaCore.data.Attribute;
import dbaCore.data.dBTypes.TypeEnum;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Custom Tablemodel which can handle our ArrayList with Attributes
 * and contains all Actions to handle it
 *
 * @author Andreas Freitag
 */
public class DTMTableModel extends AbstractTableModel {
  /**
   *
   */
  private static final long serialVersionUID = 7202056709045229442L;
  private String[] columnNames;
  private ArrayList<Attribute> data;
  private ArrayList<Attribute> dataOld;

  private DTMTableModel(TypeEnum newType, TypeEnum oldType) {
    super();
    Localization locale = Localization.getInstance();
    columnNames = new String[]{locale.getString("Attribute"), locale.getString("DTM_Old") + ": " + oldType.getName(),
      locale.getString("DTM_New") + ": " + newType.getName()};
    data = new ArrayList<>();
  }

  /**
   * Constructor
   *
   * @param data ArrayList with Attributes
   */
  public DTMTableModel(ArrayList<Attribute> data, ArrayList<Attribute> dataOld, TypeEnum newType, TypeEnum oldType) {
    this(oldType, newType);
    this.data = data;
    this.dataOld = dataOld;
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public String getColumnName(int col) {
    return columnNames[col];
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (row < 0 || col < 0) {
      return null;
    }
    if (col == 0 || col == 1) {
      return getValueAt(dataOld.get(row), col);
    } else {
      return getValueAt(data.get(row), col);
    }
  }

  private Object getValueAt(Attribute attribute, int column) {
    switch (column) {
      case 0:
        return attribute.getName();
      case 1:
      case 2:
        return attribute.getConstraints();
      default:
        throw new IllegalArgumentException("ColumnIndex out of bounds");
    }
  }

  @Override
  public Class<?> getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return col == 2;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    setValueAt(data.get(row), col, value);
    fireTableCellUpdated(row, col);
  }

  private void setValueAt(Attribute attribute, int column, Object value) {
    switch (column) {
      case 2:
        if (value instanceof String) {
          attribute.setConstraints((String) value);
          return;
        }
      default:
        throw new IllegalArgumentException();
    }
  }
}