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

package dba.gui.newRelation;

import data.Database;
import data.RelationSchema;
import dba.gui.newRelation.auxClasses.AddRelationFunctionalDependencyPanel;
import dba.gui.newRelation.auxClasses.AddRelationNameAttributePanel;
import dba.gui.newRelation.auxClasses.ToggleControl;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.WizardEnum;
import dba.utils.constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class provides a JDialog which represents a Wizzard for a new
 * Relation or editing a existing Relation
 *
 * @author andreas
 */
public class RelationWizard extends JDialog implements constants {

  /**
   *
   */
  private static final long serialVersionUID = -7998280360637057225L;
  private RelationSchema relation;
  private JPanel pnlCardMain;
  private CardLayout card;
  private JButton btnFinish;
  private ToggleControl btnToggle;
  private AddRelationFunctionalDependencyPanel addRelationFunctionalDependencyPanel;
  private static String Attributes;
  private static String FDs = "FDs";
  private boolean relationChanged;

  /**
   * Defaultconstructor to create the frame.
   */
  public RelationWizard(Database database, RelationSchema rel, WizardEnum type) {
    super();
    this.setModal(true);
    Localization locale = Localization.getInstance();

    Attributes = locale.getString("Attributes");
    FDs = locale.getString("WIZ_FDs");

    if (type == WizardEnum.NEW) {
      this.setTitle(locale.getString("NewRelWiz"));
    } else if (type == WizardEnum.EDIT) {
      this.setTitle(locale.getString("EditRelWiz"));
    }

    GetIcons getIcons = GetIcons.getInstance();

    ImageIcon iconFinish = getIcons.getButtonFinish();
    ImageIcon iconCancel = getIcons.getButtonCancel();
    ImageIcon iconAttribute = getIcons.getButtonAttribute();
    ImageIcon iconFDs = getIcons.getButtonFd();
    ImageIcon iconFrame = getIcons.getIconWizardFrame();

    this.setIconImage(iconFrame.getImage());

    relationChanged = false;

    relation = rel;
    AddRelationNameAttributePanel addRelationNameAttributePanel = new AddRelationNameAttributePanel(relation, database, type);
    addRelationFunctionalDependencyPanel = new AddRelationFunctionalDependencyPanel(relation);
    card = new CardLayout(0, 0);
    btnFinish = new JButton(locale.getString("Finish"), iconFinish);
    btnFinish.setEnabled(false);
    PropertyChangeListener changeListener = new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("RelName")) {
          btnFinish.setEnabled((Boolean) evt.getNewValue());
        }
      }
    };

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setSize(800, 400);
    setMinimumSize(new Dimension(640, 480));
    setLocationRelativeTo(null);
    JPanel contentPane = new JPanel();
    // contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel pnlButtons = new JPanel();
    contentPane.add(pnlButtons, BorderLayout.SOUTH);
    pnlButtons.setLayout(new GridBagLayout());

    GridBagConstraints gbcCancel = new GridBagConstraints();
    gbcCancel.weightx = 0.25;
    gbcCancel.fill = GridBagConstraints.HORIZONTAL;
    gbcCancel.gridx = 0;
    gbcCancel.gridy = 0;
    JButton btnCancel = new JButton(locale.getString("Cancel"), iconCancel);
    btnCancel.addActionListener(new cancelButtonListener());
    pnlButtons.add(btnCancel, gbcCancel);

    GridBagConstraints gbcLbl1 = new GridBagConstraints();
    gbcLbl1.weightx = 0.1;
    gbcLbl1.fill = GridBagConstraints.HORIZONTAL;
    gbcLbl1.gridx = 1;
    gbcLbl1.gridy = 0;
    pnlButtons.add(new JLabel(""), gbcLbl1);

    GridBagConstraints gbcToggle = new GridBagConstraints();
    gbcToggle.weightx = 0.5;
    gbcToggle.fill = GridBagConstraints.HORIZONTAL;
    gbcToggle.gridx = 2;
    gbcToggle.gridy = 0;
    btnToggle = new ToggleControl(new String[]{Attributes, FDs});
    btnToggle.setSelectedButtonIndex(0);
    btnToggle.setIconOfButtonAtIndex(0, iconAttribute);
    btnToggle.setIconOfButtonAtIndex(1, iconFDs);
    pnlButtons.add(btnToggle, gbcToggle);
    btnToggle.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("SelectedButton")) {
          if (btnToggle.getSelectedButtonIndex() == 1) {
            addRelationFunctionalDependencyPanel.updateAttrLists();
            addRelationFunctionalDependencyPanel.updateFDsLists();
          }
          if (btnToggle.getSelectedButtonText().equals(Attributes)) {
            card.show(pnlCardMain, Attributes);
          } else if (btnToggle.getSelectedButtonText().equals(FDs)) {
            card.show(pnlCardMain, FDs);
          } else {
            // Should not happen
          }
        }

      }
    });

    GridBagConstraints gbcLbl2 = new GridBagConstraints();
    gbcLbl2.weightx = 0.1;
    gbcLbl2.fill = GridBagConstraints.HORIZONTAL;
    gbcLbl2.gridx = 3;
    gbcLbl2.gridy = 0;
    pnlButtons.add(new JLabel(""), gbcLbl2);

    GridBagConstraints gbcFinish = new GridBagConstraints();
    gbcFinish.weightx = 0.25;
    gbcFinish.fill = GridBagConstraints.HORIZONTAL;
    gbcFinish.gridx = 4;
    gbcFinish.gridy = 0;
    btnFinish.addActionListener(new finishButtonListener());
    pnlButtons.add(btnFinish, gbcFinish);

    pnlCardMain = new JPanel();
    contentPane.add(pnlCardMain, BorderLayout.CENTER);
    pnlCardMain.setLayout(card);
    addRelationNameAttributePanel.addPropertyChangeListener(changeListener);
    pnlCardMain.add(addRelationNameAttributePanel, Attributes);
    pnlCardMain.add(addRelationFunctionalDependencyPanel, FDs);
  }

  /**
   * Getter to see, if the given relation has changed
   *
   * @return true if relation has changed, else false
   */
  public boolean getRelationChanged() {
    return relationChanged;
  }

  /**
   * Getter for the relation
   *
   * @return Relation which was created or modified
   */
  public RelationSchema getRelation() {
    return relation;
  }

  private class cancelButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      dispose();
    }

  }

  private class finishButtonListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      relationChanged = true;
      dispose();
    }

  }
}
