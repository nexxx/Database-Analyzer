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

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import data.Attribute;
import data.Database;
import data.FunctionalDependency;
import data.RelationSchema;
import dba.gui.auxClasses.nodes.AttributeNode;
import dba.gui.auxClasses.nodes.DatabaseNode;
import dba.gui.auxClasses.nodes.FunctionalDependencyNode;
import dba.gui.auxClasses.nodes.RelationNode;
import dba.utils.TreeEnum;

/**
 * Class which extends JTree and implements methods for our usage
 * (singleton)
 * 
 * @author Andreas Freitag
 */
public class CustomTree extends JTree {
  private DefaultMutableTreeNode lastSelectedNode;
  private CustomTree tree;
  private static CustomTree instance;
  private List<Integer> expandedTreeObjects;
  private boolean supressExpansionEvent;
  /**
   * 
   */
  private static final long serialVersionUID = -7671158844002979801L;

  private CustomTree() {
	UIManager.put("PopupMenu.consumeEventOnClose", Boolean.FALSE);
	tree = this;
	this.addTreeSelectionListener(new TreeSelectionListener() {

	  @Override
	  public void valueChanged(TreeSelectionEvent e) {
		lastSelectedNode = (DefaultMutableTreeNode) tree
		    .getLastSelectedPathComponent();
	  }
	});

	expandedTreeObjects = new LinkedList<Integer>();
	// add expansion listener to preserve expansion state of each node
	tree.addTreeExpansionListener(new TreeExpansionListener() {
	  @Override
	  public void treeExpanded(TreeExpansionEvent event) {
		processTreeExpansion(event);
	  }

	  @Override
	  public void treeCollapsed(TreeExpansionEvent event) {
		processTreeCollapse(event);
	  }
	});
  }

  /**
   * Getter for the singelton customtree (thread-safe)
   * */
  public synchronized static CustomTree getInstance() {
	if (instance == null) {
	  synchronized (CustomTree.class) {
		instance = new CustomTree();
	  }
	}
	return instance;
  }

  /**
   * Get parent relation from current marked attribute or fd
   * 
   * @return parent relation
   */
  public RelationSchema getParentRelation() {
	RelationSchema relation = (RelationSchema) ((RelationNode) ((DefaultMutableTreeNode) this
	    .getSelectionPath().getLastPathComponent()).getParent())
	    .getUserObject();
	if (relation == null) {
	  return (RelationSchema) ((RelationNode) lastSelectedNode.getParent())
		  .getUserObject();
	}
	return relation;
  }

  /**
   * Get current marked attribute
   * 
   * @return marked attribute
   */
  public Attribute getAttribute() {
	Attribute attribute = (Attribute) ((AttributeNode) this.getSelectionPath()
	    .getLastPathComponent()).getUserObject();
	if (attribute == null) {
	  return (Attribute) lastSelectedNode.getUserObject();
	}
	return attribute;
  }

  /**
   * Check if relationname is existing in DB
   * 
   * @param relName
   *          Relation Name to check, if existing in database
   * @return true if name exists or if name does not exist
   */
  public boolean checkIfRelationExists(String relName) {

	for (RelationSchema relation : getDatabase().getDatabase()) {
	  if (relation.getName().equalsIgnoreCase(relName)) {
		return true;
	  }
	}
	return false;
  }

  /**
   * Getter for the current database
   * 
   * @return Database
   */
  public Database getDatabase() {
	Database database = (Database) ((DatabaseNode) this.getModel().getRoot())
	    .getUserObject();
	if (database == null) {
	  return (Database) lastSelectedNode.getUserObject();
	}
	return database;
  }

  /**
   * Getter for the current marked FD
   * 
   * @return Marked FD
   */
  public FunctionalDependency getFd() {
	FunctionalDependency fd = (FunctionalDependency) ((FunctionalDependencyNode) this
	    .getSelectionPath().getLastPathComponent()).getUserObject();
	if (fd == null) {
	  return (FunctionalDependency) lastSelectedNode.getUserObject();
	}
	return fd;
  }

  /**
   * Get current marked relation
   * 
   * @return current marked relation
   */
  public RelationSchema getRelation() {
	RelationSchema relation = (RelationSchema) ((RelationNode) this
	    .getSelectionPath().getLastPathComponent()).getUserObject();
	if (relation == null) {
	  return (RelationSchema) lastSelectedNode.getUserObject();
	}

	return relation;
  }

