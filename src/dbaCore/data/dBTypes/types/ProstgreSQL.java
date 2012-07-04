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
 * Singelton class with all available Attribute for a PostgreSQL Database
 *
 * @author Andreas Freitag
 */
public class ProstgreSQL extends DbType {
  private final String[] types = {"---", "INT8", "SERIAL8", "BIT", "VARBIT", "BOOL", "BOX", "BYTE", "VARCHAR",
    "CHAR", "CIDR", "CIRCLE", "DATE", "FLOAT8", "INET", "INT4", "INTERCVAL", "LINE", "LESG", "MACADDR", "MONEY",
    "NUMERIC", "PATH", "POINT", "POLYGON", "FLOAT24", "INT2", "SERIAL4", "TEXT", "TIME", "TIMETZ", "TIMESTAMP",
    "TIMESTAMPTP", "TSQUERY", "TSVECTOR", "TXID_SNAPSHOT", "UUID", "XML"};
  private static ProstgreSQL instance = null;
  private JComboBox<String> combobox;

  @Override
  public String[] getTypes() {
    return types;
  }

  @Override
  public JComboBox<String> getCombobox() {
    return combobox;
  }

  private ProstgreSQL() {
    super();
    combobox = new JComboBox<>(types);
  }


  /**
   * Getter for the singelton PostgreSQL (thread-save)
   */
  public synchronized static ProstgreSQL getInstance() {
    if (instance == null) {
      synchronized (ProstgreSQL.class) {
        instance = new ProstgreSQL();
      }
    }
    return instance;
  }
}
