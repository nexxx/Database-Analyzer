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

package dbaCore.dbConnection;

import java.sql.DriverManager;

/**
 * Create a connection to a Database and import Relations, Attributes and Keys
 *
 * @author Andreas Freitag
 */
public class SQLiteConnection extends DbConnection {

  private String driver = null;
  private String DbUrl = null;

  public SQLiteConnection(String url) throws Exception {
    super();
    driver = "org.sqlite.JDBC";
    DbUrl = "jdbc:sqlite:" + url;

    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(DbUrl);

      getRelationsFromDb();
      getAttributesFromDb();
      getForeignKeysFromDb();

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("DB Connection Failed");
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
