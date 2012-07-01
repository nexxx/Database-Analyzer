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

import data.Database;
import data.RelationSchema;
import dba.gui.CustomTree;
import dba.gui.inspectFrame.inspectFrame;
import dba.gui.newRelation.RelationWizard;
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
    RelationWizard wizard = new RelationWizard(database, relation, WizardEnum.NEW);
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
