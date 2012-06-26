package dba.gui.auxClasses.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

import data.FunctionalDependency;

/**
 * Represents Functional Dependency Node in JTree Component
 * 
 * @author Andreas Freitag
 * 
 */
public class FunctionalDependencyNode extends DefaultMutableTreeNode {
  /**
   * 
   */
  private static final long serialVersionUID = 7566043665204414293L;

  /**
   * Parametric constructor
   * 
   * @param fd
   *          FunctionalDependency which will be the node
   */
  public FunctionalDependencyNode(FunctionalDependency fd) {
	super(fd);
  }

}
