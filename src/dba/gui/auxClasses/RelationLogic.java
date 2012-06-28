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

import data.Attribute;
import data.Database;
import data.NormalForm;
import data.RelationSchema;
import dba.gui.FDWizard.FDWizard;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.gui.inspectFrame.inspectFrame;
import dba.gui.newRelation.RelationWizard;
import dba.options.FeedbackEnum;
import dba.utils.Localization;
import dba.utils.WizardEnum;

import javax.swing.*;

/**
 * Class to provide methods for relations
 */
public class RelationLogic {
  private Localization locale;
  private CustomTree tree;

  public RelationLogic() {
    super();
    tree = CustomTree.getInstance();
    locale = Localization.getInstance();
  }

  /**
   * Add a new attribute to the selected relation
   */
  public void addAttribute() {
    RelationSchema relation = tree.getRelation();
    String name = "Name";

    for (int i = 1; i < Integer.MAX_VALUE; i++) {
      if (tree.checkIfAttrExists(name, relation)) {
        name = "Name" + i;
      } else {
        break;
      }
    }

    Attribute attribute = new Attribute(name);
    tree.expandNode(relation);
    relation.addAttribute(attribute);
  }

  /**
   * Add a fd to the selected relation
   */
  public void addFd() {
    Database database = tree.getDatabase();
    RelationSchema relation = tree.getRelation();
    RelationSchema tmpRelation = relation.getClone();

    FDWizard wizard = new FDWizard(tmpRelation, null);
    wizard.setVisible(true);

    if (wizard.getRelationChanged()) {
      tree.expandNode(tmpRelation);
      database.replaceRelationSchema(relation, tmpRelation);
    }
  }

  /**
   * Edit the selected relation with wizard
   */
  public void editRelationWizard() {
    Database database = tree.getDatabase();
    RelationSchema relation = tree.getRelation();
    RelationSchema tmpRelation = relation.getClone();
    RelationWizard wizard = new RelationWizard(database, tmpRelation, WizardEnum.EDIT);
    wizard.setVisible(true);

    if (wizard.getRelationChanged()) {
      database.replaceRelationSchema(relation, tmpRelation);
    }

  }

  /**
   * Delete the selected relation
   */
  public void deleteRelation() {
    Database database = tree.getDatabase();

    RelationSchema relation = tree.getRelation();

    Object[] options = {locale.getString("TREE_Yes"), locale.getString("TREE_No")};
    int n = JOptionPane.showOptionDialog(null, locale.getString("TREE_RelDelMsg") + " '" + relation.getName() + "'", locale.getString("TREE_RelDelTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    if (n == 0) {
      database.removeRelationSchema(relation);
    }
  }

  /**
   * Rename the selected relation
   */
  public void renameRelation() {
    RelationSchema relation = tree.getRelation();
    String name = (String) JOptionPane.showInputDialog(null, locale.getString("TREE_RelRenMsg"), locale.getString("TREE_RelRenTitle"), JOptionPane.QUESTION_MESSAGE, null, null, relation.getName());
    if (name != null && !name.isEmpty() && !tree.checkIfRelationExists(name)) {
      tree.getDatabase().renameRelationSchema(relation, name);
    } else {
      FeedbackbarPanel.getInstance().showFeedback(locale.getString("FB_RenameFailed"), FeedbackEnum.FAILED);
    }
  }

  /**
   * Inspect the selected relation
   */
  public void inspectRelation() {
    RelationSchema relation = tree.getRelation();
    inspectFrame frame = new inspectFrame(relation);
    frame.setVisible(true);
  }

  /**
   * Normalize the selected relation to the given normalform
   *
   * @param nf Target normalform
   */
  public void optimizeRelation(NormalForm nf) {
    switch (nf) {
      case SECOND:
        optimizeTo2NF();
        break;
      case THIRD:
        optimizeTo3NF();
        break;
      case BOYCECODD:
        optimizeToBCNF();
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  private void optimizeTo2NF() {
    Database database = tree.getDatabase();
    RelationSchema relation = tree.getRelation();

    OptimizeFrame optimizeFrame = new OptimizeFrame(relation, NormalForm.SECOND);
    optimizeFrame.setVisible(true);
    if (optimizeFrame.isModified()) {
      database.insertNormalizationResult(relation, optimizeFrame.getNormalizedRelations(), optimizeFrame.getForeignKeys());
    }
  }

  private void optimizeTo3NF() {
    Database database = tree.getDatabase();
    RelationSchema relation = tree.getRelation();

    OptimizeFrame optimizeFrame = new OptimizeFrame(relation, NormalForm.THIRD);
    optimizeFrame.setVisible(true);
    if (optimizeFrame.isModified()) {
      database.insertNormalizationResult(relation, optimizeFrame.getNormalizedRelations(), optimizeFrame.getForeignKeys());
    }
  }

  private void optimizeToBCNF() {
    Database database = tree.getDatabase();
    RelationSchema relation = tree.getRelation();

    OptimizeFrame optimizeFrame = new OptimizeFrame(relation, NormalForm.BOYCECODD);
    optimizeFrame.setVisible(true);
    if (optimizeFrame.isModified()) {
      database.insertNormalizationResult(relation, optimizeFrame.getNormalizedRelations(), optimizeFrame.getForeignKeys());
    }
  }
}
