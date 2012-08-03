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

import dbaCore.data.Attribute;
import dbaCore.data.Database;
import dbaCore.data.ForeignKeyConstraint;
import dbaCore.data.RelationSchema;

import java.sql.*;
import java.util.ArrayList;

/**
 * Create a connection to a Database and import Relations, Attributes and Keys
 *
 * @author Andreas Freitag
 */
public abstract class DbConnection {

  Connection conn = null;
  private DatabaseMetaData dbm = null;
  private ArrayList<String> relationNames = null;
  private ResultSet tables = null;
  private Database database = null;

  protected DbConnection() {
    super();
    relationNames = new ArrayList<>();
    database = new Database();
  }

  public Database getDatabase() {
    return database;
  }

  void getRelationsFromDb() throws SQLException {
    //Get Relation Names
    dbm = conn.getMetaData();
    String[] types = {"TABLE"};
    tables = dbm.getTables(null, null, "%", types);
    while (tables.next()) {
      String table = tables.getString("TABLE_NAME");
      relationNames.add(table);
    }
  }

  void getAttributesFromDb() throws SQLException {
    for (String relation : relationNames) {
      RelationSchema tmpRelation = new RelationSchema(relation);

      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM " + relation);
      ResultSetMetaData md = rs.getMetaData();
      int col = md.getColumnCount();
      for (int i = 1; i <= col; i++) {
        String col_name = md.getColumnName(i);
        String dataType = md.getColumnTypeName(i);
        Attribute attr = new Attribute(col_name);
        attr.setType(dataType.toUpperCase());
        tmpRelation.addAttribute(attr);
      }
      ResultSet pks = dbm.getPrimaryKeys(null, null, relation);
      while (pks.next()) {
        String columnName = pks.getString("COLUMN_NAME");
        tmpRelation.getAttributeByName(columnName).setIsPrimaryKey(true);
      }
      database.addRelationSchema(tmpRelation);
    }
  }

  void getForeignKeysFromDb() throws SQLException {
    for (String relation : relationNames) {
      ResultSet fks = dbm.getExportedKeys(conn.getCatalog(), null, relation);

      while (fks.next()) {

        String fkTableName = fks.getString("FKTABLE_NAME");
        String fkColumnName = fks.getString("FKCOLUMN_NAME");
        String pkTableName = fks.getString("PKTABLE_NAME");
        String pkColumnName = fks.getString("PKCOLUMN_NAME");


        ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint();
        foreignKeyConstraint.setSourceRelationName(fkTableName);
        foreignKeyConstraint.setSourceAttributeName(fkColumnName);
        foreignKeyConstraint.setTargetRelationName(pkTableName);
        foreignKeyConstraint.setTargetAttributeName(pkColumnName);
        database.getForeignKeys().add(foreignKeyConstraint);
        database.getRelationSchemaByName(fkTableName).getAttributeByName(fkColumnName).setIsForeignKey(true);
      }
    }
  }
}
