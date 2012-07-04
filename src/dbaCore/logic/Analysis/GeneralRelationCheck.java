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

package dbaCore.logic.Analysis;

import dbaCore.data.*;

import java.util.ArrayList;

/**
 * Class containing the general Relation-check
 *
 * @author Sebastian Theuermann
 */
public class GeneralRelationCheck extends RelationCheck {

  /**
   * Returns the NormalForm of the given Schema
   */
  @Override
  public NormalForm getNF(RelationSchema schema, ArrayList<FunctionalDependency> violatingFds) {
    ArrayList<FunctionalDependency> prevViolatingFds = new ArrayList<>();

    // BCNF
    updateFdList(violatingFds, checkForBCNF(schema));
    if (violatingFds.isEmpty()) {
      return NormalForm.BOYCECODD;
    }
    updateFdList(prevViolatingFds, violatingFds);

    // Third NF
    updateFdList(violatingFds, checkForThirdNF(schema));
    if (violatingFds.isEmpty()) {
      updateFdList(violatingFds, prevViolatingFds);
      return NormalForm.THIRD;
    }
    updateFdList(prevViolatingFds, violatingFds);

    // Second NF
    updateFdList(violatingFds, checkForSecondNF(schema));
    if (violatingFds.isEmpty()) {
      updateFdList(violatingFds, prevViolatingFds);
      return NormalForm.SECOND;
    } else {
      return NormalForm.FIRST;
    }
  }

  /**
   * Returns the highest Normal Form of the given relations
   *
   * @param relations relations to be checked
   * @return the highest NormalForm of the relations
   */
  @Override
  public NormalForm getNF(ArrayList<RelationSchema> relations) {
    NormalForm totalNf = NormalForm.BOYCECODD;
    NormalForm relationNf;

    for (RelationSchema relation : relations) {
      relationNf = getNF(relation, new ArrayList<FunctionalDependency>());
      if (relationNf.ordinal() < totalNf.ordinal()) {
        totalNf = relationNf;
      }
    }

    return totalNf;
  }

  /**
   * Updates the source-List with the values of the target-List
   *
   * @param target the new values for the source-list
   * @param source the list to update
   */
  private void updateFdList(ArrayList<FunctionalDependency> target, ArrayList<FunctionalDependency> source) {
    target.clear();
    target.addAll(source);
  }

  /**
   * Returns if a given Schema is in the second NF or not
   */
  @Override
  public boolean isSecondNF(RelationSchema schema) {
    return checkForSecondNF(schema).isEmpty();
  }

  /**
   * Returns all functional dependencies that violate the second NF
   *
   * @param schema the RelationSchema to work with
   * @return a ArrayList containing all fd's that violate 2.NF
   */
  @Override
  public ArrayList<FunctionalDependency> checkForSecondNF(RelationSchema schema) {
    ArrayList<FunctionalDependency> violatingFds = new ArrayList<>();

    ArrayList<Key> candidateKeys = getAllCandidateKeys(schema);
    if (candidateKeys.isEmpty()) {
      candidateKeys.add(RelationUtils.getInstance().getPrimaryKey(schema));
    }

    int numberOfMatches;

    for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
      if (isListContainingANonPrimeAttribute(fd.getTargetAttributes(), candidateKeys)) {
        for (Key key : candidateKeys) {
          if (key.getAttributes().size() > 1) {
            numberOfMatches = getNumberOfMatchingAttributes(key.getAttributes(), fd.getSourceAttributes());
            if (numberOfMatches > 0 && numberOfMatches < key.getAttributes().size()) {
              violatingFds.add(fd);
            }
          }
        }
      }
    }
    return violatingFds;
  }

  /**
   * Returns if a given Schema is in third NF
   */
  @Override
  public boolean isThirdNF(RelationSchema schema) {
    return checkForThirdNF(schema).isEmpty();
  }

  /**
   * Returns all functional dependencies that violate the Third NF
   *
   * @param schema the RelationSchema to work with
   * @return a ArrayList containing all fds that violate Third NF
   */
  @Override
  public ArrayList<FunctionalDependency> checkForThirdNF(RelationSchema schema) {
    ArrayList<FunctionalDependency> violatingFds = new ArrayList<>();
    ArrayList<Key> candidateKeys = getAllCandidateKeys(schema);

    for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
      if (isListContainingANonPrimeAttribute(fd.getTargetAttributes(), candidateKeys)) {
        if (!isCandidateKey(fd.getSourceAttributes(), candidateKeys) && !isSuperKey(fd.getSourceAttributes(),
          candidateKeys) && !fd.getSourceAttributes().containsAll(fd.getTargetAttributes())) {
          violatingFds.add(fd);
        }
      }
    }

    return violatingFds;
  }

  /**
   * Returns if a given schema is in Boyce Codd NF
   */
  @Override
  public boolean isBCNF(RelationSchema schema) {
    return checkForBCNF(schema).isEmpty();
  }

  /**
   * Returns all functional dependencies that violate the BCNF
   *
   * @param schema the RelationSchema to work with
   * @return a ArrayList containing all fds that violate BCNF
   */
  @Override
  public ArrayList<FunctionalDependency> checkForBCNF(RelationSchema schema) {
    ArrayList<FunctionalDependency> violatingFds = new ArrayList<>();
    ArrayList<Key> candidateKeys = getAllCandidateKeys(schema);

    for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
      if (!isCandidateKey(fd.getSourceAttributes(), candidateKeys) && !isSuperKey(fd.getSourceAttributes(),
        candidateKeys) && !fd.getSourceAttributes().containsAll(fd.getTargetAttributes())) {
        violatingFds.add(fd);
      }
    }

    return violatingFds;
  }

  /**
   * Returns if a given ArrayList contains a non-Prime Attribute
   *
   * @param list          the ArrayList to work with
   * @param candidateKeys the candidateKeys which are used to detect the prime
   *                      attributes
   * @return true if the list contains a nonPrime, false if not
   */
  private boolean isListContainingANonPrimeAttribute(ArrayList<Attribute> list, ArrayList<Key> candidateKeys) {
    for (Attribute attribute : list) {
      if (!isPrimeAttribute(attribute, candidateKeys)) {
        return true;
      }
    }
    return false;
  }

}
