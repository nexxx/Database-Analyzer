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

package dba.gui.FDWizard;

import data.Attribute;
import data.FunctionalDependency;
import data.RelationSchema;
import dba.gui.newRelation.auxClasses.MultiListSelectionModel;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.constants;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Frame for the New FD Wizard
 *
 * @author Andreas Freitag
 */
public class FDWizard extends JDialog implements constants {

  /**
   *
   */
  private static final long serialVersionUID = 5047143456941921933L;
  private JPanel contentPane;
  private RelationSchema relation;
  private DefaultListModel<Attribute> lstMSource;
  private DefaultListModel<Attribute> lstMTarget;
  private JList<Attribute> listSource;
  private JList<Attribute> listTarget;
  private JButton btnFinish;
  private boolean relationChanged;
  private Localization locale;

  /**
   * Constructor to create the frame.
   *
   * @param rel                  Relation where the FD will be stored
   * @param functionalDependency FD which will be edited (null if new FD will be created)
   */
  public FDWizard(RelationSchema rel, FunctionalDependency functionalDependency) {
    super();
    locale = Localization.getInstance();

    FunctionalDependency fd = functionalDependency;
    relationChanged = false;
    if (fd == null) {
      this.setTitle(locale.getString("WIZ_FDNewTitle"));
      fd = new FunctionalDependency();
    } else {
      this.setTitle(locale.getString("WIZ_FDEditTitle"));
    }

    GetIcons getIcons = GetIcons.getInstance();
    ImageIcon iconFdArrow = getIcons.getFdArrow();
    ImageIcon iconFinish = getIcons.getButtonFinish();
    ImageIcon iconCancel = getIcons.getButtonCancel();
    ImageIcon iconFrame = getIcons.getIconFDWizardFrame();

    setIconImage(iconFrame.getImage());
    setModal(true);
    contentPane = new JPanel();
    JPanel pnlMain = new JPanel(new MigLayout("wrap 3", "[fill, grow][grow,fill,32:32:32][fill, grow]"));
    // contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    contentPane.setLayout(new BorderLayout());
    contentPane.add(pnlMain, BorderLayout.CENTER);
    setSize(600, 250);
    setMinimumSize(new Dimension(480, 250));
    lstMSource = new DefaultListModel<>();
    lstMTarget = new DefaultListModel<>();
    listSource = new JList<>(lstMSource);
    listSource.setVisibleRowCount(100);
    listTarget = new JList<>(lstMTarget);
    listTarget.setVisibleRowCount(100);
    JScrollPane spSource = new JScrollPane(listSource);
    JScrollPane spTarget = new JScrollPane(listTarget);
    btnFinish = new JButton(locale.getString("WIZ_Finish"), iconFinish);
    JButton btnCancel = new JButton(locale.getString("WIZ_Cancel"), iconCancel);
    relation = rel;

    // Label FDs
    JLabel lblFd = new JLabel(locale.getString("WIZ_FDLabel"));
    pnlMain.add(lblFd, "spanx");

    // Label Source
    JLabel lblSource = new JLabel(locale.getString("WIZ_FdSource"));
    pnlMain.add(lblSource, "growx");

    // Label Target
    JLabel lblTarget = new JLabel(locale.getString("WIZ_FdTarget"));
    pnlMain.add(lblTarget, "growx, cell 2 1");

    // List Source
    listSource.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        setSelectableElements(listSource, listTarget);
        checkForButton();

      }
    });
    listSource.setSelectionModel(new MultiListSelectionModel());
    pnlMain.add(spSource, "grow, spany");

    // Label FD Arrow
    JLabel label = new JLabel(iconFdArrow);
    pnlMain.add(label, "grow, spany");

    // List Target
    listTarget.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        setSelectableElements(listTarget, listSource);
        checkForButton();
      }
    });
    listTarget.setSelectionModel(new MultiListSelectionModel());
    pnlMain.add(spTarget, "grow, spany");

    updateAttrLists();
    selectSourceTarget(fd);

    // Buttons
    JPanel pnlButtons = new JPanel(new GridLayout(1, 4));
    btnCancel.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    pnlButtons.add(btnCancel);
    pnlButtons.add(new JLabel(""));
    pnlButtons.add(new JLabel(""));
    btnFinish.setEnabled(false);
    btnFinish.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        FunctionalDependency fd = new FunctionalDependency(getSourceAttributes(), getTargetAttributes());
        for (FunctionalDependency functionalDependency : relation.getFunctionalDependencies()) {
          if (fd.equals(functionalDependency)) {
            JOptionPane.showMessageDialog(contentPane, locale.getString("WIZ_FDAlreadyExistingMsg"));
            return;
          }
        }
        relation.addFunctionalDependency(fd);
        relationChanged = true;
        dispose();

      }
    });
    pnlButtons.add(btnFinish);
    contentPane.add(pnlButtons, BorderLayout.SOUTH);

    setLocationRelativeTo(null);

  }

  private void setSelectableElements(JList<Attribute> list, JList<Attribute> otherList) {
    for (int i : otherList.getSelectedIndices()) {
      for (int j : list.getSelectedIndices()) {
        if (i == j) {
          list.removeSelectionInterval(j, j);
          // TODO: Change color to grey if item is already selected in
          // other list!!!
        }
      }
    }
  }

  /**
   * Method to update the JLists containing the Sources and Targets
   */
  private void updateAttrLists() {
    lstMSource.clear();
    lstMTarget.clear();
    for (Attribute attr : relation.getAttributes()) {
      lstMSource.addElement(attr);
      lstMTarget.addElement(attr);
    }
  }

  private void checkForButton() {
    if (listSource.getSelectedIndices().length != 0 && listTarget.getSelectedIndices().length != 0) {
      btnFinish.setEnabled(true);
    } else {
      btnFinish.setEnabled(false);
    }
  }

  public boolean getRelationChanged() {
    return relationChanged;
  }

  /**
   * Getter for the SourceAttributes
   *
   * @return ArrayList with all SourceAttributes
   */
  private ArrayList<Attribute> getSourceAttributes() {
    ArrayList<Attribute> sourceAttributes = new ArrayList<>();
    for (Attribute attr : listSource.getSelectedValuesList()) {
      sourceAttributes.add(attr);
    }

    return sourceAttributes;
  }

  /**
   * Getter for the TargetAttributes
   *
   * @return ArrayList with all TargetAttributes
   */
  private ArrayList<Attribute> getTargetAttributes() {
    ArrayList<Attribute> targetAttributes = new ArrayList<>();
    for (Attribute attr : listTarget.getSelectedValuesList()) {
      targetAttributes.add(attr);
    }
    return targetAttributes;
  }

  private void selectSourceTarget(FunctionalDependency fd) {
    int[] sources = new int[fd.getSourceAttributes().size()];
    int[] targets = new int[fd.getTargetAttributes().size()];
    int i = 0;

    for (Attribute a : relation.getAttributes()) {
      if (fd.getSourceAttributes().contains(a)) {
        sources[i++] = relation.getAttributes().indexOf(a);
      }
    }
    i = 0;
    for (Attribute a : fd.getTargetAttributes()) {
      if (fd.getTargetAttributes().contains(a)) {
        targets[i++] = relation.getAttributes().indexOf(a);
      }
    }
    listSource.setSelectedIndices(sources);
    listTarget.setSelectedIndices(targets);
  }
}
