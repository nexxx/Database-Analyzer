package dba.gui.auxClasses.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

import data.RelationSchema;

/**
 * Represents Relation Node in JTree Component
 * 
 * @author Andreas Freitag
 * 
 */
public class RelationNode extends DefaultMutableTreeNode {

  /**
   * 
   */
  private static final long serialVersionUID = 2858634879356999410L;

  /**
   * Parametric constructor
   * 
   * @param relation
   *          RelationSchema which will be the node
   */
  public RelationNode(RelationSchema relation) {
	super(relation);
  }

  @Override
  public String toString() {

	return ((RelationSchema) getUserObject()).getName();
  }

}
