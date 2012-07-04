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
 * Basic Methods to analyze a relation
 *
 * @author Sebastian Theuermann
 */
public class RelationUtils {
  private static RelationUtils instance = new RelationUtils();

  // Avoid initialization with "new RelationUtils();"
  private RelationUtils() {
    super();
  }

  // Threadsafety first!
  public static synchronized RelationUtils getInstance() {
    return instance;
  }

  /**
   * Returns a name for a new Relation
   *
   * @param name   name of the parent-relation
   * @param schema the RelationSchema to work with
   * @return a new valid name for a relationSchema
   */
  public String getRelationName(String name, ArrayList<RelationSchema> schema) {
    int i = 1;
    while (relationNameAlreadyExists(name + String.valueOf(i), schema)) {
      i++;
    }
    return name + String.valueOf(i);
  }

  /**
   * Returns if a given Relation-name already exists
   *
   * @param name   the name to look for
   * @param schema the RelationSchema to work with
   * @return true if the name already exists/ false if not
   */
  public boolean relationNameAlreadyExists(String name, ArrayList<RelationSchema> schema) {
    for (RelationSchema rel : schema) {
      if (rel.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the CandidateKey with fewest Attributes s
   *
   * @param schema the RelationSchema to get the Keys from
   * @return the "smallest" Key
   */
  public Key getKey(RelationSchema schema) {
    GeneralRelationCheck checker = new GeneralRelationCheck();
    return getKey(checker.getAllCandidateKeys(schema));
  }

  /**
   * Returns the smallest of all candidateKeys
   *
   * @param keyList a list with all candidateKeys
   * @return the smallest candidateKey
   */
  public Key getKey(ArrayList<Key> keyList) {
    Key result = new Key();

    if (!keyList.isEmpty()) {
      result = keyList.get(0);
      for (Key key : keyList) {
        if (key.getAttributes().size() < result.getAttributes().size()) {
          result = key;
        }
      }
    }

    return result;
  }

  /**
   * Returns a Key made of all Attributes marked as PK
   *
   * @param schema the relation to work with
   * @return a key (all Attributes with pk=true)
   */
  public Key getPrimaryKey(RelationSchema schema) {
    Key result = new Key();

    for (Attribute attr : schema.getAttributes()) {

      if (attr.getIsPrimaryKey()) {
        result.getAttributes().add(attr);
      }
    }

    return result;
  }

  /**
   * Restores Attributes of the given relation with the attributes of
   * the functionalDependencies
   *
   * @param schema the relation to work with
   */
  public void restoreAttributesByFds(RelationSchema schema) {
    ArrayList<Attribute> allAttributes = new ArrayList<>();
    for (FunctionalDependency fd : schema.getFunctionalDependencies()) {
      for (Attribute attr : fd.getSourceAttributes()) {
        allAttributes.add(attr);
      }
      for (Attribute attr : fd.getTargetAttributes()) {
        allAttributes.add(attr);
      }
    }

    for (Attribute attribute : allAttributes) {
      if (!schema.getAttributes().contains(attribute)) {
        schema.addAttribute(attribute);
      }
    }
  }

  /**
   * Checks if at least one side of the given Fd contains a given
   * Attribute
   *
   * @param fd         Functional Dependency to work with
   * @param attributes Attributes to look for
   * @return true if the fd contains the attributes, false if not
   */
  public boolean isFdContainingGivenAttributes(FunctionalDependency fd, ArrayList<Attribute> attributes) {

    for (Attribute attribute : attributes) {
      if (fd.getSourceAttributes().contains(attribute)) {
        return true;
      } else if (fd.getTargetAttributes().contains(attribute)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Unites all fd's with equal left sides
   *
   * @param fdList to work with
   */
  public void uniteFdsWithSameLeftSide(ArrayList<FunctionalDependency> fdList) {
    ArrayList<FunctionalDependency> fdsToDelete = new ArrayList<>();
    for (FunctionalDependency fd : fdList) {
      // Don't deal with items that will be deleted shortly
      if (fdsToDelete.contains(fd)) {
        continue;
      }

      for (FunctionalDependency subFd : fdList) {
        if (fdsToDelete.contains(fd) || subFd == fd) {
          continue;
        }
        if (fd.getSourceAttributes().equals(subFd.getSourceAttributes())) {
          fd.getTargetAttributes().addAll(subFd.getTargetAttributes());
          fdsToDelete.add(subFd);
        }
      }
    }

    fdList.removeAll(fdsToDelete);
  }

  /**
   * Resets the Primary Key to the optimal choice
   */
  public void resetPrimaryKey(RelationSchema schema) {
    Key key = getKey(schema);
    for (Attribute attr : schema.getAttributes()) {
      attr.setIsPrimaryKey(key.getAttributes().contains(attr));
    }
  }

  /**
   * Returns the text for a given Normalform
   *
   * @param nf the nf to get a text for
   * @return a String representing the given nf
   */
  public String getNormalFormText(NormalForm nf) {
    switch (nf) {
      case BOYCECODD:
        return "BCNF";
      case THIRD:
        return "3.NF";
      case SECOND:
        return "2.NF";
      default:
        return "1.NF";
    }
  }

  /**
   * Let all undetermined Attributes be determined by the primaryKey
   *
   * @param relation the relation to work with
   */
  public void determineAllAttributes(RelationSchema relation) {
    Key primaryKey = RelationUtils.getInstance().getPrimaryKey(relation);

    ArrayList<Attribute> targetAttributes = new ArrayList<>();
    ArrayList<Attribute> determinedAttributes = getDeterminedAttributes(relation);

    for (Attribute attr : relation.getAttributes()) {
      if (!primaryKey.getAttributes().contains(attr)) {
        if (!determinedAttributes.contains(attr)) {
          targetAttributes.add(attr);
        }
      }
    }

    if (!targetAttributes.isEmpty()) {
      relation.addFunctionalDependency(new FunctionalDependency(primaryKey.getAttributes(), targetAttributes));
    }

    relation.updateFunctionalDependencies();

  }

  /**
   * Returns a ArrayList of Attributes which are determined by at
   * least one fd
   *
   * @param relation the relation to work with
   * @return a ArrayList with all Attributes that are determined by
   *         something
   */
  public ArrayList<Attribute> getDeterminedAttributes(RelationSchema relation) {
    ArrayList<Attribute> determinedAttributes = new ArrayList<>();

    for (FunctionalDependency fd : relation.getFunctionalDependencies()) {
      determinedAttributes.addAll(fd.getTargetAttributes());
    }

    return determinedAttributes;
  }

}
