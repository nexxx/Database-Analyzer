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

import java.io.Serializable;

/**
 * Class representing a ForeignKey-Constraint
 *
 * @author Sebastian Theuermann
 */
public final class ForeignKeyConstraint implements Serializable {

  public ForeignKeyConstraint() {
    super();
  }

  public ForeignKeyConstraint(String sourceRelationName, String sourceAttributeName, String targetRelationName,
                              String targetAttributeName) {
    super();
    this.sourceRelationName = sourceRelationName;
    this.sourceAttributeName = sourceAttributeName;
    this.targetRelationName = targetRelationName;
    this.targetAttributeName = targetAttributeName;
  }

  /**
   *
   */
  private static final long serialVersionUID = 3011555081376590937L;
  private String sourceRelationName;
  private String targetRelationName;
  private String targetAttributeName;
  private String sourceAttributeName;

  /**
   * @return the targetRelationName
   */
  public String getTargetRelationName() {
    return targetRelationName;
  }

  /**
   * @param targetRelationName the targetRelationName to set
   */
  public void setTargetRelationName(String targetRelationName) {
    this.targetRelationName = targetRelationName;
  }

  /**
   * @return the targetAttributeName
   */
  public String getTargetAttributeName() {
    return targetAttributeName;
  }

  /**
   * @param targetAttributeName the targetAttributeName to set
   */
  public void setTargetAttributeName(String targetAttributeName) {
    this.targetAttributeName = targetAttributeName;
  }

  /**
   * @return the sourceRelationName
   */
  public String getSourceRelationName() {
    return sourceRelationName;
  }

  /**
   * @param sourceRelationName the sourceRelationName to set
   */

  public void setSourceRelationName(String sourceRelationName) {
    this.sourceRelationName = sourceRelationName;
  }

  /**
   * @return the sourceAttributeName
   */
  public String getSourceAttributeName() {
    return sourceAttributeName;
  }

  /**
   * @param sourceAttributeName the sourceAttributeName to set
   */
  public void setSourceAttributeName(String sourceAttributeName) {
    this.sourceAttributeName = sourceAttributeName;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ForeignKeyConstraint) {
      ForeignKeyConstraint fk = (ForeignKeyConstraint) obj;
      if (fk.getSourceAttributeName().equals(sourceAttributeName)) {
        if (fk.getTargetRelationName().equals(targetRelationName)) {
          if (fk.getTargetAttributeName().equals(targetAttributeName)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public Object getClone() {
    ForeignKeyConstraint fk = new ForeignKeyConstraint();
    fk.sourceRelationName = sourceRelationName;
    fk.sourceAttributeName = sourceAttributeName;
    fk.targetRelationName = targetRelationName;
    fk.targetAttributeName = targetAttributeName;
    return fk;
  }

  @Override
  public String toString() {
    return sourceRelationName + "." + sourceAttributeName + "==>" + targetRelationName + "." + targetAttributeName;
  }

}
