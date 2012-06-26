package dba.gui.auxClasses;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import dba.gui.auxClasses.nodes.AttributeNode;
import dba.gui.auxClasses.nodes.DatabaseNode;
import dba.gui.auxClasses.nodes.FunctionalDependencyNode;
import dba.gui.auxClasses.nodes.RelationNode;
import dba.utils.GetIcons;
import dba.utils.constants;


/**
 * Custom TreeCellRenderer to provide custom icons for Database,
 * Relations, Attributes and FDs
 * 
 * @author Andreas Freitag
 * 
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer implements
    constants {

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
  public Component getTreeCellRendererComponent(JTree tree, Object value,
	  boolean selected, boolean expanded, boolean leaf, int row,
	  boolean hasFocus) {

	super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf,
	    row, hasFocus);

	ImageIcon icon = new ImageIcon();
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
