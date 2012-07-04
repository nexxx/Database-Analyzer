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
import dbaCore.data.FunctionalDependency;
import dbaCore.data.NormalizationResult;
import dbaCore.data.RelationSchema;
import dbaCore.logic.Analysis.RelationUtils;

import java.util.ArrayList;

/**
 * Normalization to 3.NF using Decomposition
 *
 * @author Sebastian Theuermann
 */
public class DecompositionTo3NF extends Decomposition {

  @Override
  public void normalize(RelationSchema relationToNormalize, NormalizationResult result, Boolean minimizeFds) {

    if (minimizeFds) {
      normalizeRelation(getMinimalRelation(relationToNormalize), result);
    } else {
      normalizeRelation(relationToNormalize, result);
    }

  }

  private void normalizeRelation(RelationSchema relationToNormalize, NormalizationResult result) {

    RelationSchema newSchema;

    ArrayList<FunctionalDependency> violatingFds = checker.checkForThirdNF(relationToNormalize);
    FunctionalDependency violatingFd;

    // If there is no FD violating 3NF ==> nothing to do here
    if (relationToNormalize.getFunctionalDependencies().isEmpty() || violatingFds.isEmpty()) {
      // Remove duplicate entry of relation in results
      for (RelationSchema relation : result.getRelations()) {
        if (relation.equals(relationToNormalize)) {
          result.getRelations().remove(relation);
          break;
        }
      }
      result.getRelations().add(relationToNormalize);
      return;
    }

    // FD Y ==> A violates 3NF
    newSchema = new RelationSchema();
    newSchema.setName(RelationUtils.getInstance().getRelationName(relationToNormalize.getName(),
      result.getRelations()));
    violatingFd = violatingFds.get(0);

    // S2 = YA
    // S1 = S - A
    transferFd(relationToNormalize, newSchema, violatingFd);

    // Update keys
    updatePrimaryAndForeignKeys(relationToNormalize, newSchema, result.getForeignKeys());

    // Recursive calls
    normalizeRelation(newSchema, result);
    normalizeRelation(relationToNormalize, result);

  }

  /**
   * Moves a FD and it's Attributes from one RelationSchema to another
   *
   * @param oldSchema the RelationSchema to cut from
   * @param newSchema the RelationSchema to paste into
   * @param fd        the FunctionalDependency to move
   */
  private void transferFd(RelationSchema oldSchema, RelationSchema newSchema, FunctionalDependency fd) {

    oldSchema.removeFunctionalDependency(fd);

    // Copy everything to new schema
    for (Attribute sourceAttribute : fd.getSourceAttributes()) {
      newSchema.addAttribute((Attribute) sourceAttribute.getClone());
    }
    for (Attribute targetAttribute : fd.getTargetAttributes()) {
      newSchema.addAttribute((Attribute) targetAttribute.getClone());
    }
    newSchema.getFunctionalDependencies().add(fd.getClone());

    // reconnect attributes with functional dependencies
    newSchema.restoreReferences();

    // Remove copied things in the old schema
    oldSchema.getAttributes().removeAll(fd.getTargetAttributes());

  }

}
