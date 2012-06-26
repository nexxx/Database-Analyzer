package dba.gui.auxClasses;

import javax.swing.JOptionPane;

import data.Database;
import data.FunctionalDependency;
import data.RelationSchema;
import dba.gui.FDWizard.FDWizard;
import dba.utils.Localization;

/**
 * Class to provide all methods for functional dependencies
 * 
 * @author Andreas Freitag
 */
public class FdLogic {
  private Localization locale;

  public FdLogic() {
	locale = Localization.getInstance();
  }

  /**
   * Edit selected fd
   */
  public void editFd() {
	Database database = CustomTree.getInstance().getDatabase();
	RelationSchema relation = CustomTree.getInstance().getParentRelation();
	RelationSchema tmpRelation = relation.getClone();
	FunctionalDependency fd = CustomTree.getInstance().getFd();
	FunctionalDependency fdClone = fd.getClone();

	FDWizard wizard = new FDWizard(tmpRelation, fd);
	wizard.setVisible(true);

	if (wizard.getRelationChanged()) {
	  tmpRelation.removeFunctionalDependency(fdClone);
	  database.replaceRelationSchema(relation, tmpRelation);
	}
  }

  /**
   * Delete selected fd
   */
  public void deleteFd() {
	RelationSchema relation = CustomTree.getInstance().getParentRelation();
	FunctionalDependency fd = CustomTree.getInstance().getFd();

	Object[] options = { locale.getString("TREE_Yes"),
	    locale.getString("TREE_No") };
	int n = JOptionPane.showOptionDialog(null,
	    locale.getString("TREE_FDDelMsg") + " '" + fd.toString() + "'",
	    locale.getString("TREE_FDDelTitle"), JOptionPane.YES_NO_OPTION,
	    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
	if (n == 0) {
	  relation.removeFunctionalDependency(fd);
	}
  }
}
