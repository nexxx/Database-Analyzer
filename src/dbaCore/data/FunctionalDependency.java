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
import java.util.ArrayList;

/**
 * Class representing a Functional dependency
 *
 * @author Sebastian Theuermann
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "fd")
public class FunctionalDependency implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 166324774376697454L;
  // source (determining set of attributes) ==> target (dependent set
  // of attributes)
  // X(source) ==> Y(target)
  private ArrayList<Attribute> sourceAttributes;
  private ArrayList<Attribute> targetAttributes;

  public FunctionalDependency() {
    super();
    sourceAttributes = new ArrayList<>();
    targetAttributes = new ArrayList<>();
  }

  public FunctionalDependency(ArrayList<Attribute> sourceAttributes, ArrayList<Attribute> targetAttributes) {
    this();
    this.sourceAttributes = sourceAttributes;
    this.targetAttributes = targetAttributes;
  }

  public ArrayList<Attribute> getSourceAttributes() {
    return sourceAttributes;
  }

  public void setSourceAttributes(ArrayList<Attribute> sourceAttributes) {
    this.sourceAttributes = sourceAttributes;
  }

  public void setTargetAttributes(ArrayList<Attribute> targetAttributes) {
    this.targetAttributes = targetAttributes;
  }

  public ArrayList<Attribute> getTargetAttributes() {
    return targetAttributes;
  }

  @Override
  public String toString() {
    return "[" + getStringOfAttributes(sourceAttributes) + "] -> [" + getStringOfAttributes(targetAttributes) + "]";
  }

  /**
   * Returns a String containing the Names of the given Attributes
   *
   * @param attributes the attributes to work with
   * @return a String with all Attributenames and delimiters
   */
  private String getStringOfAttributes(ArrayList<Attribute> attributes) {
    return Utilities.getStringFromArrayList(attributes);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FunctionalDependency) {
      FunctionalDependency fd = (FunctionalDependency) obj;
      if (this.sourceAttributes.equals(fd.getSourceAttributes())) {
        if (this.targetAttributes.equals(fd.getTargetAttributes())) {
          return true;
        }
      }
    }
    return false;
  }

  public FunctionalDependency getClone() {
    FunctionalDependency clone = new FunctionalDependency();

    for (Attribute sourceAttribute : sourceAttributes) {
      clone.getSourceAttributes().add((Attribute) sourceAttribute.getClone());
    }

    for (Attribute targetAttribute : targetAttributes) {
      clone.getTargetAttributes().add((Attribute) targetAttribute.getClone());
    }

    return clone;
  }
}