  /**
   * Get the new selected Node in the after performing an operation!
   * 
   * @param type
   *          Operation which will be performed
   * @return Rownumber for selected Node after operation
   */
  public int getNewSelectedItem(TreeEnum type) {
	int rowNr = 0;
	switch (type) {
	case DelAttribute:
	case DelFD:
	  // Parent Relation
	  rowNr = tree.getRowForPath(new TreePath(
		  ((RelationNode) ((DefaultMutableTreeNode) this.getSelectionPath()
		      .getLastPathComponent()).getParent()).getPath()));
	  break;
	case AddRelation:
	case DelRelation:
	case InspectDb:
	case OptimizeDb:
	  // Database
	  rowNr = tree.getRowForPath(new TreePath(((DatabaseNode) this.getModel()
		  .getRoot()).getPath()));
	  break;
	case RenameRel:
	case EditRel:
	case InspectRel:
	case OptimizeRel:
	case AddFD:
	case AddAttribute:
	  // Relation
	  rowNr = tree.getRowForPath(new TreePath(((RelationNode) this
		  .getSelectionPath().getLastPathComponent()).getPath()));
	  break;
	case RenameAttr:
	case EditAttr:
	case TogglePk:
	case ToggleFk:
	case SwitchType:
	  // Attribute
	  rowNr = tree.getRowForPath(new TreePath(((AttributeNode) this
		  .getSelectionPath().getLastPathComponent()).getPath()));
	  break;
	case EditFd:
	  // FD
	  rowNr = tree.getRowForPath(new TreePath(((FunctionalDependencyNode) this
		  .getSelectionPath().getLastPathComponent()).getPath()));
	  break;

	}

	return rowNr;
  }

  /**
   * Select a row in tree and scroll to it
   * 
   * @param row
   *          Row which will be selected
   */
  public void setSelectedItem(int row) {
	tree.setSelectionRow(row);
	tree.scrollRowToVisible(row);
	// tree.requestFocus();
  }

  /**
   * Check if a given Attribute name exists in given RelationSchema
   * 
   * @param name
   *          Name of the Attribute
   * @param relation
   *          RelationSchema which will be checked
   * @return true/false
   */
  public boolean checkIfAttrExists(String name, RelationSchema relation) {
	for (Attribute attr : relation.getAttributes()) {
	  if (attr.getName().equalsIgnoreCase(name)) {
		return true;
	  }
	}
	return false;
  }

  /**
   * Select Node in tree by given object
   * 
   * @param target
   *          Object which will be selected
   */
  public void setSelectedNode(Object target) {
	selectNode(tree.getModel().getRoot(), target);
  }

  private void selectNode(Object root, Object target) {
	int cc;
	cc = tree.getModel().getChildCount(root);
	for (int i = 0; i < cc; i++) {
	  DefaultMutableTreeNode child = (DefaultMutableTreeNode) tree.getModel()
		  .getChild(root, i);
	  if (child.getUserObject() == target) {
		TreeNode[] pathWithNodes = child.getPath();
		TreePath path = new TreePath(pathWithNodes);
		tree.setSelectionPath(path);
		break;
	  } else {
		selectNode(child, target);
	  }
	}
  }

  private void restoreTreeNode(CustomTree tree, TreePath parent,
	  DefaultMutableTreeNode treeNode) {
	// Traverse down through the children
	TreeNode node = (TreeNode) parent.getLastPathComponent();

	if (node.getChildCount() >= 0) { // If the node has children?
	  // Create a child numerator over the node
	  Enumeration<?> en = node.children();
	  while (en.hasMoreElements()) { // While we have children
		DefaultMutableTreeNode dmTreeNode = (DefaultMutableTreeNode) en
		    .nextElement(); // Derive the node
		TreePath path = parent.pathByAddingChild(dmTreeNode); // Derive
		                                                      // the
		                                                      // path
		restoreTreeNode(tree, path, dmTreeNode); // Recursive call
		                                         // with new path

	  } // End While we have more children
	} // End If the node has children?

	// Nodes need to be expand from last branch node up
	if (treeNode != null) { // If true, this is the root node - ignore
		                    // it

	  DefaultMutableTreeNode currentNode = treeNode;

	  if (currentNode instanceof RelationNode) {
		if (expandedTreeObjects.contains(((RelationSchema) currentNode
		    .getUserObject()).getOwnId())) {
		  tree.expandPath(parent); // et viola
		}

	  }
	}
  }

  private void processTreeExpansion(TreeExpansionEvent e) {

	if (supressExpansionEvent == false) {
	  DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
		  .getLastPathComponent();

	  if (node instanceof RelationNode) {
		expandedTreeObjects.add(((RelationSchema) node.getUserObject())
		    .getOwnId());
	  }

	}

  }

  private void processTreeCollapse(TreeExpansionEvent e) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
	    .getLastPathComponent();

	if (node instanceof RelationNode) {
	  Integer schemaId = ((RelationSchema) node.getUserObject()).getOwnId();
	  if (expandedTreeObjects.contains(schemaId)) {
		expandedTreeObjects.remove(schemaId);
	  }
	}
  }

  /**
   * Restore the expansion state as stored in the list
   */
  public void restoreExpansionState() {
	supressExpansionEvent = true;

	((DefaultTreeModel) tree.getModel()).reload();
	restoreTreeNode(tree, new TreePath(tree.getModel().getRoot()), null);

	supressExpansionEvent = false; // Now we can go back to responding
	                               // normally to expansion events
  }

  /**
   * Reset the stored list
   */
  public void resetExpansionState() {
	expandedTreeObjects.clear();
  }

  /**
   * Expand a given relation node
   * 
   * @param relation
   *          node which will be expanded
   */
  public void expandNode(RelationSchema relation) {
	if (!expandedTreeObjects.contains(relation.getOwnId())) {
	  expandedTreeObjects.add(relation.getOwnId());
	}

  }
}
