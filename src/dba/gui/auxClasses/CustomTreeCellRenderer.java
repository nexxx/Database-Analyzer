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

package dba.gui.auxClasses;

import dba.gui.auxClasses.nodes.AttributeNode;
import dba.gui.auxClasses.nodes.DatabaseNode;
import dba.gui.auxClasses.nodes.FunctionalDependencyNode;
import dba.gui.auxClasses.nodes.RelationNode;
import dba.utils.GetIcons;
import dba.utils.constants;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;


/**
 * Custom TreeCellRenderer to provide custom icons for Database,
 * Relations, Attributes and FDs
 *
 * @author Andreas Freitag
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer implements constants {

  private GetIcons getIcons;
  /**
   *
   */
  private static final long serialVersionUID = -2155183026454520506L;

  public CustomTreeCellRenderer() {
    super();
    getIcons = GetIcons.getInstance();
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    ImageIcon icon;
    if (value instanceof AttributeNode) {
      icon = getIcons.getTreeAttribute();
      setIcon(icon);
    } else if (value instanceof FunctionalDependencyNode) {
      icon = getIcons.getTreeFd();
      setIcon(icon);
    } else if (value instanceof RelationNode) {
      icon = getIcons.getTreeRelation();
      setIcon(icon);
    } else if (value instanceof DatabaseNode) {
      icon = getIcons.getTreeDatabase();
      setIcon(icon);
    }
    return this;
  }
}
