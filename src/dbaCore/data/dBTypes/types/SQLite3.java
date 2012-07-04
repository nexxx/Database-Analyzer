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

package dbaCore.data.dBTypes.types;

import javax.swing.*;

/**
 * Singelton class with all available Attribute for a SQLite Database
 *
 * @author Andreas Freitag
 */
public class SQLite3 extends DbType {
  private final String[] types = {"---", "INT", "INTEGER", "TINYINT", "SMALINT", "BIGINT", "MEDIUMINT",
    "UNSIGNED BIG INT", "INT2", "INT2", "CHARACHTER", "VARCHAR", "VARYING CHARACTER", "NCHAR", "NATIVE CHARACTER",
    "NVARCHAR", "TEXT", "CLOB", "BLOB", "REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT", "NUMBERIC", "DECIMAL",
    "BOOLEAN", "DATE", "DATETIME"};
  private static SQLite3 instance = null;
  public JComboBox<String> combobox;

  @Override
  public String[] getTypes() {
    return types;
  }

  @Override
  public JComboBox<String> getCombobox() {
    return combobox;
  }

  private SQLite3() {
    super();
    combobox = new JComboBox<>(types);

  }

  /**
   * Getter for the singelton SQLite (thread-save)
   */
  public synchronized static SQLite3 getInstance() {
    if (instance == null) {
      synchronized (SQLite3.class) {
        instance = new SQLite3();
      }
    }
    return instance;
  }
}
