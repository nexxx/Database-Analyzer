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
 * Singelton class with all available Attribute for a MS Database
 *
 * @author Andreas Freitag
 */
public class MicrosoftDatabase extends DbType {
  private final String[] types = {"---", "BIGINT", "INT", "SMALLINT", "TINYINT", "BIT", "DECIMAL", "NUMERIC",
    "MONEY", "SMALLMONEY", "FLOAT", "REAL", "DATETIME", "SMALLDATETIME", "CHAR", "VARCHAR", "TEXT", "NCHAR",
    "NVARCHAR", "NTEXT", "BINARY", "VARBINARY", "IMAGE", "CURSOR", "SQL_VARIANT", "TABLE", "TIMESTAMP",
    "UNIQUEIDENTIFIER"};
  private static MicrosoftDatabase instance = null;
  public JComboBox<String> combobox;

  @Override
  public String[] getTypes() {
    return types;
  }

  @Override
  public JComboBox<String> getCombobox() {
    return combobox;
  }

  private MicrosoftDatabase() {
    super();
    combobox = new JComboBox<>(types);

  }

  /**
   * Getter for the singelton MSDatabase (thread-save)
   */
  public synchronized static MicrosoftDatabase getInstance() {
    if (instance == null) {
      synchronized (MicrosoftDatabase.class) {
        instance = new MicrosoftDatabase();
      }
    }
    return instance;
  }
}
