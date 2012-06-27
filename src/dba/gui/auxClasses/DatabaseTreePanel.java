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

import data.*;
import dba.gui.auxClasses.nodes.AttributeNode;
import dba.gui.auxClasses.nodes.DatabaseNode;
import dba.gui.auxClasses.nodes.FunctionalDependencyNode;
import dba.gui.auxClasses.nodes.RelationNode;
import dba.utils.Localization;
import logic.Analysis.GeneralRelationCheck;
import logic.Analysis.RelationUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Class to provide the JTree representation of the Database. This
 * class is also a Observer and reacts to changes on its Observables.
 *
 * @author Andreas Freitag
 */
public class DatabaseTreePanel extends JPanel {

  /**
   *
   */
  private static final long serialVersionUID = -8351937533511287535L;

  private Database database;
  private CustomTree tree;
  private DatabaseNode root;
  private JPopupMenu dbPopUpMenu;
  private JPopupMenu relPopUpMenu;
  private JPopupMenu attrPopUpMenu;
  private JPopupMenu fdPopUpMenu;
  private DbPopupFactory dbPopupFactory;
  private RelPopupFactory relPopupFactory;
  private AttrPopupFactory attrPopupFactory;

  private JLabel lblNormalFormDb;
  private JLabel lblNormalFormRel;
  private Localization locale;
  private GeneralRelationCheck checker;

