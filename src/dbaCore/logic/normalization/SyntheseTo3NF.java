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

import dbaCore.data.*;
import dbaCore.logic.Analysis.GeneralRelationCheck;
import dbaCore.logic.Analysis.RelationUtils;

import java.util.ArrayList;

/**
 * Synthese-Algorithm for Third-NF
 *
 * @author Sebastian Theuermann
 */
public class SyntheseTo3NF extends Synthese {
  private GeneralRelationCheck checker;

  public SyntheseTo3NF() {
    super();
    checker = new GeneralRelationCheck();
  }

  @Override
  public void normalize(RelationSchema relationToNormalize, NormalizationResult result, Boolean minimizeFds) {
    result.getRelations().clear();
    normalizeRelation(relationToNormalize, result);
  }

  private void normalizeRelation(RelationSchema relationToNormalize, NormalizationResult result) {

    ArrayList<ArrayList<FunctionalDependency>> groups;
    RelationSchema tempSchema;
    ArrayList<Key> candidateKeys = checker.getAllCandidateKeys(relationToNormalize);

    // Make Fd's minimal
    relationToNormalize.setFunctionalDependencies(checker.getMinimalSetOfFds(relationToNormalize
      .getFunctionalDependencies()));

    // Create Groups
    groups = getGroupsWithSameLeftSide(relationToNormalize.getFunctionalDependencies());

    // Unite mutual dependent Groups
    uniteMutualDepdendentGroups(groups);

    // Create a relation from each set of functional Dependencies
    for (ArrayList<FunctionalDependency> fdList : groups) {
      tempSchema = getSchemaFromFds(fdList);
      tempSchema.setName(RelationUtils.getInstance().getRelationName(relationToNormalize.getName(),
        result.getRelations()));
      result.getRelations().add(tempSchema);
    }

    // Search for a candidateKey in the Relations
    // If none existing, add new Relation with key
    if (!isAnyRelationContainingAKey(result.getRelations(), candidateKeys)) {
      tempSchema = new RelationSchema();
      for (Attribute attr : RelationUtils.getInstance().getKey(candidateKeys).getAttributes()) {
        tempSchema.addAttribute(attr);
      }
      tempSchema.setName(RelationUtils.getInstance().getRelationName(relationToNormalize.getName(),
        result.getRelations()));
      result.getRelations().add(tempSchema);
    }

    // Remove relations that are fully contained by other relations,
    // one by one
    RelationSchema fullyContainedRelation = getFullyContainedRelation(result.getRelations());
    while (fullyContainedRelation != null) {
      result.getRelations().remove(fullyContainedRelation);
    }

    super.updatePrimaryAndForeignKeys(result.getRelations(), result.getForeignKeys());
  }
}
