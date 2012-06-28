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

package dba.gui.auxClasses.nodes;

import data.Attribute;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents Attribute Node in JTree Component
 *
 * @author Andreas Freitag
 */
public class AttributeNode extends DefaultMutableTreeNode {
  /**
   *
   */
  private static final long serialVersionUID = 6106415129286467781L;

  /**
   * Parametric constructor
   *
   * @param attr Attribute which will be the node
   */
  public AttributeNode(Attribute attr) {
    super(attr);
  }

}
