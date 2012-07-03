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

package dba.gui.newRelation.auxClasses;

import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.constants;
import dbaCore.data.Attribute;
import dbaCore.data.FunctionalDependency;
import dbaCore.data.RelationSchema;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class to provide a JPanel with all Functions needed for the Wizard
 * Card1
 *
 * @author Andreas Freitag
 */
public class AddRelationFunctionalDependencyPanel extends JPanel implements constants {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private RelationSchema tmpRelation;
  private DefaultListModel<Attribute> lstMSource;
  private DefaultListModel<Attribute> lstMTarget;
  private DefaultListModel<FunctionalDependency> lstMFDs;
  private JList<Attribute> listSource;
  private JList<Attribute> listTarget;
  private JList<FunctionalDependency> listFDs;
  private JButton btnAdd;
  private JButton btnEdit;
  private JButton btnDelete;

  /**
   * Defaultconstructor to create the panel.
   *
   * @param relation RelationSchema which will the edited
   */
  public AddRelationFunctionalDependencyPanel(RelationSchema relation) {
    super();
    Localization locale = Localization.getInstance();

    GetIcons getIcons = GetIcons.getInstance();

    ImageIcon iconAdd = getIcons.getButtonAdd();
    ImageIcon iconEdit = getIcons.getButtonEdit();
    ImageIcon iconDelete = getIcons.getButtonDelete();
    ImageIcon iconFdArrow = getIcons.getFdArrow();

    setLayout(new MigLayout("wrap 5", "[fill, grow][grow,fill,32:32:32][fill, grow][grow,fill,130:130:130][fill, " +
      "" + "grow]"));

    tmpRelation = relation;
    btnAdd = new JButton(locale.getString("Add"), iconAdd);
    btnAdd.setHorizontalAlignment(SwingConstants.LEFT);
    btnAdd.setEnabled(false);
    btnEdit = new JButton(locale.getString("Edit"), iconEdit);
    btnEdit.setHorizontalAlignment(SwingConstants.LEFT);
    btnEdit.setEnabled(false);
    btnDelete = new JButton(locale.getString("Delete"), iconDelete);
    btnDelete.setHorizontalAlignment(SwingConstants.LEFT);
    btnDelete.setEnabled(false);
    lstMSource = new DefaultListModel<>();
    lstMTarget = new DefaultListModel<>();
    lstMFDs = new DefaultListModel<>();
    listSource = new JList<>(lstMSource);
    listTarget = new JList<>(lstMTarget);
    listFDs = new JList<>(lstMFDs);
    JScrollPane spSource = new JScrollPane(listSource);
    JScrollPane spTarget = new JScrollPane(listTarget);
    JScrollPane spFDs = new JScrollPane(listFDs);

    // Label FDs
    JLabel lblFunctionalDependencies = new JLabel(locale.getString("WIZ_FdFd"));
    lblFunctionalDependencies.setFont(new Font("Dialog", Font.BOLD, 14));
    add(lblFunctionalDependencies, "span");

    // Label Source Attr
    JLabel lblText = new JLabel(locale.getString("WIZ_FdSource"));
    add(lblText, "growx");

    // Label Target Attr
    JLabel lblTarget = new JLabel(locale.getString("WIZ_FdTarget"));
    add(lblTarget, "growx, cell 2 1");

    // Label Avail. FDs
    JLabel lblAvailFds = new JLabel(locale.getString("WIZ_FdAvailFds"));
    add(lblAvailFds, "growx, cell 4 1");

    // List Source Attr
    listSource.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        enableDisableAddBtn();
        setSelectableElements(listSource, listTarget);
      }
    });
    listSource.setSelectionModel(new MultiListSelectionModel());
    add(spSource, "grow, spany");

    // Label FD Arrow
    JLabel label = new JLabel(iconFdArrow);
    add(label, "grow, spany");

    // List Target Attr
    listTarget.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        enableDisableAddBtn();
        setSelectableElements(listTarget, listSource);
      }
    });
    listTarget.setSelectionModel(new MultiListSelectionModel());
    add(spTarget, "grow, spany");
    updateAttrLists();

    JPanel pnlButtons = new JPanel(new BorderLayout());
    JPanel pnlButtons2 = new JPanel();
    pnlButtons2.setLayout(new GridLayout(0, 1, 0, 10));
    pnlButtons.add(pnlButtons2, BorderLayout.NORTH);

    // Button Add
    btnAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addFDToRelation();
        listSource.clearSelection();
        listTarget.clearSelection();
        updateFDsLists();
      }
    });
    pnlButtons2.add(btnAdd);

    // Button Delete
    btnDelete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        deleteFD(listFDs.getSelectedValue());
      }
    });
    pnlButtons2.add(btnDelete);

    btnEdit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectSourceTarget(listFDs.getSelectedValue());
        tmpRelation.removeFunctionalDependency(listFDs.getSelectedValue());
        lstMFDs.removeElement(listFDs.getSelectedValue());
      }
    });
    pnlButtons2.add(btnEdit);

    // Panel Buttons
    add(pnlButtons, "grow, spany, pushy");

    // List FDs
    listFDs.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        if (!listFDs.getSelectedValuesList().isEmpty()) {
          btnDelete.setEnabled(true);
          btnEdit.setEnabled(true);
        } else {
          btnDelete.setEnabled(false);
          btnEdit.setEnabled(false);
        }
      }
    });
    listFDs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    add(spFDs, "grow, spany");
    updateFDsLists();

  }

  /**
   * Method to update the JLists containing the Sources and Targets
   */
  public void updateAttrLists() {
    lstMSource.clear();
    lstMTarget.clear();
    for (Attribute attr : tmpRelation.getAttributes()) {
      lstMSource.addElement(attr);
      lstMTarget.addElement(attr);
    }
  }

  /**
   * Method to update the JList with the FDs
   */
  public void updateFDsLists() {
    lstMFDs.clear();
    for (FunctionalDependency fd : tmpRelation.getFunctionalDependencies()) {
      lstMFDs.addElement(fd);
    }
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

  private void addFDToRelation() {
    FunctionalDependency fd = new FunctionalDependency(getSourceAttributes(), getTargetAttributes());
    tmpRelation.addFunctionalDependency(fd);
  }

  private void selectSourceTarget(FunctionalDependency fd) {
    int[] sources = new int[fd.getSourceAttributes().size()];
    int[] targets = new int[fd.getTargetAttributes().size()];
    int i = 0;

    for (Attribute a : tmpRelation.getAttributes()) {
      if (fd.getSourceAttributes().contains(a)) {
        sources[i++] = tmpRelation.getAttributes().indexOf(a);
      }
    }
    i = 0;
    for (Attribute a : fd.getTargetAttributes()) {
      if (fd.getTargetAttributes().contains(a)) {
        targets[i++] = tmpRelation.getAttributes().indexOf(a);
      }
    }
    listSource.setSelectedIndices(sources);
    listTarget.setSelectedIndices(targets);
  }

  private void enableDisableAddBtn() {
    if (listSource.getSelectedIndices().length == 0 || listTarget.getSelectedIndices().length == 0) {
      btnAdd.setEnabled(false);
    } else {
      btnAdd.setEnabled(true);
    }
  }

  private void deleteFD(FunctionalDependency fd) {
    tmpRelation.removeFunctionalDependency(fd);
    updateFDsLists();
  }

}
