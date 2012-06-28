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

import data.NormalForm;
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
public class RelPopupFactory implements constants {
  private CustomTree tree;
  private Localization locale;
  private ImageIcon iconAddAttr;
  private ImageIcon iconAddFd;
  private ImageIcon iconEdit;
  private ImageIcon iconDelete;
  private ImageIcon iconRename;
  private ImageIcon iconInspect;
  private ImageIcon icon2NF;
  private ImageIcon icon3NF;
  private ImageIcon iconBCNF;
  private JMenuItem secondNormalFormRelationMenuItem;
  private JMenuItem thirdNormalFormRelationMenuItem;
  private JMenu optimizeRelationMenu;
  private RelationLogic relLogic;
  private JMenuItem functionalDependencyMenuItem;

  /**
   * Defaultconstructor
   *
   * @param jtree JTree with the DB
   */
  public RelPopupFactory(CustomTree jtree) {
    super();
    relLogic = new RelationLogic();
    tree = jtree;
    locale = Localization.getInstance();
    GetIcons getIcons = GetIcons.getInstance();
    iconAddAttr = getIcons.getPopupAttribute();
    iconAddFd = getIcons.getButtonFd();
    iconEdit = getIcons.getPopupEdit();
    iconDelete = getIcons.getPopupDelete();
    iconRename = getIcons.getPopupRename();
    iconInspect = getIcons.getPopupInspect();
    icon2NF = getIcons.getPopupOptimize2NF();
    icon3NF = getIcons.getPopupOptimize3NF();
    iconBCNF = getIcons.getPopupOptimizeBCNF();
  }

  /**
   * Getter for the PopupMenu with all Listeners
   *
   * @return JPopupMenu for RelationSchemes
   */
  public JPopupMenu getRelPopupMenu() {

    JPopupMenu relPopUpMenu = new JPopupMenu();

    JMenu addRelationMenu = new JMenu(locale.getString("Add"));
    relPopUpMenu.add(addRelationMenu);

    JMenuItem attributeRelationMenuItem = new JMenuItem(locale.getString("Attribute"), iconAddAttr);
    attributeRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.AddAttribute);
        relLogic.addAttribute();
        tree.setSelectedItem(x);
      }
    });
    addRelationMenu.add(attributeRelationMenuItem);

    functionalDependencyMenuItem = new JMenuItem(locale.getString("FunctionalDependency"), iconAddFd);
    functionalDependencyMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.AddFD);
        relLogic.addFd();
        tree.setSelectedItem(x);
      }
    });
    addRelationMenu.add(functionalDependencyMenuItem);

    JMenuItem wizardRelationMenuItem = new JMenuItem(locale.getString("TREE_RelEditWizard"), iconEdit);
    wizardRelationMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.EditRel);
        relLogic.editRelationWizard();
        tree.setSelectedItem(x);
      }
    });
    relPopUpMenu.add(wizardRelationMenuItem);

    JMenuItem deleteRelationMenuItem = new JMenuItem(locale.getString("Delete"), iconDelete);
    deleteRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.DelRelation);
        relLogic.deleteRelation();
        tree.setSelectedItem(x);
      }
    });
    relPopUpMenu.add(deleteRelationMenuItem);

    JMenuItem renameRelationMenuItem = new JMenuItem(locale.getString("Rename"), iconRename);
    renameRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.RenameRel);
        relLogic.renameRelation();
        tree.setSelectedItem(x);
      }
    });
    relPopUpMenu.add(renameRelationMenuItem);

    JMenuItem inspectRelationMenuItem = new JMenuItem(locale.getString("TREE_RelInspect"), iconInspect);
    inspectRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.InspectRel);
        relLogic.inspectRelation();
        tree.setSelectedItem(x);
      }
    });
    relPopUpMenu.add(inspectRelationMenuItem);

    optimizeRelationMenu = new JMenu(locale.getString("Optimize"));
    relPopUpMenu.add(optimizeRelationMenu);

    secondNormalFormRelationMenuItem = new JMenuItem(locale.getString("TREE_Rel2NF"), icon2NF);
    secondNormalFormRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
        relLogic.optimizeRelation(NormalForm.SECOND);
        tree.setSelectedItem(x);
      }
    });
    optimizeRelationMenu.add(secondNormalFormRelationMenuItem);

    thirdNormalFormRelationMenuItem = new JMenuItem(locale.getString("TREE_Rel3NF"), icon3NF);
    thirdNormalFormRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
        relLogic.optimizeRelation(NormalForm.THIRD);
        tree.setSelectedItem(x);
      }
    });
    optimizeRelationMenu.add(thirdNormalFormRelationMenuItem);

    JMenuItem boyceCoddNormalFormRelationMenuItem = new JMenuItem(locale.getString("TREE_RelBCNF"), iconBCNF);
    boyceCoddNormalFormRelationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
        relLogic.optimizeRelation(NormalForm.BOYCECODD);
        tree.setSelectedItem(x);
      }
    });
    optimizeRelationMenu.add(boyceCoddNormalFormRelationMenuItem);

    return relPopUpMenu;
  }

  public void setEnabledOpti2NF(boolean enabled) {
    secondNormalFormRelationMenuItem.setEnabled(enabled);
  }

  public void setEnabledOpti3NF(boolean enabled) {
    thirdNormalFormRelationMenuItem.setEnabled(enabled);
  }

  public void setEnabledOpti(boolean enabled) {
    optimizeRelationMenu.setEnabled(enabled);
  }

  public void setEnabledFD(boolean enabled) {
    functionalDependencyMenuItem.setEnabled(enabled);
  }
}
