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
public class DbPopupFactory implements constants {
  private CustomTree tree;
  private Localization locale;
  private ImageIcon iconWizard;
  private ImageIcon iconRelation;
  private ImageIcon iconInspect;
  private DatabaseLogic dbLogic;
  private JMenuItem inspectDatabaseMenuItem;

  /**
   * Defaultconstructor
   *
   * @param jtree JTree with the DB
   */
  public DbPopupFactory(CustomTree jtree) {
    super();
    tree = jtree;
    locale = Localization.getInstance();
    GetIcons getIcon = GetIcons.getInstance();
    iconWizard = getIcon.getPopupWizard();
    iconRelation = getIcon.getPopupRelation();
    iconInspect = getIcon.getPopupInspect();
    dbLogic = new DatabaseLogic();

  }

  /**
   * Getter for the PopupMenu with all Listeners
   *
   * @return JPopupMenu for Database
   */
  public JPopupMenu getDbPopupMenu() {
    JPopupMenu dbPopUpMenu = new JPopupMenu();

    JMenuItem wizardDatabaseMenuItem = new JMenuItem(locale.getString("TREE_DBNewRelWiz"), iconWizard);
    wizardDatabaseMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
        dbLogic.newWizardRelation();
        tree.setSelectedItem(x);
      }
    });
    dbPopUpMenu.add(wizardDatabaseMenuItem);

    JMenuItem addDatabaseMenuItem = new JMenuItem(locale.getString("TREE_DBAddEmptyRel"), iconRelation);
    addDatabaseMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
        dbLogic.newEmptyRelation();
        tree.setSelectedItem(x);
      }
    });
    dbPopUpMenu.add(addDatabaseMenuItem);

    inspectDatabaseMenuItem = new JMenuItem(locale.getString("TREE_DBInspect"), iconInspect);
    inspectDatabaseMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.InspectDb);
        dbLogic.inspectRelation();
        tree.setSelectedItem(x);
      }
    });
    dbPopUpMenu.add(inspectDatabaseMenuItem);

    return dbPopUpMenu;

  }

  /**
   * Enable/Disable the inspect button
   *
   * @param enabled true/false
   */
  public void setEnabledInspect(boolean enabled) {
    inspectDatabaseMenuItem.setEnabled(enabled);
  }
}
