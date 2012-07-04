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

import dbaCore.data.events.Change;
import dbaCore.data.events.ChangeListener;
import dbaCore.utils.Utilities;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing a Relation
 *
 * @author Sebastian Theuermann
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "relation")
@XmlType(propOrder = {"name", "attributes", "functionalDependencies"})
public class RelationSchema extends HistoricObject implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = -3301439448650204024L;
  private String name;
  private ArrayList<Attribute> attributes;
  private ArrayList<FunctionalDependency> functionalDependencies;
  private static int id = 0;
  private int ownId;

  public RelationSchema() {
    super();
    ownId = id++;
    attributes = new ArrayList<>();
    functionalDependencies = new ArrayList<>();
    super.changeListener = new ChangeListener() {
      @Override
      public void Change(Change change) {
        changeSupport.fireChange(change.getTime());
      }
    };
  }

  public RelationSchema(String name) {
    this();
    this.name = name;
  }

  public RelationSchema(String name, ArrayList<Attribute> attributes) {
    this(name);
    this.attributes = attributes;
  }

  public RelationSchema(String name, ArrayList<Attribute> attributes, ArrayList<FunctionalDependency>
    functionalDependencies) {
    this(name, attributes);
    this.functionalDependencies = functionalDependencies;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (!name.equals(this.name)) {
      changeSupport.fireBeforeChange();
      this.name = name;
      changeSupport.fireAfterChange();
    }
  }

  /**
   * Assigns the new name without firing the changeEvent!
   *
   * @param name the new name for the RelationSchema
   */
  public void setNameWithoutFiring(String name) {
    if (!name.equals(this.name)) {
      this.name = name;
    }
  }

  /**
   * Renames a given Attribute without firing Change
   *
   * @param attribute the Attribute to rename
   * @param newName   the new name for the attribute
   */
  public void renameAttributeWithoutFiring(Attribute attribute, String newName) {
    if (attributes.contains(attribute)) {
      attribute.setNameWithoutFiring(newName);
    }
  }

  public void setOwnId(int ownId) {
    this.ownId = ownId;
  }

  @XmlTransient
  public int getOwnId() {
    return ownId;
  }

  @XmlElementWrapper(name = "attributes")
  @XmlElement(name = "attribute")
  public ArrayList<Attribute> getAttributes() {
    return attributes;
  }

  /**
   * Returns a array containing the names of all Attributes of the
   * relation
   *
   * @return a array of the attribute-names
   */
  public String[] getAttributesNameArray() {
    String[] attrArray = new String[attributes.size()];

    int i = 0;
    for (Attribute attr : attributes) {
      attrArray[i++] = attr.getName();
    }

    return attrArray;
  }

  /**
   * Returns a Attribute of the given name
   *
   * @param name the name of the attribute to look for
   * @return the attribute with the given name
   */
  public Attribute getAttributeByName(String name) {

    for (Attribute attr : attributes) {
      if (attr.getName().equals(name)) {
        return attr;
      }
    }
    return null;
  }

  @XmlElementWrapper(name = "functionaldependencies")
  @XmlElement(name = "fd")
  public ArrayList<FunctionalDependency> getFunctionalDependencies() {
    return functionalDependencies;
  }

  public void setAttributes(ArrayList<Attribute> attributes) {
    changeSupport.fireBeforeChange();
    this.attributes = attributes;
    changeSupport.fireAfterChange();
  }

  public void removeAttribute(Attribute attribute) {
    changeSupport.fireBeforeChange();
    attribute.removeChangeListener(changeListener);
    attributes.remove(attribute);
    updateFunctionalDependencies();
    changeSupport.fireAfterChange();
  }

  public void setFunctionalDependencies(ArrayList<FunctionalDependency> functionalDependencies) {
    changeSupport.fireBeforeChange();
    this.functionalDependencies = functionalDependencies;
    changeSupport.fireAfterChange();
  }

  public boolean addFunctionalDependency(FunctionalDependency dependency) {
    for (FunctionalDependency fd : functionalDependencies) {
      if (fd.equals(dependency)) {
        return false;
      }
    }
    changeSupport.fireBeforeChange();
    functionalDependencies.add(dependency);
    changeSupport.fireAfterChange();
    return true;
  }

  public boolean addAttribute(String name) {
    for (Attribute attribute : attributes) {
      if (attribute.getName().equalsIgnoreCase(name)) {
        return false;
      }
    }
    return addAttribute(new Attribute(name));
  }

  public boolean addAttribute(Attribute attribute) {
    if (!attributes.contains(attribute)) {
      attribute.addChangeListener(changeListener);
      changeSupport.fireBeforeChange();
      attributes.add(attribute);
      changeSupport.fireAfterChange();
      return true;
    }
    return false;
  }

  public void removeFunctionalDependency(FunctionalDependency dependency) {
    changeSupport.fireBeforeChange();
    functionalDependencies.remove(dependency);
    changeSupport.fireAfterChange();
  }

  public void updateFunctionalDependencies() {
    ArrayList<FunctionalDependency> toDelete = new ArrayList<>();

    for (FunctionalDependency fd : functionalDependencies) {
      cleanReferences(fd.getSourceAttributes());
      cleanReferences(fd.getTargetAttributes());

      if (fd.getSourceAttributes().isEmpty() || fd.getTargetAttributes().isEmpty()) {
        toDelete.add(fd);
      }
    }

    for (FunctionalDependency fd : toDelete) {
      removeFunctionalDependency(fd);
    }
  }

  /**
   * Removes zombie-Attributes from the functionalDependencies
   *
   * @param list the Attributes to check
   */
  private void cleanReferences(ArrayList<Attribute> list) {
    ArrayList<Attribute> toDelete = new ArrayList<>();

    for (Attribute item : list) {
      if (!isAttributeExisting(item)) {
        toDelete.add(item);
      }
    }

    for (Attribute attr : toDelete) {
      list.remove(attr);
    }
  }

  /**
   * Returns if a given Attribute exists in the Relation
   *
   * @param attribute the Attribute to check
   * @return true if it exists, false if not
   */
  private boolean isAttributeExisting(Attribute attribute) {
    for (Attribute attr : attributes) {
      if (attr == attribute) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return name + " (" + Utilities.getStringFromArrayList(attributes) + ")";
  }

  @Override
  public RelationSchema getClone() {
    RelationSchema clone;

    ArrayList<Attribute> clonesAttributes = new ArrayList<>();
    ArrayList<FunctionalDependency> clonesFunctionalDependencies = new ArrayList<>();

    for (Attribute attribute : attributes) {
      clonesAttributes.add((Attribute) attribute.getClone());
    }

    for (FunctionalDependency dependency : functionalDependencies) {
      clonesFunctionalDependencies.add(dependency.getClone());
    }

    clone = new RelationSchema(name, clonesAttributes, clonesFunctionalDependencies);

    clone.setOwnId(ownId);

    clone.restoreReferences();
    clone.initPropertyChangeListeners();

    clone.setDirty(super.isDirty());

    return clone;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof RelationSchema) {
      RelationSchema otherSchema = (RelationSchema) object;

      if (!name.equals(otherSchema.getName())) {
        return false;
      }

      if (!attributes.equals(otherSchema.getAttributes())) {
        return false;
      }

      if (!functionalDependencies.equals(otherSchema.getFunctionalDependencies())) {
        return false;
      }

    }

    return true;
  }

  /**
   * Initializes Attribute-ChangeListeners by adding them to the
   * Attributes of the relation
   */
  public void initPropertyChangeListeners() {
    for (Attribute attribute : attributes) {
      attribute.addChangeListener(changeListener);
    }
  }

  /**
   * Restores the References between the FunctionalDependencies and
   * the Attributes of the relation
   */
  public void restoreReferences() {
    for (FunctionalDependency fd : getFunctionalDependencies()) {
      for (int i = 0; i < fd.getSourceAttributes().size(); i++) {
        fd.getSourceAttributes().set(i, getAttributeByName(fd.getSourceAttributes().get(i).getName()));
      }
      for (int i = 0; i < fd.getTargetAttributes().size(); i++) {
        fd.getTargetAttributes().set(i, getAttributeByName(fd.getTargetAttributes().get(i).getName()));
      }
    }
  }

}