  /**
   * Defaultconstructor to create the panel.
   */
  public DatabaseTreePanel(Database db) {
    super();
    locale = Localization.getInstance();
    setLayout(new BorderLayout());
    database = db;
    checker = new GeneralRelationCheck();

    root = new DatabaseNode(database);
    DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
    tree = CustomTree.getInstance();
    tree.setModel(defaultTreeModel);
    CustomTreeCellRenderer renderer = new CustomTreeCellRenderer();
    tree.setCellRenderer(renderer);
    tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
    root.setAllowsChildren(true);

    tree.expandPath(new TreePath(root.getPath()));
    JScrollPane scrollPane = new JScrollPane(tree);
    add(scrollPane, BorderLayout.CENTER);

    JPanel pnlNormalForm = new JPanel(new BorderLayout());
    lblNormalFormDb = new JLabel();
    pnlNormalForm.add(lblNormalFormDb, BorderLayout.SOUTH);
    lblNormalFormRel = new JLabel();
    pnlNormalForm.add(lblNormalFormRel, BorderLayout.NORTH);
    add(pnlNormalForm, BorderLayout.SOUTH);

    updateTree();

    dbPopupFactory = new DbPopupFactory(tree);
    dbPopUpMenu = dbPopupFactory.getDbPopupMenu();

    relPopupFactory = new RelPopupFactory(tree);
    relPopUpMenu = relPopupFactory.getRelPopupMenu();

    attrPopupFactory = new AttrPopupFactory(tree);
    attrPopUpMenu = attrPopupFactory.getAttrPopupMenu();

    FdPopupFactory fdPopupFactory = new FdPopupFactory(tree);
    fdPopUpMenu = fdPopupFactory.getFdPopupMenu();

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
        // System.out.println("JTree Item selected!");

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                .getLastSelectedPathComponent();

        if (node instanceof DatabaseNode) {
          DatabaseTreePanel.this.firePropertyChange("TreeClick", null,
                  "Database");
          if (tree.getDatabase().getDatabase().isEmpty()) {
            dbPopupFactory.setEnabledInspect(false);
          } else {
            dbPopupFactory.setEnabledInspect(true);
          }
          addRightClickPopUpMenu(tree);
        } else if (node instanceof RelationNode) {
          DatabaseTreePanel.this.firePropertyChange("TreeClick", null,
                  "Relation");
          enableOptimizeButtons();
          if (tree.getRelation().getAttributes().size() < 2) {
            relPopupFactory.setEnabledFD(false);
          } else {
            relPopupFactory.setEnabledFD(true);
          }
          addRightClickPopUpMenu(tree);
        } else if (node instanceof AttributeNode) {
          DatabaseTreePanel.this.firePropertyChange("TreeClick", null,
                  "Attribute");
          attrPopupFactory.updateElements();
          addRightClickPopUpMenu(tree);
        } else if (node instanceof FunctionalDependencyNode) {
          DatabaseTreePanel.this.firePropertyChange("TreeClick", null, "FD");
          addRightClickPopUpMenu(tree);
        }

        updateInspectLabels();

      }
    });
  }

  private void enableOptimizeButtons() {
    NormalForm currentNF = checker.getNF(tree.getRelation(),
            new ArrayList<FunctionalDependency>());
    if (currentNF != NormalForm.SECOND && currentNF != NormalForm.THIRD
            && currentNF != NormalForm.BOYCECODD) {
      relPopupFactory.setEnabledOpti2NF(true);
    } else {
      relPopupFactory.setEnabledOpti2NF(false);
    }

    if (currentNF != NormalForm.THIRD && currentNF != NormalForm.BOYCECODD) {
      relPopupFactory.setEnabledOpti3NF(true);
    } else {
      relPopupFactory.setEnabledOpti3NF(false);
    }

    if (currentNF != NormalForm.BOYCECODD) {
      relPopupFactory.setEnabledOpti(true);
    } else {
      relPopupFactory.setEnabledOpti(false);
    }
  }

  private void updateInspectLabels() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
            .getLastSelectedPathComponent();

    String nfTextDb = getNFStringDb();
    lblNormalFormDb.setText(nfTextDb);
    String nfTextRel = getNFStringRel(node);
    lblNormalFormRel.setText(nfTextRel);
  }

  private void addRightClickPopUpMenu(Component component) {
    component.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
        tree.setSelectedItem(row);
        if (e.isPopupTrigger()) {
          showPopUpMenu(e);
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
        tree.setSelectedItem(row);
        if (e.isPopupTrigger()) {
          showPopUpMenu(e);
        }
      }

      private void showPopUpMenu(MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        if ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent() instanceof DatabaseNode) {
          popupMenu = dbPopUpMenu;
        } else if ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent() instanceof RelationNode) {
          popupMenu = relPopUpMenu;
        } else if ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent() instanceof AttributeNode) {
          attrPopupFactory.updateElements();
          popupMenu = attrPopUpMenu;
        } else if ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent() instanceof FunctionalDependencyNode) {
          popupMenu = fdPopUpMenu;
        } else {
          // Must not happen
        }
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    });
  }

  /**
   * Update tree and reload all elements from database
   */
  public void updateTree() {
    root.removeAllChildren();
    database.restoreReferences();
    root.setDatabase(database);
    RelationNode relationNode;
    for (RelationSchema rel : database.getDatabase()) {
      relationNode = new RelationNode(rel);

      for (Attribute attr : rel.getAttributes()) {
        AttributeNode node = new AttributeNode(attr);
        relationNode.add(node);
      }

      for (FunctionalDependency fd : rel.getFunctionalDependencies()) {
        FunctionalDependencyNode node = new FunctionalDependencyNode(fd);
        relationNode.add(node);
      }

      root.add(relationNode);
    }
    tree.restoreExpansionState();
    updateInspectLabels();
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  private String getNFStringRel(DefaultMutableTreeNode node) {
    String normalForm;
    GeneralRelationCheck checker = new GeneralRelationCheck();

    if (node instanceof RelationNode) {
      RelationSchema relation = tree.getRelation();
      if (relation.getAttributes().isEmpty()) {
        return "";
      }
      normalForm = RelationUtils.getInstance().getNormalFormText(
              checker.getNF(relation, new ArrayList<FunctionalDependency>()));
    } else if (node instanceof AttributeNode
            || node instanceof FunctionalDependencyNode) {
      RelationSchema relation = tree.getParentRelation();
      if (relation.getAttributes().isEmpty()) {
        return "";
      }
      normalForm = RelationUtils.getInstance().getNormalFormText(
              checker.getNF(relation, new ArrayList<FunctionalDependency>()));
    } else {
      return "";
    }
    return locale.getString("TREE_Relation") + ": " + normalForm;
  }

  private String getNFStringDb() {
    String normalForm;
    GeneralRelationCheck checker = new GeneralRelationCheck();

    Database database = tree.getDatabase();
    if (database.getDatabase().isEmpty()) {
      return "";
    }
    normalForm = RelationUtils.getInstance().getNormalFormText(
            checker.getNF(database.getDatabase()));

    return locale.getString("TREE_Database") + ": " + normalForm;
  }

  /**
   * Clear selection in tree
   */
  public void disselectItems() {
    tree.clearSelection();
  }

  public CustomTree getTree() {
    return tree;
  }

}
