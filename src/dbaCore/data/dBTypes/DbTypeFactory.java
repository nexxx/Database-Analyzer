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

import dbaCore.data.Database;
import dbaCore.data.dBTypes.types.*;

/**
 * Factory Class to provide the correct datatype class
 *
 * @author Andreas Freitag
 */
public class DbTypeFactory {
  private Database database;

  public DbTypeFactory(Database db) {
    super();
    this.database = db;
  }

  /**
   * Factory method which returns the correct Datatype class
   *
   * @return DbType Class acc. to selection in Database
   */

  public DbType getType() {
    switch (database.getType()) {
      case MYSQL:
        return MySql.getInstance();
      /*case POSTGRES:
        return ProstgreSQL.getInstance();
      case MSDB:
        return MicrosoftDatabase.getInstance();
      case ORACLE:
        return Oracle.getInstance();
      case SQLITE:
        return SQLite3.getInstance();  */
      default:
        throw new IllegalArgumentException();
    }

  }


}
