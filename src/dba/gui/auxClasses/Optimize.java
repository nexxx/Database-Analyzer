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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import logic.Analysis.GeneralRelationCheck;
import logic.normalization.Optimizer;
import data.ForeignKeyConstraint;
import data.FunctionalDependency;
import data.NormalForm;
import data.NormalizationResult;
import data.RelationSchema;
import dba.utils.GetIcons;
import dba.utils.Localization;

/**
 * Class which provides the Optimize frame to show the results!
 * 
 * @author Andreas Freitag
 */
public class Optimize extends JDialog {

  private static final long serialVersionUID = 3804928181363080738L;
  private final JPanel contentPanel = new JPanel();
  private JDialog jDialog;
  private boolean modified;
  private NormalizationResult normalizationResult;
  private GetIcons getIcons;
  private Localization locale;

  /**
   * Defaultconstructor to create the dialog.
   */
  public Optimize(RelationSchema relation, NormalForm nf) {
	locale = Localization.getInstance();
	this.setTitle(locale.getString("OPTI_FrameTitle"));
	getIcons = GetIcons.getInstance();
	ImageIcon iconFrame = getIcons.getIconOptimizeFrame();
	this.setIconImage(iconFrame.getImage());
	jDialog = this;
	modified = false;
	setModal(true);
	getContentPane().setLayout(new BorderLayout());
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
	  new IllegalArgumentException();
	  break;
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
