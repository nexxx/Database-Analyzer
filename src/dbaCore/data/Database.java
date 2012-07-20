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

import dbaCore.data.dBTypes.TypeEnum;
import dbaCore.data.events.Change;
import dbaCore.data.events.ChangeListener;
import dbaCore.data.events.Time;
import dbaCore.utils.Utilities;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * Class representing a Database
 *
 * @author Sebastian Theuermann & Andreas Freitag
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "database")
public class Database extends HistoricObject {

  private ArrayList<RelationSchema> database;
  private String custCompany;
  private String custAdress;
  private String notes;
  private ArrayList<Person> persons;
  private TypeEnum type;

  private ArrayList<ForeignKeyConstraint> foreignKeys;

  public Database() {
    super();
    database = new ArrayList<>();
    foreignKeys = new ArrayList<>();
    changeSupport.addChangeListener(new ChangeListener() {
      @Override
      public void Change(Change change) {
        if (change.getTime().equals(Time.AFTERCHANGE)) {
          removeInvalidForeignKeyReferences();
        }
      }
    });
    super.changeListener = new ChangeListener() {
      @Override
      public void Change(Change change) {
        changeSupport.fireChange(change.getTime());
      }
    };
    custCompany = "";
    custAdress = "";
    notes = "";
    persons = new ArrayList<>();
    type = TypeEnum.MYSQL;
  }

  @XmlElementWrapper(name = "relations")
  @XmlElement(name = "relation")
  public ArrayList<RelationSchema> getDatabase() {
    return database;
  }

  /**
   * Cleans up invalid FK-References and fires the afterChange-event
   */
  private void fireAfterChange(){
    removeInvalidForeignKeyReferences();
    changeSupport.fireAfterChange();
  }

  /**
   * Adds a RelationSchema to the database and adds
   * PropertyChangedListener
   *
   * @param schema to add to the database
   * @return true ==> success, false ==> fail
   */
  public boolean addRelationSchema(RelationSchema schema) {
    boolean added;
    changeSupport.fireBeforeChange();
    added = database.add(schema);
    schema.addChangeListener(changeListener);
    fireAfterChange();
    return added;

  }

  /**
   * Renames the RelationSchema and updates the ForeignKey-Constraints
   *
   * @param relation the relation to rename
   * @param newName  the new name for the relation
   */
  public void renameRelationSchema(RelationSchema relation, String newName) {
    if (database.contains(relation)) {
      changeSupport.fireBeforeChange();
      updateFkRelationNames(relation.getName(), newName);
      relation.setNameWithoutFiring(newName);
      fireAfterChange();
    }
  }

  /**
   * Renames a Attribute
   *
   * @param parentRelation the parentrelation of the attribute to rename
   * @param attribute      the attribute to rename
   * @param newName        the name name for the attribute
   */
  public void renameAttribute(RelationSchema parentRelation, Attribute attribute, String newName) {
    if (database.contains(parentRelation)) {
      if (parentRelation.getAttributes().contains(attribute)) {
        changeSupport.fireBeforeChange();
        updateFkAttributeNames(parentRelation.getName(), attribute.getName(), newName);
        parentRelation.renameAttributeWithoutFiring(attribute, newName);
        fireAfterChange();
      }
    }
  }

  /**
   * Replaces the targetRelation with the sourceRelation
   *
   * @param targetRelation the relation to replace
   * @param sourceRelation the new relation
   * @return true ==> success, false ==> fail
   */
  public boolean replaceRelationSchema(RelationSchema targetRelation, RelationSchema sourceRelation) {
    if (database.contains(targetRelation)) {
      changeSupport.fireBeforeChange();
      targetRelation.removeChangeListener(changeListener);
      sourceRelation.addChangeListener(changeListener);
      database.set(database.indexOf(targetRelation), sourceRelation);
      fireAfterChange();
      return true;
    }
    return false;

  }

  /**
   * Replaces the targetRelation with the sourceRelation
   *
   * @param targetRelation  the relation to replace
   * @param sourceRelations the new relations
   * @return true ==> success, false ==> fail
   */
  public boolean replaceRangeOfRelationSchemas(RelationSchema targetRelation,
                                               ArrayList<RelationSchema> sourceRelations) {
    if (database.contains(targetRelation)) {
      changeSupport.fireBeforeChange();
      targetRelation.removeChangeListener(changeListener);
      int index = database.indexOf(targetRelation);
      database.remove(index);
      for (RelationSchema rel : sourceRelations) {
        rel.addChangeListener(changeListener);
      }
      database.addAll(index, sourceRelations);
      fireAfterChange();
      return true;
    }
    return false;

  }

  /**
   * Replaces a old targetRelation with the results of a
   * normalization, including Relations and ForeignKeys
   *
   * @param targetRelation  the relation to replace
   * @param sourceRelations the results of a normalization
   * @param foreignKeys     the new foreignKeys
   * @return true for success, false ==> targetRelation not part of
   *         the db
   */
  public boolean insertNormalizationResult(RelationSchema targetRelation, ArrayList<RelationSchema> sourceRelations,
                                           ArrayList<ForeignKeyConstraint> foreignKeys) {
    if (database.contains(targetRelation)) {
      changeSupport.fireBeforeChange();
      replaceRangeOfRelationsWithoutFiring(targetRelation, sourceRelations);
      this.foreignKeys.addAll(foreignKeys);
      updateForeignKeyReferences(targetRelation, sourceRelations);
      fireAfterChange();
      return true;
    }

    return false;
  }

