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
import dbaCore.logic.Analysis.RelationUtils;

import java.util.ArrayList;

/**
 * Class providing basic synthesis-methods
 */
public abstract class Synthese implements NormalizationAlgorithm {

  /**
   * Unites groups of fd's that are mutual dependent
   *
   * @param groups the groups of functional dependencies to work with
   */
  protected void uniteMutualDepdendentGroups(ArrayList<ArrayList<FunctionalDependency>> groups) {
    ArrayList<ArrayList<FunctionalDependency>> mutualDependentGroups;
    do {
      mutualDependentGroups = getMutualDependentGroups(groups);
      if (mutualDependentGroups.size() == 2) {
        for (ArrayList<FunctionalDependency> list : groups) {
          if (list.equals(mutualDependentGroups.get(0))) {
            list.addAll(mutualDependentGroups.get(1));
            break;
          }
        }
        groups.remove(mutualDependentGroups.get(1));
      }

    } while (!mutualDependentGroups.isEmpty());
  }

  /**
   * Returns two mutual dependent Groups
   *
   * @param groups the overall list of FunctionalDependencies to work with
   * @return A ArrayList of mutual dependent groups
   */
  protected ArrayList<ArrayList<FunctionalDependency>> getMutualDependentGroups
  (ArrayList<ArrayList<FunctionalDependency>> groups) {
    ArrayList<ArrayList<FunctionalDependency>> mutualDependentGroups = new ArrayList<>();

    for (ArrayList<FunctionalDependency> group : groups) {
      for (FunctionalDependency fd : group) {
        for (ArrayList<FunctionalDependency> group2 : groups) {
          if (group == group2) {
            continue;
          }
          for (FunctionalDependency fd2 : group2) {
            if (fd.getTargetAttributes().equals(fd2.getSourceAttributes()) && fd2.getTargetAttributes().equals(fd
              .getSourceAttributes())) {
              mutualDependentGroups.add(group);
              mutualDependentGroups.add(group2);
              return mutualDependentGroups;
            }
          }
        }
      }
    }

    return mutualDependentGroups;
  }

  /**
   * Returns one fully contained relation at a time
   *
   * @param schemata all relationSchemata to work with
   * @return the fully contained RelationSchema or null if none
   *         existing
   */
  protected RelationSchema getFullyContainedRelation(ArrayList<RelationSchema> schemata) {
    // TODO: Rework me!
    for (RelationSchema schemaToTest : schemata) {
      for (RelationSchema schema : schemata) {
        if (schemaToTest == schema) {
          continue;
        }
        if (getNumberOfMatchingFds(schemaToTest, schema) == schemaToTest.getFunctionalDependencies().size() &&
          !schemaToTest.getFunctionalDependencies().isEmpty()) {
          return schemaToTest;
        }
      }
    }

    return null;
  }

  /**
   * Creates and returns a new RelationSchema from a given List of Fds
   *
   * @param fds the list of functional dependencies to work with
   * @return a new RelationSchema
   */
  protected RelationSchema getSchemaFromFds(ArrayList<FunctionalDependency> fds) {
    RelationSchema schema = new RelationSchema();

    // Add fd's
    schema.setFunctionalDependencies(fds);

    // Add attributes
    for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
      for (Attribute attribute : fd.getSourceAttributes()) {
        schema.addAttribute(attribute);
      }
      for (Attribute attribute : fd.getTargetAttributes()) {
        schema.addAttribute(attribute);
      }
    }

    return schema;
  }

  /**
   * Looks up, if at least any of the relations contains a candidate
   * Key for everything
   *
   * @param schemata      the ArrayList of Relations to search in
   * @param candidateKeys the candidateKeys to look for in the relations
   * @return true if any Relation contains a key, false if not
   */
  protected boolean isAnyRelationContainingAKey(ArrayList<RelationSchema> schemata, ArrayList<Key> candidateKeys) {
    for (RelationSchema schema : schemata) {
      for (Key candidateKey : candidateKeys) {
        if (schema.getAttributes().containsAll(candidateKey.getAttributes())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Functional Depdendencies with the same left side get a own group
   *
   * @param fds the set of functional dependencies to work with
   * @return a ArrayList of Groups having the same left side
   */
  protected ArrayList<ArrayList<FunctionalDependency>> getGroupsWithSameLeftSide(ArrayList<FunctionalDependency> fds) {
    ArrayList<ArrayList<FunctionalDependency>> groups = new ArrayList<>();
    ArrayList<FunctionalDependency> newList;
    boolean inserted;

    for (FunctionalDependency fd : fds) {
      inserted = false;

      for (ArrayList<FunctionalDependency> fdlist : groups) {
        if (hasSameLeftSide(fd, fdlist)) {
          fdlist.add(fd);
          inserted = true;
          break;
        }
      }

      if (!inserted) {
        newList = new ArrayList<>();
        newList.add(fd);
        groups.add(newList);
      }
    }

    return groups;
  }

  /**
   * Checks if a fd has the same left side as the fds of a list
   *
   * @param fd   the fd to decide on
   * @param list the list of fds to compare with
   * @return true if it has the same left side, false if not
   */
  private boolean hasSameLeftSide(FunctionalDependency fd, ArrayList<FunctionalDependency> list) {
    for (FunctionalDependency fdep : list) {
      if (fdep.getSourceAttributes().containsAll(fd.getSourceAttributes()) && fd.getSourceAttributes().containsAll
        (fdep.getSourceAttributes())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the number of matching fds of two relations
   *
   * @param schema1 first relation to compare
   * @param schema2 second relation to compare
   * @return the number of matching functional dependencies
   */
  protected int getNumberOfMatchingFds(RelationSchema schema1, RelationSchema schema2) {
    int numberOfMatches = 0;

    for (FunctionalDependency fd1 : schema1.getFunctionalDependencies()) {
      for (FunctionalDependency fd2 : schema2.getFunctionalDependencies()) {
        if (fd1.equals(fd2)) {
          numberOfMatches++;
          break;
        }
      }
    }

    return numberOfMatches;
  }

  /**
   * Updates the primary- and foreignKeys of the given relations
   *
   * @param relations   the relations to work with
   * @param foreignKeys the foreign keys to take into account
   */
  public void updatePrimaryAndForeignKeys(ArrayList<RelationSchema> relations, ArrayList<ForeignKeyConstraint>
    foreignKeys) {

    // set primaryKey for newly created relation
    for (RelationSchema relation : relations) {
      RelationUtils.getInstance().resetPrimaryKey(relation);
    }

    // set foreign keys
    for (RelationSchema relation : relations) {
      for (RelationSchema subRelation : relations) {
        if (relation == subRelation) {
          continue;
        }
        for (Attribute attr : relation.getAttributes()) {
          for (Attribute subAttr : subRelation.getAttributes()) {
            if (attr.getName().equals(subAttr.getName()) && subAttr.getIsPrimaryKey() && RelationUtils.getInstance()
              .getKey(subRelation).getAttributes().size() == 1) {
              foreignKeys.add(new ForeignKeyConstraint(relation.getName(), attr.getName(), subRelation.getName(),
                subAttr.getName()));
              attr.setIsForeignKey(true);
            }
          }
        }
      }
    }
  }
}