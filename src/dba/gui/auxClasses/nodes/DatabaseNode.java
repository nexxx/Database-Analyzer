package dba.gui.auxClasses.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

import data.Database;

/**
 * Represents Database Node in JTree Component
 * 
 * @author Andreas Freitag
 * 
 */
public class DatabaseNode extends DefaultMutableTreeNode {

  /**
   * 
   */
  private static final long serialVersionUID = -3341868219612487768L;

  /**
   * Parametric constructor
   * 
   * @param db
   *          Database which will be the node
   */
  public DatabaseNode(Database db) {
	super(db);
  }

  public void setDatabase(Database database) {
	userObject = database;
  }

}
