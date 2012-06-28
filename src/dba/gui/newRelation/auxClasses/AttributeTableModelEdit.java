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

package dba.gui.newRelation.auxClasses;

import data.Attribute;
import data.Database;
import data.RelationSchema;
import dba.gui.auxClasses.CustomTree;

import java.util.ArrayList;

/**
 * Custom Tablemodel which can handle our ArrayList with Attributes
 * and contains all Actions to handle it
 *
 * @author Andreas Freitag
 */
public class AttributeTableModelEdit extends AttributeTableModel {

  public AttributeTableModelEdit(ArrayList<Attribute> data, Database db, RelationSchema rel) {
    super(data, db, rel);
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    if (CustomTree.getInstance().getDatabase().getDatabase().size() >= 2) {
      return true;
    }
    return col != 3;

  }
}