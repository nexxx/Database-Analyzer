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

import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.TreeEnum;
import dba.utils.constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class which provides the PopupContextmenu for the JTree, inlcuding
 * all Listeners. This class is a Observable and notifies all
 * Observers when something has changed!
 *
 * @author Andreas Freitag
 */
public class FdPopupFactory implements constants {
  private CustomTree tree;
  private Localization locale;
  private ImageIcon iconEdit;
  private ImageIcon iconDelete;
  private FdLogic fdLogic;

  /**
   * Defaultconstructor
   *
   * @param jtree JTree with the DB
   */
  public FdPopupFactory(CustomTree jtree) {
    super();
    fdLogic = new FdLogic();
    tree = jtree;
    locale = Localization.getInstance();
    GetIcons getIcons = GetIcons.getInstance();
    iconEdit = getIcons.getPopupEdit();
    iconDelete = getIcons.getPopupDelete();
  }

  /**
   * Getter for the PopupMenu with all Listeners
   *
   * @return JPopupMenu for Database
   */
  public JPopupMenu getFdPopupMenu() {
    JPopupMenu fdPopUpMenu = new JPopupMenu();

    JMenuItem editFunctionalDependencyMenuItem = new JMenuItem(locale.getString("TREE_FDEdit"), iconEdit);
    editFunctionalDependencyMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.EditFd);
        fdLogic.editFd();
        tree.setSelectedItem(x);
      }
    });
    fdPopUpMenu.add(editFunctionalDependencyMenuItem);

    JMenuItem deleteFunctionalDependencyMenuItem = new JMenuItem(locale.getString("TREE_FDDelete"), iconDelete);
    deleteFunctionalDependencyMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.DelFD);
        fdLogic.deleteFd();
        tree.setSelectedItem(x);
      }
    });
    fdPopUpMenu.add(deleteFunctionalDependencyMenuItem);

    return fdPopUpMenu;
  }

}
