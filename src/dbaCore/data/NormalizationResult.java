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

package dbaCore.data;

import java.util.ArrayList;

/**
 * Class representing the result of a normalization
 *
 * @author Sebastian Theuermann
 */
public class NormalizationResult {
  private ArrayList<RelationSchema> relations;
  private ArrayList<ForeignKeyConstraint> foreignKeys;

  public NormalizationResult() {
    super();
    relations = new ArrayList<>();
    foreignKeys = new ArrayList<>();
  }

  public NormalizationResult(ArrayList<RelationSchema> relations, ArrayList<ForeignKeyConstraint> foreignKeys) {
    super();
    this.relations = relations;
    this.foreignKeys = foreignKeys;
  }

  /**
   * @return the relations
   */
  public ArrayList<RelationSchema> getRelations() {
    return relations;
  }

  /**
   * @param relations the relations to set
   */
  public void setRelations(ArrayList<RelationSchema> relations) {
    this.relations = relations;
  }

  /**
   * @return the foreignKeys
   */
  public ArrayList<ForeignKeyConstraint> getForeignKeys() {
    return foreignKeys;
  }

  /**
   * @param foreignKeys the foreignKeys to set
   */
  public void setForeignKeys(ArrayList<ForeignKeyConstraint> foreignKeys) {
    this.foreignKeys = foreignKeys;
  }

  @SuppressWarnings("unchecked")
  public Object getClone() {
    NormalizationResult clone = new NormalizationResult();
    clone.setRelations((ArrayList<RelationSchema>) relations.clone());
    clone.setForeignKeys((ArrayList<ForeignKeyConstraint>) foreignKeys.clone());
    return clone;
  }
}
