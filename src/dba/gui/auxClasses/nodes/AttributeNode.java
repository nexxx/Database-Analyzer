package dba.gui.auxClasses.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

import data.Attribute;

/**
 * Represents Attribute Node in JTree Component
 * 
 * @author Andreas Freitag
 * 
 */
public class AttributeNode extends DefaultMutableTreeNode {
  /**
   * 
   */
  private static final long serialVersionUID = 6106415129286467781L;

  /**
   * Parametric constructor
   * 
   * @param attr
   *          Attribute which will be the node
   */
  public AttributeNode(Attribute attr) {
	super(attr);
  }

}
