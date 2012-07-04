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
 * Singelton class with all available Attribute for a MYSQL Database
 *
 * @author Andreas Freitag
 */
public class MySql extends DbType {
  private final String[] types = {"---", "CHAR", "VARCHAR", "TINYTEXT", "TEXT", "BLOB", "MEDIUMTEXT", "MEDIUMBLOB",
    "LONGTEXT", "LONGBLOB", "TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL",
    "DATE", "DATETIME", "TIMESTAMP", "TIME", "ENUM", "SET"};
  private static MySql instance = null;
  public JComboBox<String> combobox;

  @Override
  public String[] getTypes() {
    return types;
  }

  @Override
  public JComboBox<String> getCombobox() {
    return combobox;
  }

  private MySql() {
    super();
    combobox = new JComboBox<>(types);

  }

  /**
   * Getter for the singelton MySql (thread-save)
   */
  public synchronized static MySql getInstance() {
    if (instance == null) {
      synchronized (MySql.class) {
        instance = new MySql();
      }
    }
    return instance;
  }
}
