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

import dbaCore.data.Database;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents Database Node in JTree Component
 *
 * @author Andreas Freitag
 */
public class DatabaseNode extends DefaultMutableTreeNode {

  /**
   *
   */
  private static final long serialVersionUID = -3341868219612487768L;

  /**
   * Parametric constructor
   *
   * @param db Database which will be the node
   */
  public DatabaseNode(Database db) {
    super(db);
  }

  public void setDatabase(Database database) {
    userObject = database;
  }

}
