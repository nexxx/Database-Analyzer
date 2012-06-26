package dba.gui.auxClasses;

import javax.swing.JOptionPane;

import data.Attribute;
import data.RelationSchema;
import data.dBTypes.mySql;
import dba.gui.FkWizard.FkWizard;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.options.FeedbackEnum;
import dba.utils.Localization;

/**
 * Class with methods to perform actions on Attributes
 * 
 * @author Andreas Freitag
 */
public class AttrLogic {
  private Localization locale;
  private CustomTree tree;

  public AttrLogic() {
	locale = Localization.getInstance();
	tree = CustomTree.getInstance();
  }

  /**
   * Delete the currently marked Attribute from Relation
   */
  public void deleteAttribute() {
	RelationSchema relation = tree.getParentRelation();
	Attribute attribute = tree.getAttribute();
	Object[] options = { locale.getString("TREE_Yes"),
	    locale.getString("TREE_No") };
	int n = JOptionPane.showOptionDialog(null,
	    locale.getString("TREE_AttrDelMsg") + " '" + attribute.getName() + "'",
	    locale.getString("TREE_AttrDelTitle"), JOptionPane.YES_NO_OPTION,
	    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
	if (n == 0) {
	  relation.removeAttribute(attribute);
	}
  }

  /**
   * Rename the currently marked Attribute
   */
  public void renameAttribute() {
	Attribute attribute = tree.getAttribute();
	String name = (String) JOptionPane.showInputDialog(null,
	    locale.getString("TREE_AttrNewAttrName"),
	    locale.getString("TREE_AttrRenAttrTitle"),
	    JOptionPane.QUESTION_MESSAGE, null, null, attribute.getName());
	if (name != null && name.length() > 0
	    && !tree.checkIfAttrExists(name, tree.getParentRelation())) {
	  tree.getDatabase().renameAttribute(tree.getParentRelation(), attribute,
		  name);
	} else {
	  FeedbackbarPanel.getInstance().showFeedback(
		  locale.getString("FB_RenameFailed"), FeedbackEnum.FAILED);
	}
  }

  /**
   * Toggle Primary Key on marked attribute (on/off)
   */
  public void togglePK() {
	Attribute attribute = tree.getAttribute();
	if (attribute.getIsPrimaryKey()) {
	  attribute.setIsPrimaryKey(false);
	} else {
	  attribute.setIsPrimaryKey(true);
	}
  }

  /**
   * Toggle Foreign Key on marked attribute (on/off)
   */
  public void toggleFK() {
	Attribute attribute = tree.getAttribute();
	if (attribute.getIsForeignKey()) {
	  removeForeignKey();
	  attribute.setIsForeignKey(false);
	} else {

	  FkWizard wizard = new FkWizard(tree.getDatabase(),
		  tree.getParentRelation(), attribute);
	  wizard.setVisible(true);

	  if (wizard.isDataBaseChanged()) {
		attribute.setIsForeignKey(true);
	  }
	}
  }

  /**
   * Removes a existent ForeignKey associated with this attribute
   */
  private void removeForeignKey() {
	tree.getDatabase().removeForeignKey(tree.getParentRelation().getName(),
	    tree.getAttribute().getName());
  }

  /**
   * Set the Attribute type (as given in class mySql)
   * 
   * @param type
   *          Attribute Type
   */
  public void setType(String type) {
	for (String s : mySql.getInstance().getTypes()) {
	  if (s.equalsIgnoreCase(type)) {
		tree.getAttribute().setType(type);
		return;
	  }
	}
	throw new IllegalArgumentException();
  }
}
