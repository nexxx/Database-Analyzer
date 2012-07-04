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

import dbaCore.utils.Utilities;

import java.util.ArrayList;

/**
 * List of Attributes representing a Key
 *
 * @author Sebastian Theuermann
 */
public class Key {
  private ArrayList<Attribute> attributes;

  public Key() {
    super();
    attributes = new ArrayList<>();
  }

  public Key(ArrayList<Attribute> attributes) {
    super();
    this.attributes = attributes;
  }

  public ArrayList<Attribute> getAttributes() {
    return attributes;
  }

  @Override
  public String toString() {
    return "(" + Utilities.getStringFromArrayList(attributes) + ")";
  }
}
