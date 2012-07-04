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
 * Superclass for the RelationCheck-Classes, offers common methods
 *
 * @author Sebastian Theuermann
 */
public abstract class RelationCheck implements RelationInformation {
  @Override
  public abstract boolean isSecondNF(RelationSchema schema);

  @Override
  public abstract boolean isThirdNF(RelationSchema schema);

  @Override
  public abstract boolean isBCNF(RelationSchema schema);

  @Override
  public abstract NormalForm getNF(RelationSchema schema, ArrayList<FunctionalDependency> violatingFds);

  /**
   * Returns the number of matching items of two lists
   *
   * @param list1 the first list
   * @param list2 the second list
   * @return the number of matches
   */
  public int getNumberOfMatchingAttributes(ArrayList<Attribute> list1, ArrayList<Attribute> list2) {
    int numberOfMatches = 0;

    for (Attribute attr1 : list1) {
      for (Attribute attr2 : list2) {
        if (attr1.equals(attr2)) {
          numberOfMatches++;
          break;
        }
      }
    }

    return numberOfMatches;
  }

  /**
   * Returns if a given Attribute is a prime Attribute
   *
   * @param attribute     the attribute to be checked
   * @param candidateKeys the candidate Keys in which to look for the attribute
   * @return true if it is a prime attribute, false if not
   */
  public boolean isPrimeAttribute(Attribute attribute, ArrayList<Key> candidateKeys) {
    for (Key key : candidateKeys) {
      if (key.getAttributes().contains(attribute)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a ArrayList containing the minimal set of functional
   * dependencies
   *
   * @param fds functional dependencies to check
   * @return returns a ArrayList containing the minimal fd's
   */
  @Override
  public ArrayList<FunctionalDependency> getMinimalSetOfFds(ArrayList<FunctionalDependency> fds) {
    ArrayList<FunctionalDependency> minimalSet = new ArrayList<>();
    ArrayList<FunctionalDependency> tempSet = new ArrayList<>();

    // get a copy of the given fd's
    for (FunctionalDependency fd : fds) {
      tempSet.add(fd.getClone());
    }

    // make fd's cannonical
    // e.g. A ==> B,C gets split up to A==>B and A==>C
    for (FunctionalDependency fd : tempSet) {
      minimalSet.addAll(makeFdCannonical(fd));
    }

    // Remove unnecessary Attributes of Fd's
    // e.g. AB==>C, if A==>C holds, B is un
    minimalSet = removeUnneccessarySourceAttributes(minimalSet);

    // Remove redundant functional Dependencies
    // e.g. {B==>A, D==>A, B==>D} B==>A is redundant
    removeRedundantFunctionalDependencies(minimalSet);

    return minimalSet;
  }

  /**
   * Returns true/false if Set of FD's is cannonical
   *
   * @param setOfFDs to check
   * @return true / false
   */
  public boolean isSetOfFDsCannonical(ArrayList<FunctionalDependency> setOfFDs) {

    for (FunctionalDependency fd : setOfFDs) {
      if (makeFdCannonical(fd).size() > 1) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns the cannonical parts of a FD, or the FD if it was already
   * cannonical
   *
   * @param fd the FunctionalDependency to operate on
   * @return the fd if it was cannonical, or cannonical fd's
   */
  public ArrayList<FunctionalDependency> makeFdCannonical(FunctionalDependency fd) {
    ArrayList<FunctionalDependency> cannonicalFds = new ArrayList<>();
    ArrayList<Attribute> tempAttributes;
    cannonicalFds.add(fd);

    if (fd.getTargetAttributes().size() > 1) {

      for (Attribute targetAttribute : fd.getTargetAttributes()) {
        tempAttributes = new ArrayList<>();
        tempAttributes.add(targetAttribute);
        cannonicalFds.add(new FunctionalDependency(fd.getSourceAttributes(), tempAttributes));
      }

      cannonicalFds.remove(fd);
    }

    return cannonicalFds;
  }

  /**
   * Returns a ArrayList of FunctionalDependencies, no unnecessary
   * Attributes on the Left side
   *
   * @param fdList ArrayList of FunctionalDependencies to work with
   * @return a ArrayList of optimized FunctionalDependencies
   */
  public ArrayList<FunctionalDependency> removeUnneccessarySourceAttributes(ArrayList<FunctionalDependency> fdList) {
    ArrayList<FunctionalDependency> resultList = new ArrayList<>();
    for (FunctionalDependency fd : fdList) {
      if (fd.getSourceAttributes().size() == 1) {
        resultList.add(fd);
      } else {
        removeObsoleteLeftAttributes(fd, fdList);
        resultList.add(fd);
      }

    }
    return resultList;
  }

  /**
   * Removes obsolete Attributes from the left side of the fd
   *
   * @param fd  to optimize
   * @param fds all functional dependencies
   */
  private void removeObsoleteLeftAttributes(FunctionalDependency fd, ArrayList<FunctionalDependency> fds) {
    FunctionalDependency testFd;
    ArrayList<Attribute> toDelete = new ArrayList<>();

    for (Attribute attribute : fd.getSourceAttributes()) {
      testFd = fd.getClone();
      testFd.getSourceAttributes().remove(attribute);

      if (getClosure(fds, testFd.getSourceAttributes()).containsAll(testFd.getTargetAttributes())) {
        toDelete.add(attribute);
      }
    }

    // TODO: Refactoring needed
    // e.g. AB==>C A AND B can determine C independently
    if (toDelete.size() == fd.getSourceAttributes().size()) {
      return;
    }

    for (Attribute attribute : toDelete) {
      fd.getSourceAttributes().remove(attribute);
    }
  }

  /**
   * Removes redundant functional dependencies from the ArrayList
   *
   * @param fds ArrayList to clean up
   */
  @SuppressWarnings("unchecked")
  public void removeRedundantFunctionalDependencies(ArrayList<FunctionalDependency> fds) {
    ArrayList<FunctionalDependency> toDelete = new ArrayList<>();
    ArrayList<FunctionalDependency> allFds;

    for (FunctionalDependency fd : fds) {
      allFds = (ArrayList<FunctionalDependency>) fds.clone();
      allFds.removeAll(toDelete);
      allFds.remove(fd);

      if (getClosure(allFds, fd.getSourceAttributes()).containsAll(fd.getTargetAttributes())) {
        toDelete.add(fd);
      }
    }

    for (FunctionalDependency fd : toDelete) {
      fds.remove(fd);
    }
  }

  /**
   * Returns all subsets of a given ArrayList<Attribute>
   *
   * @param attributes the initial ArrayList of Attributes
   * @param resultList a empty ArrayList of ArrayLists
   * @return a ArrayList of ArrayLists, containing all Subsets
   */
  public ArrayList<ArrayList<Attribute>> getSubsetOfAttributes(ArrayList<Attribute> attributes,
                                                               ArrayList<ArrayList<Attribute>> resultList) {
    ArrayList<Attribute> tmpList;

    for (Attribute arr : attributes) {
      tmpList = new ArrayList<>();
      for (int cnt = 0; cnt < attributes.size(); cnt++) {
        if (attributes.get(cnt) != arr) {
          tmpList.add(attributes.get(cnt));
        }
      }
      if (!isListAlreadyPresent(tmpList, resultList) && !tmpList.isEmpty()) {
        resultList.add(tmpList);
      }
      if (tmpList.size() > 1) {
        getSubsetOfAttributes(tmpList, resultList);
      }
    }
    return resultList;
  }

  /**
   * Tells if a ArrayList with similar Values already exist
   *
   * @param listToTest the list with the values to search for
   * @param targetList the list to search in
   * @return true/false if ArrayList already exists / not exists
   */
  private boolean isListAlreadyPresent(ArrayList<Attribute> listToTest, ArrayList<ArrayList<Attribute>> targetList) {
    for (ArrayList<Attribute> list : targetList) {
      if (list.equals(listToTest)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a Key-Object, that contains all Attributes marked as
   * Primary Key
   *
   * @param schema schema which contains the Attributes
   * @return a Key-Object, representing the user-chosen Primary-Key
   */
  @Override
  public Key getPrimaryKey(RelationSchema schema) {
    Key primaryKey = new Key();

    for (Attribute attr : schema.getAttributes()) {
      if (attr.getIsPrimaryKey()) {
        primaryKey.getAttributes().add(attr);
      }
    }

    return primaryKey;
  }

  /**
   * Returns all candidate - keys
   *
   * @param schema schema to work with
   * @return all candidate-keys
   */
  @Override
  public ArrayList<Key> getAllCandidateKeys(RelationSchema schema) {
    ArrayList<Key> candidateKeys = new ArrayList<>();
    ArrayList<Attribute> l = new ArrayList<>();
    ArrayList<Attribute> m = new ArrayList<>();
    ArrayList<Attribute> n = new ArrayList<>();
    ArrayList<Attribute> rootNode = new ArrayList<>();
    ArrayList<ArrayList<Attribute>> level = new ArrayList<>();

    for (Attribute attr : schema.getAttributes()) {
      switch (getAttributePosition(attr, schema.getFunctionalDependencies())) {
        case ONLY_LEFT:
          l.add(attr);
          break;
        case LEFT_AND_RIGHT:
          m.add(attr);
          break;
        case NOT_EXISTING:
          n.add(attr);
          break;
      }
    }

    // Root-node = n AND l
    rootNode.addAll(n);
    rootNode.addAll(l);

    // If Root-node determines everything, were done here
    if (isKeyDeterminingEverything(schema.getAttributes(), schema.getFunctionalDependencies(), new Key(rootNode))) {
      candidateKeys.add(new Key(rootNode));
      return candidateKeys;
    }

    level.add(rootNode);
    candidateKeys = searchCandidateKeys(schema, m, candidateKeys, level);

    return candidateKeys;
  }

  public ArrayList<Attribute> getOnlyDeterminedAttributes(RelationSchema schema) {
    ArrayList<Attribute> r = new ArrayList<>();
    for (Attribute attr : schema.getAttributes()) {
      if (getAttributePosition(attr, schema.getFunctionalDependencies()) == AttributePosition.ONLY_RIGHT) {
        r.add(attr);
      }
    }
    return r;
  }

  /**
   * Recursively determines all candidate Keys
   *
   * @param schema        to work with
   * @param m             the Attributes that occur on the left and right side of
   *                      fds
   * @param candidateKeys a empty ArrayList
   * @param level         a set of ArrayLists containing Attributes
   * @return all candidate Keys
   */
  private ArrayList<Key> searchCandidateKeys(RelationSchema schema, ArrayList<Attribute> m,
                                             ArrayList<Key> candidateKeys, ArrayList<ArrayList<Attribute>> level) {
    ArrayList<ArrayList<Attribute>> nextLevel = new ArrayList<>();
    ArrayList<Attribute> tempList;

    for (ArrayList<Attribute> attrList : level) {
      if (isKeyDeterminingEverything(schema.getAttributes(), schema.getFunctionalDependencies(), new Key(attrList))) {
        if (!isSuperKey(attrList, candidateKeys)) {
          candidateKeys.add(new Key(attrList));
        }
      }
    }

    for (ArrayList<Attribute> attrLst : level) {
      if (!isCandidateKey(attrLst, candidateKeys) && !isSuperKey(attrLst, candidateKeys)) {
        for (Attribute attr : m) {
          if (!attrLst.contains(attr)) {
            tempList = new ArrayList<>();
            tempList.addAll(attrLst);
            tempList.add(attr);
            nextLevel.add(tempList);
          }
        }
      }
    }

    if (!nextLevel.isEmpty()) {
      searchCandidateKeys(schema, m, candidateKeys, nextLevel);
    }

    return candidateKeys;
  }

  /**
   * Returns the Position of the Attribute in the given fd's
   *
   * @param attribute attribute to be searched
   * @param fds       fds in which to look for the attribute
   * @return a AttributePosition Object, indicating the position
   */
  public AttributePosition getAttributePosition(Attribute attribute, ArrayList<FunctionalDependency> fds) {
    AttributePosition position = AttributePosition.NOT_EXISTING;

    for (FunctionalDependency fd : fds) {
      if (position == AttributePosition.LEFT_AND_RIGHT) {
        break;
      }

      if (fd.getSourceAttributes().contains(attribute)) {
        if (position == AttributePosition.ONLY_RIGHT) {
          position = AttributePosition.LEFT_AND_RIGHT;
        } else {
          position = AttributePosition.ONLY_LEFT;
        }
      }
      if (fd.getTargetAttributes().contains(attribute)) {
        if (position == AttributePosition.ONLY_LEFT) {
          position = AttributePosition.LEFT_AND_RIGHT;
        } else {
          position = AttributePosition.ONLY_RIGHT;
        }
      }
    }
    return position;
  }

  /**
   * Tells if a number of Attributes are a superKey
   *
   * @param keyToTest     the key to test
   * @param candidateKeys the candidateKeys to test against
   * @return true if it is a superKey, false if not
   */
  public boolean isSuperKey(ArrayList<Attribute> keyToTest, ArrayList<Key> candidateKeys) {
    for (Key key : candidateKeys) {
      if (keyToTest.containsAll(key.getAttributes())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Tells if a number of Attributes are a candidateKey
   *
   * @param keyToTest     the key to test
   * @param candidateKeys the candidateKeys to test against
   * @return true if it is a candidateKey, false if not
   */
  @Override
  public boolean isCandidateKey(ArrayList<Attribute> keyToTest, ArrayList<Key> candidateKeys) {
    for (Key key : candidateKeys) {
      if (key.getAttributes().equals(keyToTest)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns if a Key determines all Attributes of a given relation
   *
   * @param schema schema which contains the attributes
   * @param key    key under test
   * @return true/false if Key determines everything/or not
   */
  @Override
  public boolean isKeyDeterminingEverything(RelationSchema schema, Key key) {
    return isKeyDeterminingEverything(schema.getAttributes(), schema.getFunctionalDependencies(), key);
  }

  /**
   * Returns if a Key determines all Attributes of a given relation
   *
   * @param attributes attributes that must be determined
   * @param fds        functional dependencies to consider
   * @param key        key to be tested
   * @return true/false if key determines everything / or not
   */
  public boolean isKeyDeterminingEverything(ArrayList<Attribute> attributes, ArrayList<FunctionalDependency> fds,
                                            Key key) {
    ArrayList<Attribute> pkClosure = getClosure(fds, key.getAttributes());

    return pkClosure.containsAll(attributes);
  }

  /**
   * Checks if a given functional dependency is a Member of a schema
   *
   * @param schema schema to work with
   * @param fd     functional dependency to test
   * @return true/false if functional dependency is a member/ or not
   */
  public boolean isMember(RelationSchema schema, FunctionalDependency fd) {
    // FD X==>Y
    // IF Closure of X in schema contains Y ==> isMember
    ArrayList<Attribute> closureOfX = getClosure(schema, fd.getSourceAttributes());
    return closureOfX.containsAll(fd.getTargetAttributes());
  }

  /**
   * Returns the Closure of given Attributes
   *
   * @param schema                schema to work with
   * @param determiningAttributes Closure(determiningAttributes)
   * @return the Closure of given Attributes on specified schema
   */
  public ArrayList<Attribute> getClosure(RelationSchema schema, ArrayList<Attribute> determiningAttributes) {
    return getClosure(schema.getFunctionalDependencies(), determiningAttributes);
  }

  /**
   * Returns the Closure of given Attributes
   *
   * @param fds                   functional dependencies to consider
   * @param determiningAttributes determining attributes to work with
   * @return a ArrayList of determined Attributes
   */
  public ArrayList<Attribute> getClosure(ArrayList<FunctionalDependency> fds,
                                         ArrayList<Attribute> determiningAttributes) {
    ArrayList<Attribute> closure = new ArrayList<>();
    ArrayList<Attribute> oldClosure = new ArrayList<>();

    copyAttributes(determiningAttributes, closure);

    do {
      copyAttributes(closure, oldClosure);
      for (FunctionalDependency fd : fds) {
        if (closure.containsAll(fd.getSourceAttributes())) {
          copyAttributes(fd.getTargetAttributes(), closure);
        }
      }

    } while (!closure.equals(oldClosure));

    return closure;
  }

  /**
   * Returns if two Sets of Functional Dependencies are equivalent
   *
   * @param list1 first set of fd's to consider
   * @param list2 second set of fd's to consider
   * @return true if the sets are equivalent, false if not
   */
  @Override
  public boolean areFdSetsEquivalent(ArrayList<FunctionalDependency> list1, ArrayList<FunctionalDependency> list2) {
    // check if all fd's of list1 are in list2
    for (FunctionalDependency fd : list1) {
      if (!list2.contains(fd)) {
        if (!getClosure(list2, fd.getSourceAttributes()).containsAll(fd.getTargetAttributes())) {
          return false;
        }
      }
    }

    // check if all fd's of list2 are in list1
    for (FunctionalDependency fd : list2) {
      if (!list1.contains(fd)) {
        if (!getClosure(list1, fd.getSourceAttributes()).containsAll(fd.getTargetAttributes())) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Copies a set of Attributes to another set of Attributes
   *
   * @param sourceList the source of the operation
   * @param targetList the target of the operation
   */
  public void copyAttributes(ArrayList<Attribute> sourceList, ArrayList<Attribute> targetList) {
    for (Attribute attr : sourceList) {
      if (!targetList.contains(attr)) {
        targetList.add(attr);
      }
    }
  }
}
