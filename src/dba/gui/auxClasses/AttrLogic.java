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

import dba.gui.CustomTree;
import dba.gui.FkWizard.FkWizard;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.options.FeedbackEnum;
import dba.utils.Localization;
import dbaCore.data.Attribute;
import dbaCore.data.RelationSchema;

import javax.swing.*;

/**
 * Class with methods to perform actions on Attributes
 *
 * @author Andreas Freitag
 */
public class AttrLogic {
  private Localization locale;
  private CustomTree tree;

  public AttrLogic() {
    super();
    locale = Localization.getInstance();
    tree = CustomTree.getInstance();
  }

  /**
   * Delete the currently marked Attribute from Relation
   */
  public void deleteAttribute() {
    RelationSchema relation = tree.getParentRelation();
    Attribute attribute = tree.getAttribute();
    Object[] options = {locale.getString("Yes"), locale.getString("No")};
    int n = JOptionPane.showOptionDialog(null, locale.getString("TREE_AttrDelMsg") + " '" + attribute.getName() +
      "'", locale.getString("TREE_AttrDelTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
      options, options[1]);
    if (n == 0) {
      relation.removeAttribute(attribute);
    }
  }

  /**
   * Rename the currently marked Attribute
   */
  public void renameAttribute() {
    Attribute attribute = tree.getAttribute();
    String name = (String) JOptionPane.showInputDialog(null, locale.getString("TREE_AttrNewAttrName"),
      locale.getString("TREE_AttrRenAttrTitle"), JOptionPane.QUESTION_MESSAGE, null, null, attribute.getName());
    if (name != null && !name.isEmpty() && !tree.checkIfAttrExists(name, tree.getParentRelation())) {
      tree.getDatabase().renameAttribute(tree.getParentRelation(), attribute, name);
    } else {
      FeedbackbarPanel.getInstance().showFeedback(locale.getString("FB_RenameFailed"), FeedbackEnum.FAILED);
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
      attribute.setIsForeignKey(false);
    } else {

      FkWizard wizard = new FkWizard(tree.getDatabase(), tree.getParentRelation(), attribute);
      wizard.setVisible(true);

      if (wizard.isDataBaseChanged()) {
        attribute.setIsForeignKey(true);
      }
    }
  }

  /**
   * Set the Attribute constraints (as given in class mySql)
   *
   * @param constraints Attribute constraints
   */
  public void setConstraints(String constraints) {
    tree.getAttribute().setConstraints(constraints);
  }

}
