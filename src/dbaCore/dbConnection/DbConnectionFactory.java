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

import dbaCore.data.dBTypes.TypeEnum;

import java.sql.SQLException;

/**
 * (Description)
 *
 * @author Andreas Freitag
 */
public class DbConnectionFactory {

  public static DbConnection getConnection(TypeEnum type, String user, String pwd, String address) throws SQLException {
    switch (type) {
      case MYSQL:
        return new MYSQLConnection(user, pwd, address);
      /*case POSTGRES:
        return new PostgresConnection(user, pwd, address);
      case MSDB:
        return null;
      case ORACLE:
        return null;
      case SQLITE:
        return new SQLiteConnection(address);  */
      default:
        return null;
    }
  }
}

