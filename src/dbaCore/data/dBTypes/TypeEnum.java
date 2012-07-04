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

package dbaCore.data.dBTypes;

/**
 * Enumeration for Database type names
 *
 * @author Andreas Freitag
 */
public enum TypeEnum {
  MYSQL("MYSQL"), POSTGRES("PostgreSQL"), MSDB("Microsoft"), ORACLE("Oracle"), SQLITE("SQLite3");

  private final String name;

  private TypeEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Get the Enumeration by String (name)
   *
   * @param value Name to look for
   * @return Enumeration with value "name", or null if failure
   */
  public static TypeEnum getEnumByValue(String value) {
    for (TypeEnum e : values()) {
      if (e.getName().endsWith(value)) {
        return e;
      }
    }
    return null;
  }

}