  /**
   * Updates the RelationNames in the ForeignKeyConstraints
   *
   * @param targetRelation  the old relation that gets replaced
   * @param sourceRelations the new relations that replace the old relation
   */
  private void updateForeignKeyReferences(RelationSchema targetRelation, ArrayList<RelationSchema> sourceRelations) {
    for (ForeignKeyConstraint fk : foreignKeys) {
      if (fk.getSourceRelationName().equals(targetRelation.getName())) {
        fk.setSourceRelationName(getNewRelationName(fk.getSourceRelationName(), fk.getSourceAttributeName(),
          sourceRelations));
      }
      if (fk.getTargetRelationName().equals(targetRelation.getName())) {
        fk.setTargetRelationName(getNewRelationName(fk.getTargetRelationName(), fk.getTargetAttributeName(),
          sourceRelations));
      }
    }
  }

  /**
   * Runs through all ForeignKeyConstraints and
   * removes those that contain invalid attributes or relations
   */
  private void removeInvalidForeignKeyReferences() {
    ArrayList<ForeignKeyConstraint> fksToDelete = new ArrayList<>();
    Attribute sourceAttribute, targetAttribute;

    for (ForeignKeyConstraint fk : foreignKeys) {
      sourceAttribute = getAttributeFromRelation(fk.getSourceRelationName(), fk.getSourceAttributeName());
      targetAttribute = getAttributeFromRelation(fk.getTargetRelationName(), fk.getTargetAttributeName());

      if (sourceAttribute == null || targetAttribute == null) {
        if (targetAttribute == null && sourceAttribute != null) {
          sourceAttribute.setIsForeignKeyWithoutFiring(false);
        }
        fksToDelete.add(fk);
      } else if (!targetAttribute.getIsPrimaryKey()) {
        sourceAttribute.setIsForeignKeyWithoutFiring(false);
        fksToDelete.add(fk);
      }else if(!sourceAttribute.getIsForeignKey()){
        fksToDelete.add(fk);
      }

    }

    foreignKeys.removeAll(fksToDelete);
  }

  /**
   * Returns a Attribute from a Relation by name
   *
   * @param relationName  the name of the relation that contains the attribute
   * @param attributeName the attribute to look for
   * @return returns the wanted attribute or null if it wasn't found
   */
  private Attribute getAttributeFromRelation(String relationName, String attributeName) {
    RelationSchema relation = getRelationSchema(relationName);
    Attribute attribute = null;
    if (relation != null) {
      attribute = relation.getAttributeByName(attributeName);
      if (attribute != null) {
        return attribute;
      }
    }
    return attribute;
  }

  /**
   * Looks inside the new relations for the attribute mentioned
   *
   * @param oldRelationName the name of the old relation
   * @param attributeName   the name of the attribute to look for
   * @param relations       the new relations that replace the old relation
   * @return returns the new RelationName if the attribute is found,
   *         or the oldRelationName if not
   */
  private String getNewRelationName(String oldRelationName, String attributeName, ArrayList<RelationSchema> relations) {
    for (RelationSchema relation : relations) {
      for (Attribute attr : relation.getAttributes()) {
        if (attr.getName().equals(attributeName)) {
          return relation.getName();
        }
      }
    }

    return oldRelationName;
  }

  /**
   * Replaces the targetRelation with the sourceRelation, no
   * ChangeEvent gets fired!
   *
   * @param targetRelation  the relation to replace
   * @param sourceRelations the new relation
   */
  public void replaceRangeOfRelationsWithoutFiring(RelationSchema targetRelation,
                                                   ArrayList<RelationSchema> sourceRelations) {
    targetRelation.removeChangeListener(changeListener);
    int index = database.indexOf(targetRelation);
    database.remove(index);
    for (RelationSchema rel : sourceRelations) {
      rel.addChangeListener(changeListener);
    }
    database.addAll(index, sourceRelations);
  }

  /**
   * Removes a RelationSchema from the database (also removes the
   * Listener)
   *
   * @param schema the relation to remove from the database
   * @return true ==> success, false ==> fail
   */
  public boolean removeRelationSchema(RelationSchema schema) {
    changeSupport.fireBeforeChange();
    schema.removeChangeListener(changeListener);
    boolean returnVal = database.remove(schema);
    fireAfterChange();
    return returnVal;
  }

  /**
   * Restores the references between the attributes and the fd's
   */
  public void restoreReferences() {
    for (RelationSchema schema : database) {
      schema.restoreReferences();
    }

  }

  public void initPropertyChangeListeners() {
    for (RelationSchema schema : database) {
      schema.initPropertyChangeListeners();
      schema.addChangeListener(changeListener);
    }
  }

