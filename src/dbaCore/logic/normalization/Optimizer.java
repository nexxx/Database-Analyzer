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
 * Class providing basic methods for relation-optimization
 *
 * @author Sebastian Theuermann
 */
public class Optimizer {
  private ArrayList<NormalizationAlgorithm> normalizationProcedures;
  protected GeneralRelationCheck checker;

  public Optimizer() {
    super();
    normalizationProcedures = new ArrayList<>();
    checker = new GeneralRelationCheck();
  }

  public NormalizationResult normalize(RelationSchema relationToNormalize, NormalForm actualNF, NormalForm targetNF) {
    RelationSchema relation = relationToNormalize.getClone();
    RelationSchema backupRelation;

    NormalizationResult result;

    // Pick up lonely Attributes (in no Functional dependency)
    RelationUtils.getInstance().determineAllAttributes(relation);

    // Set primary-key if not present
    if (RelationUtils.getInstance().getPrimaryKey(relation).getAttributes().isEmpty()) {
      Key newPrimaryKey = RelationUtils.getInstance().getKey(relation);
      for (Attribute attribute : relation.getAttributes()) {
        if (newPrimaryKey.getAttributes().contains(attribute)) {
          attribute.setIsPrimaryKey(true);
        }
      }
    }

    backupRelation = relation.getClone();

    normalizationProcedures.clear();
    switch (actualNF) {
      case FIRST:
        switch (targetNF) {
          case SECOND:
            // 1.NF ==> 2.NF
            normalizationProcedures.add(new DecompositionTo2NF());

            break;
          case THIRD:
            // 1.NF ==> 3.NF
            normalizationProcedures.add(new SyntheseTo3NF());
            break;
          case BOYCECODD:
            // 1.NF ==> BCNF
            normalizationProcedures.add(new SyntheseTo3NF());
            normalizationProcedures.add(new DecompositionToBCNF());
            break;
          default:
            throw new IllegalArgumentException();
        }
        break;
      case SECOND:
        switch (targetNF) {
          case THIRD:
            // 2.NF ==> 3.NF
            normalizationProcedures.add(new SyntheseTo3NF());
            break;
          case BOYCECODD:
            // 2.NF ==> BCNF
            normalizationProcedures.add(new SyntheseTo3NF());
            normalizationProcedures.add(new DecompositionToBCNF());
            break;
          default:
            throw new IllegalArgumentException();
        }
        break;
      case THIRD:
        // 3.NF ==> BCNF
        normalizationProcedures.add(new DecompositionToBCNF());
        break;
      default:
        throw new IllegalArgumentException();
    }

    result = startNormalizing(relation, normalizationProcedures, targetNF);

    if (result.getRelations().isEmpty()) {
      result.getRelations().add(backupRelation);
    }

    return result;

  }

  /**
   * Executes the normalization procedures defined in "procedures" on
   * the given schema, exits when the targetNf is reached
   *
   * @param schema the relation to normalize
   * @return the results of the Normalization
   */
  private NormalizationResult startNormalizing(RelationSchema schema, ArrayList<NormalizationAlgorithm> procedures,
                                               NormalForm targetNf) {
    NormalizationResult result = new NormalizationResult();
    NormalizationResult tempResult;

    result.getRelations().add(schema);

    for (NormalizationAlgorithm procedure : procedures) {
      // Quit if NF already reached
      if (checker.getNF(result.getRelations()).ordinal() >= targetNf.ordinal()) {
        break;
      }

      tempResult = (NormalizationResult) result.getClone();
      for (RelationSchema relation : tempResult.getRelations()) {
        procedure.normalize(relation, result, false);
      }
    }

    compactFds(result);

    return result;
  }


  /**
   * Compacts all Fd's of the given Relations
   *
   * @param result the NormalizationResult to work with
   */
  private void compactFds(NormalizationResult result) {
    for (RelationSchema relation : result.getRelations()) {
      uniteFdsWithSameLeftSide(relation);
    }
  }

  /**
   * Walks through all Fd's an united the fd's with the same left side
   *
   * @param relation the RelationSchema as source for the Fd's
   */
  private void uniteFdsWithSameLeftSide(RelationSchema relation) {
    ArrayList<FunctionalDependency> compactedFds = new ArrayList<>();

    for (FunctionalDependency fd : relation.getFunctionalDependencies()) {
      if (!containsLeftSide(compactedFds, fd)) {
        compactedFds.add(fd);
      } else {
        uniteWithExistingLeftSide(compactedFds, fd);
      }
    }

    relation.setFunctionalDependencies(compactedFds);

  }

  /**
   * Returns if a Fd with the same left side is already in the given
   * list
   *
   * @param fds   the ArrayList to look inside
   * @param newFd the fd with the left-side to look for
   * @return true if the ArrayList contains a fd with the same left
   *         side, false if not
   */
  private boolean containsLeftSide(ArrayList<FunctionalDependency> fds, FunctionalDependency newFd) {
    for (FunctionalDependency fd : fds) {
      if (fd.getSourceAttributes().equals(newFd.getSourceAttributes())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds the TargetAttributes of the newFd to the TargetAttributes of
   * a existing Fd with the same left side
   *
   * @param fds   a ArrayList with the existing FunctionalDependencies
   * @param newFd the FunctionalDependency to unite with a existing one
   * @return true for success, false if the was no existing Fd with
   *         the same left side
   */
  private boolean uniteWithExistingLeftSide(ArrayList<FunctionalDependency> fds, FunctionalDependency newFd) {
    for (FunctionalDependency fd : fds) {
      if (fd.getSourceAttributes().equals(newFd.getSourceAttributes())) {
        fd.getTargetAttributes().addAll(newFd.getTargetAttributes());
        return true;
      }
    }
    return false;
  }

}
