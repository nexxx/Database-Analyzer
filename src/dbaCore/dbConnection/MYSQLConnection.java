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
import java.sql.SQLException;

/**
 * Create a connection to a Database and import Relations, Attributes and Keys
 *
 * @author Andreas Freitag
 */
public class MYSQLConnection extends DbConnection {

  private String userName = null;
  private String password = null;
  private String driver = null;
  private String DbUrl = null;

  public MYSQLConnection(String user, String pwd, String url) throws SQLException {
    super();
    userName = user;
    password = pwd;
    driver = "com.mysql.jdbc.Driver";
    DbUrl = "jdbc:mysql://" + url;

    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(DbUrl, userName, password);

      getRelationsFromDb();
      getAttributesFromDb();
      getForeignKeysFromDb();

    } catch (SQLException e) {
      //System.out.println(e.getMessage());
      //e.printStackTrace();
      throw e;
    } catch (ClassNotFoundException e) {
      //e.printStackTrace();
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
