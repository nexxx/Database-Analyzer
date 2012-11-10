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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Class representing a Attribute
 *
 * @author Sebastian Theuermann
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "attribute")
public class Attribute extends HistoricObject implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 8605668187166117214L;
  private String name;
  private boolean isPrimaryKey;
  private boolean isForeignKey;
  private String constraints;

  public Attribute() {
    super();
    name = "newAttribute";
    isPrimaryKey = false;
    isForeignKey = false;
    constraints = "";
  }

  public Attribute(String name) {
    this();
    setName(name);
  }

  public Attribute(String name, boolean isPrimaryKey, boolean isForeignKey) {
    this(name);
    this.isPrimaryKey = isPrimaryKey;
    this.isForeignKey = isForeignKey;
  }

  public String getName() {
    return name;
  }

  public String getNameWithKeyNotation() {
    String returnString = name;
    if (isPrimaryKey) {
      returnString = returnString + "<pk>";
    }
    if (isForeignKey) {
      returnString = returnString + "<fk>";
    }
    if (!constraints.isEmpty()) {
      returnString = returnString + " [" + constraints + "]";
    }
    return returnString;
  }

  public void setName(String name) {
    changeSupport.fireBeforeChange();
    this.name = name;
    changeSupport.fireAfterChange();
  }

  /**
   * Assigns the new name without firing the changeEvent!
   *
   * @param name the new name of the Attribute
   */
  public void setNameWithoutFiring(String name) {
    if (!name.equals(this.name)) {
      this.name = name;
    }
  }

  // PrimaryKey
  public void setIsPrimaryKey(boolean isPrimaryKey) {
    changeSupport.fireBeforeChange();
    this.isPrimaryKey = isPrimaryKey;
    changeSupport.fireAfterChange();
  }

  public boolean getIsPrimaryKey() {
    return isPrimaryKey;
  }

  public void setIsForeignKey(boolean isForeignKey) {
    changeSupport.fireBeforeChange();
    this.isForeignKey = isForeignKey;
    changeSupport.fireAfterChange();
  }

  public void setIsForeignKeyWithoutFiring(boolean isForeignKey) {
    this.isForeignKey = isForeignKey;
  }

  public boolean getIsForeignKey() {
    return isForeignKey;
  }

  @Override
  public String toString() {
    return getNameWithKeyNotation();
  }

  @Override
  public Object getClone() {
    Attribute clone = new Attribute(name);
    clone.setConstraints(constraints);
    clone.setIsPrimaryKey(isPrimaryKey);
    clone.setIsForeignKey(isForeignKey);
    return clone;
  }

  @Override
  public boolean equals(Object otherObject) {
    if (otherObject instanceof Attribute) {
      Attribute otherAttribute = (Attribute) otherObject;
      // Name
      if (!name.equals(otherAttribute.getName())) {
        return false;
      }
      // PK
      if (isPrimaryKey != otherAttribute.getIsPrimaryKey()) {
        return false;
      }
      // FK
      if (isForeignKey != otherAttribute.getIsForeignKey()) {
        return false;
      }
    }
    // Not a Attribute
    else {
      return false;
    }

    return true;
  }

  public String getConstraints() {
    return constraints;
  }

  public void setConstraints(String constraints) {
    changeSupport.fireBeforeChange();
    this.constraints = constraints;
    changeSupport.fireAfterChange();
  }
}
