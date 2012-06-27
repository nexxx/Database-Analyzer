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
import data.FunctionalDependency;
import data.RelationSchema;
import dba.gui.FDWizard.FDWizard;
import dba.utils.Localization;

import javax.swing.*;

/**
 * Class to provide all methods for functional dependencies
 *
 * @author Andreas Freitag
 */
public class FdLogic {
  private Localization locale;

  public FdLogic() {
    super();
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

    Object[] options = {locale.getString("TREE_Yes"),
            locale.getString("TREE_No")};
    int n = JOptionPane.showOptionDialog(null,
            locale.getString("TREE_FDDelMsg") + " '" + fd.toString() + "'",
            locale.getString("TREE_FDDelTitle"), JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    if (n == 0) {
      relation.removeFunctionalDependency(fd);
    }
  }
}
