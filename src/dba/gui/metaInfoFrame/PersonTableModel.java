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

import data.Person;
import dba.utils.Localization;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Custom Tablemodel which can handle our ArrayList with Attributes
 * and contains all Actions to handle it
 *
 * @author Andreas Freitag
 */
public class PersonTableModel extends AbstractTableModel {
  /**
   *
   */
  private static final long serialVersionUID = 7202056709045229442L;
  private String[] columnNames;
  private ArrayList<Person> data;

  private PersonTableModel() {
    super();
    Localization locale = Localization.getInstance();
    columnNames = new String[]{locale.getString("MI_Name"),
            locale.getString("MI_Title"), locale.getString("MI_Mail"),
            locale.getString("MI_Tel"), locale.getString("MI_Fax")};
    data = new ArrayList<>();
  }

  /**
   * Constructor
   *
   * @param data ArrayList with Attributes
   */
  public PersonTableModel(ArrayList<Person> data) {
    this();
    this.data = data;
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
    return getValueAt(data.get(row), col);
  }

  private Object getValueAt(Person person, int column) {
    switch (column) {
      case 0:
        return person.getName();
      case 1:
        return person.getJob();
      case 2:
        return person.getMail();
      case 3:
        return person.getTel();
      case 4:
        return person.getFax();
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
    return true;

  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    setValueAt(data.get(row), col, value);
    fireTableCellUpdated(row, col);
  }

  private void setValueAt(Person person, int column, Object value) {
    switch (column) {
      case 0:
        if (value instanceof String) {
          person.setName((String) value);
          return;
        }
      case 1:
        if (value instanceof String) {
          person.setJob((String) value);
          return;
        }
      case 2:
        if (value instanceof String) {
          person.setMail((String) value);
          return;
        }
      case 3:
        if (value instanceof String) {
          person.setTel((String) value);
          return;
        }
      case 4:
        if (value instanceof String) {
          person.setFax((String) value);
          return;
        }
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Add new row to our Table
   *
   * @param person Attribute which will be added
   */
  public void addRow(Person person) {
    data.add(person);
    fireTableRowsInserted(0, getRowCount());
  }

  /**
   * Delete row from our Table
   *
   * @param selectedRows Row to delete ( 0 until n )
   */
  public void removeRow(int selectedRows) {
    data.remove(selectedRows);
    fireTableRowsDeleted(selectedRows, selectedRows);
  }

}