  @Override
  public Database getClone() {
    Database dbClone = new Database();

    // clone Relations
    for (RelationSchema schema : database) {
      dbClone.addRelationSchema(schema.getClone());
    }

    // clone ForeignKeys
    for (ForeignKeyConstraint fk : foreignKeys) {
      dbClone.getForeignKeys().add((ForeignKeyConstraint) fk.getClone());
    }

    dbClone.restoreReferences();

    return dbClone;
  }

  /**
   * Returns the RelationSchema with the given Name / Index (starting
   * with 1)
   *
   * @param text search for text as Index or as Name if no number
   * @return the RelationSchema you are looking for, or null if not
   *         found
   */
  public RelationSchema getRelationSchema(String text) {
    Integer relationId = Utilities.tryParseInt(text);

    if (relationId != null) {
      return getRelationSchemaByIndex(relationId - 1);
    } else {
      return getRelationSchemaByName(text);
    }
  }

  /**
   * Returns the Relation at the given Index
   *
   * @param index of the relation to return
   * @return the RelationSchema you are looking for, or null if not
   *         found
   */
  public RelationSchema getRelationSchemaByIndex(Integer index) {
    if (index >= 0 && index < database.size()) {
      return database.get(index);
    }
    return null;
  }

  /**
   * Returns the Relation with the given Name
   *
   * @param name the name of the Relation to look for
   * @return the RelationSchema you are looking for, or null if not
   */
  public RelationSchema getRelationSchemaByName(String name) {
    RelationSchema resultRelation = null;
    for (RelationSchema relation : this.database) {
      if (relation.getName().equalsIgnoreCase(name)) {
        resultRelation = relation;
        break;
      }
    }
    return resultRelation;
  }

  /**
   * Returns the names of all existing relations
   *
   * @return Names of all relations existing in database
   */
  public ArrayList<String> getAllRelationNames() {
    ArrayList<String> relationNames = new ArrayList<>();
    for (RelationSchema relation : database) {
      relationNames.add(relation.getName());
    }
    return relationNames;
  }

  /**
   * @return the foreignKeys
   */
  @XmlElementWrapper(name = "foreignKeys")
  @XmlElement(name = "foreignKey")
  public ArrayList<ForeignKeyConstraint> getForeignKeys() {
    return foreignKeys;
  }

  /**
   * Removes a existent ForeignKey associated with this attribute
   */
  public void removeForeignKey(String sourceRelationName, String sourceAttributeName) {
    ArrayList<ForeignKeyConstraint> fksToDelete = new ArrayList<>();

    for (ForeignKeyConstraint fk : foreignKeys) {
      if (fk.getSourceRelationName().equals(sourceRelationName) && fk.getSourceAttributeName().equals
        (sourceAttributeName)) {
        fksToDelete.add(fk);
      }
    }
    foreignKeys.removeAll(fksToDelete);
  }

  /**
   * Replaces a old relation name with a new one
   *
   * @param oldRelationName the relationName to look for
   * @param newRelationName the new name for the relation
   */
  public void updateFkRelationNames(String oldRelationName, String newRelationName) {
    for (ForeignKeyConstraint fk : foreignKeys) {
      if (fk.getSourceRelationName().equals(oldRelationName)) {
        fk.setSourceRelationName(newRelationName);
      }
      if (fk.getTargetRelationName().equals(oldRelationName)) {
        fk.setTargetRelationName(newRelationName);
      }
    }
  }

  /**
   * Replace old AttributeNames with new ones
   *
   * @param relationName     the relatioName to look for
   * @param attributeName    the old name of the attribute inside the relation
   * @param newAttributeName the new name for the attribute
   */
  public void updateFkAttributeNames(String relationName, String attributeName, String newAttributeName) {
    for (ForeignKeyConstraint fk : foreignKeys) {
      if (fk.getSourceRelationName().equals(relationName)) {
        if (fk.getSourceAttributeName().equals(attributeName)) {
          fk.setSourceAttributeName(newAttributeName);
        }
      } else if (fk.getTargetRelationName().equals(relationName)) {
        if (fk.getTargetAttributeName().equals(attributeName)) {
          fk.setTargetAttributeName(newAttributeName);
        }
      }
    }
  }

  @Override
  public String toString() {
    return "Database";
  }

  /**
   * @return the custCompany
   */
  public String getCustCompany() {
    return custCompany;
  }

  /**
   * @param custCompany the custCompany to set
   */
  public void setCustCompany(String custCompany) {
    this.custCompany = custCompany;
  }

  /**
   * @return the custAdress
   */
  public String getCustAdress() {
    return custAdress;
  }

  /**
   * @param custAdress the custAdress to set
   */
  public void setCustAdress(String custAdress) {
    this.custAdress = custAdress;
  }

  /**
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * @param notes the notes to set
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * @return the persons
   */
  public ArrayList<Person> getPersons() {
    return persons;
  }

  /**
   * @param persons the persons to set
   */
  public void setPersons(ArrayList<Person> persons) {
    this.persons = persons;
  }

  /**
   * @return The Databasetype
   */
  public TypeEnum getType() {
    return type;
  }

  /**
   * @param type database type
   */
  public void setType(TypeEnum type) {
    this.type = type;
  }
}

