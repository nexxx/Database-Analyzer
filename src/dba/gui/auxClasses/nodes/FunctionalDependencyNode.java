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

import dbaCore.data.FunctionalDependency;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents Functional Dependency Node in JTree Component
 *
 * @author Andreas Freitag
 */
public class FunctionalDependencyNode extends DefaultMutableTreeNode {
  /**
   *
   */
  private static final long serialVersionUID = 7566043665204414293L;

  /**
   * Parametric constructor
   *
   * @param fd FunctionalDependency which will be the node
   */
  public FunctionalDependencyNode(FunctionalDependency fd) {
    super(fd);
  }

}
