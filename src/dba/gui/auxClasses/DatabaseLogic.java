package dba.gui.auxClasses;

import data.Database;
import data.RelationSchema;
import dba.gui.inspectFrame.inspectFrame;
import dba.gui.newRelation.RelationWizzard;
import dba.utils.WizardEnum;

/**
 * Class which handles all database relevant methods used in GUI
 */
public class DatabaseLogic {
  /**
   * Show the relation wizard frame
   */
  public void newWizardRelation() {
	Database database = CustomTree.getInstance().getDatabase();
	RelationSchema relation = new RelationSchema();
	RelationWizzard wizard = new RelationWizzard(database, relation,
	    WizardEnum.NEW);
	wizard.setVisible(true);

	if (wizard.getRelationChanged()) {
	  database.addRelationSchema(wizard.getRelation());
	}
  }

  /**
   * Add a new empty relation
   */
  public void newEmptyRelation() {
	Database database = CustomTree.getInstance().getDatabase();
	String name = "Name";

	for (int i = 1; i < Integer.MAX_VALUE; i++) {
	  if (CustomTree.getInstance().checkIfRelationExists(name)) {
		name = "Name" + i;
	  } else {
		break;
	  }
	}

	RelationSchema rel = new RelationSchema(name);
	database.addRelationSchema(rel);
  }

  /**
   * Inspect all relations
   */
  public void inspectRelation() {
	Database database = CustomTree.getInstance().getDatabase();
	inspectFrame frame = new inspectFrame(database);
	frame.setVisible(true);
  }
}
