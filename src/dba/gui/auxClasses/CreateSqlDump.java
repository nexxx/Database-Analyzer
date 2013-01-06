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

package dba.gui.auxClasses;

import dba.gui.CustomTree;
import dbaCore.data.Attribute;
import dbaCore.data.ForeignKeyConstraint;
import dbaCore.data.RelationSchema;

import java.util.ArrayList;

/**
 * Create an SQL Dump from current database
 *
 * @author Andreas Freitag
 */
public class CreateSqlDump {

  public String getDump() {
    ArrayList<RelationSchema> relations = CustomTree.getInstance().getDatabase().getDatabase();
    String sqlDump = "";

    sqlDump = sqlDump + "set foreign_key_checks=0;\n";
    for (RelationSchema rel : relations) {
      sqlDump = sqlDump + "drop table if exists " + rel.getName() + ";\n";
    }
    sqlDump = sqlDump + "set foreign_key_checks=1;\n\n";
    for (RelationSchema rel : relations) {
      sqlDump = sqlDump + "create table " + rel.getName() + "\n(\n";
      String pks = "";
      String tmpString = "";
      for (Attribute attr : rel.getAttributes()) {
        tmpString = tmpString + "\n  " + attr.getName() + " " + attr.getConstraints() + ",";
        if (attr.getIsPrimaryKey()) {
          pks = pks + attr.getName() + ", ";
        }
      }
      sqlDump = sqlDump + tmpString + "\n";
      pks = pks.substring(0, pks.length() - 2);
      sqlDump = sqlDump + "  primary key(" + pks + ")\n";
      sqlDump = sqlDump + ");\n\n";

    }

    ArrayList<ForeignKeyConstraint> fks = CustomTree.getInstance().getDatabase().getForeignKeys();
    int i = 0;
    for (RelationSchema rel : relations) {
      for (Attribute attr : rel.getAttributes()) {
        String fkTarget = "";
        if (attr.getIsForeignKey()) {
          for (ForeignKeyConstraint fk : fks) {
            if (fk.getSourceRelationName().equalsIgnoreCase(rel.getName()) && fk.getSourceAttributeName()
              .equalsIgnoreCase(attr.getName())) {
              fkTarget = fk.getTargetRelationName() + "(" + fk.getTargetAttributeName() + ")";
            }
          }
          sqlDump = sqlDump + "alter table " + rel.getName() + " add constraint fk_" + attr.getName() + i++ + " " +
            "foreign key (" + attr.getName() + ")" + " references " + fkTarget + ";\n";
        }
      }
    }

    return sqlDump;
  }
}
