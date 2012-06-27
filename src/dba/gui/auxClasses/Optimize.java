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

import data.*;
import dba.utils.GetIcons;
import dba.utils.Localization;
import logic.Analysis.GeneralRelationCheck;
import logic.normalization.Optimizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class which provides the Optimize frame to show the results!
 *
 * @author Andreas Freitag
 */
public class Optimize extends JDialog {

  private static final long serialVersionUID = 3804928181363080738L;
  private JDialog jDialog;
  private boolean modified;
  private NormalizationResult normalizationResult;

  /**
   * Defaultconstructor to create the dialog.
   */
  public Optimize(RelationSchema relation, NormalForm nf) {
    Localization locale = Localization.getInstance();
    this.setTitle(locale.getString("OPTI_FrameTitle"));
    GetIcons getIcons = GetIcons.getInstance();
    ImageIcon iconFrame = getIcons.getIconOptimizeFrame();
    this.setIconImage(iconFrame.getImage());
    jDialog = this;
    modified = false;
    setModal(true);
    getContentPane().setLayout(new BorderLayout());
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    // contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton okButton = new JButton(locale.getString("OPTI_Ok"));
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        modified = true;
        jDialog.dispose();
      }
    });

    JButton cancelButton = new JButton(locale.getString("OPTI_Cancel"));
    buttonPane.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jDialog.dispose();
      }
    });

    Optimizer optimizer = new Optimizer();

    GeneralRelationCheck checker = new GeneralRelationCheck();
    NormalForm currentNF = checker.getNF(relation,
            new ArrayList<FunctionalDependency>());

    normalizationResult = optimizer.normalize(relation, currentNF, nf);

    RelationView relationView = new RelationView();
    JScrollPane scrollPane = new JScrollPane(relationView);
    relationView.display(normalizationResult.getRelations(),
            normalizationResult.getForeignKeys());

    contentPanel.add(scrollPane, BorderLayout.CENTER);
    contentPanel.add(new JLabel(locale.getString("OPTI_Result")),
            BorderLayout.NORTH);
    switch (nf) {
      case SECOND:
        contentPanel.add(new JLabel(locale.getString("OPTI_Question2")),
                BorderLayout.SOUTH);
        break;
      case THIRD:
        contentPanel.add(new JLabel(locale.getString("OPTI_Question3")),
                BorderLayout.SOUTH);
        break;
      case BOYCECODD:
        contentPanel.add(new JLabel(locale.getString("OPTI_QuestionB")),
                BorderLayout.SOUTH);
        break;
      default:
        throw new IllegalArgumentException();
    }
    setSize(450, 300);
    setMinimumSize(new Dimension(400, 250));
    setLocationRelativeTo(null);

  }

  /**
   * Getter for the modified value
   *
   * @return true if relation was modified else false
   */
  public boolean isModified() {
    return modified;
  }

  /**
   * Getter for the new modified Relation
   *
   * @return modified Relation
   */
  public ArrayList<RelationSchema> getNormalizedRelations() {
    return normalizationResult.getRelations();
  }

  /**
   * Getter for the foreign keys of the result-relations
   *
   * @return List with all foreign keys
   */
  public ArrayList<ForeignKeyConstraint> getForeignKeys() {
    return normalizationResult.getForeignKeys();
  }

}
