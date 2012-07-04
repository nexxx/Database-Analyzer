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

package dbaCore.logic.normalization;

import dbaCore.data.Attribute;
import dbaCore.data.ForeignKeyConstraint;
import dbaCore.data.RelationSchema;
import dbaCore.logic.Analysis.GeneralRelationCheck;
import dbaCore.logic.Analysis.RelationUtils;

import java.util.ArrayList;

public abstract class Decomposition implements NormalizationAlgorithm {
  protected GeneralRelationCheck checker = new GeneralRelationCheck();

  public RelationSchema getMinimalRelation(RelationSchema relationToNormalize) {
    RelationSchema relation = relationToNormalize.getClone();

    relation.setFunctionalDependencies(checker.getMinimalSetOfFds(relation.getFunctionalDependencies()));

    RelationUtils.getInstance().uniteFdsWithSameLeftSide(relation.getFunctionalDependencies());

    relation.restoreReferences();

    return relation;
  }

  /**
   * Updates the primary- and foreignKeys of the given relations
   *
   * @param oldRelation the old relation
   * @param newRelation the newly created relation
   * @param foreignKeys a ArrayList as Container for new foreignKeys
   */
  public void updatePrimaryAndForeignKeys(RelationSchema oldRelation, RelationSchema newRelation,
                                          ArrayList<ForeignKeyConstraint> foreignKeys) {

    // set primaryKey for newly created relation
    RelationUtils.getInstance().resetPrimaryKey(newRelation);

    // update foreignkeyConstraints
    for (Attribute newAttr : newRelation.getAttributes()) {
      for (Attribute oldAttr : oldRelation.getAttributes()) {
        if (newAttr.getIsPrimaryKey()) {
          if (newAttr.getName().equals(oldAttr.getName())) {
            foreignKeys.add(new ForeignKeyConstraint(oldRelation.getName(), oldAttr.getName(), newRelation.getName(),
              newAttr.getName()));
            oldAttr.setIsForeignKey(true);
          }
        }
      }
    }

  }
}
