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

package dba.gui.FkWizard;

import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.constants;
import dbaCore.data.Attribute;
import dbaCore.data.Database;
import dbaCore.data.ForeignKeyConstraint;
import dbaCore.data.RelationSchema;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Frame to create new Foreign-Key constraints
 *
 * @author Sebastian Theuermann (inspiration from Andreas Freitag)
 */
public class FkWizard extends JDialog implements constants {

  /**
   *
   */
  private static final long serialVersionUID = 5047143456941921933L;
  private DefaultListModel<String> listMRelation;
  private DefaultListModel<String> listMAttribute;
  private JList<String> listRelation;
  private JList<String> listAttribute;
  private JButton btnFinish;
  private boolean dataBaseChanged;
  private Database db;
  private ForeignKeyConstraint foreignKey;
  private RelationSchema sourceRelation;

  /**
   * Constructor to create the frame.
   *
   * @param db   the actual Database containing the relation
   * @param rel  Relation where the FD will be stored
   * @param attr FD which will be edited (null if new FD will be created)
   */
  public FkWizard(Database db, RelationSchema rel, Attribute attr) {
    super();
    foreignKey = new ForeignKeyConstraint();
    this.db = db;

    this.sourceRelation = rel;
    dataBaseChanged = false;

    Localization locale = Localization.getInstance();

    foreignKey.setSourceAttributeName(attr.getName());
    foreignKey.setSourceRelationName(sourceRelation.getName());

    GetIcons getIcons = GetIcons.getInstance();
    ImageIcon iconFdArrow = getIcons.getFdArrow();
    ImageIcon iconFinish = getIcons.getButtonFinish();
    ImageIcon iconCancel = getIcons.getButtonCancel();

    setTitle(locale.getString("WIZ_FKTitle"));

    setIconImage(getIcons.getTbFK().getImage());
    setModal(true);
    JPanel contentPane = new JPanel();
    JPanel pnlMain = new JPanel(new MigLayout("wrap 3", "[fill, grow][grow,fill,32:32:32][fill, grow]"));
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    contentPane.setLayout(new BorderLayout());
    contentPane.add(pnlMain, BorderLayout.CENTER);
    setSize(600, 250);
    setMinimumSize(new Dimension(600, 250));
    listMRelation = new DefaultListModel<>();
    listMAttribute = new DefaultListModel<>();
    listRelation = new JList<>(listMRelation);
    listRelation.setVisibleRowCount(100);
    listAttribute = new JList<>(listMAttribute);
    listAttribute.setVisibleRowCount(100);
    JScrollPane spSource = new JScrollPane(listRelation);
    JScrollPane spTarget = new JScrollPane(listAttribute);
    btnFinish = new JButton(locale.getString("Finish"), iconFinish);
    JButton btnCancel = new JButton(locale.getString("Cancel"), iconCancel);

    // Label FDs
    JLabel lblFd = new JLabel(locale.getString("WIZ_FKTarget"));
    pnlMain.add(lblFd, "spanx");

    // Label Relation
    JLabel lblRelation = new JLabel(locale.getString("Relation"));
    lblRelation.setIcon(getIcons.getTbRelation());
    pnlMain.add(lblRelation, "growx");

    // Label Attribute
    JLabel lblAttribute = new JLabel(locale.getString("WIZ_FKAttribute"));
    lblAttribute.setIcon(getIcons.getTbAttribute());
    pnlMain.add(lblAttribute, "growx, cell 2 1");

    // List Relation
    listRelation.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        foreignKey.setTargetRelationName(listRelation.getSelectedValue());
        updateAttributeList(foreignKey.getTargetRelationName());
        checkForButton();

      }
    });
    listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pnlMain.add(spSource, "grow, spany");

    // Label FK Arrow
    JLabel label = new JLabel(iconFdArrow);
    pnlMain.add(label, "grow, spany");

    // List Attribute
    listAttribute.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        foreignKey.setTargetAttributeName(listAttribute.getSelectedValue());
        checkForButton();
      }
    });
    listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pnlMain.add(spTarget, "grow, spany");

    //garther selectable relations
    for (String relationName : db.getAllRelationNames()) {
      listMRelation.addElement(relationName);
    }
    if (!listMRelation.contains(sourceRelation.getName())) {
      listMRelation.addElement(sourceRelation.getName());
    }

    // Pre-Select first relation if existing
    if (!listMRelation.isEmpty()) {
      listRelation.setSelectedIndex(0);
    }

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
        addToDataBase();
        dataBaseChanged = true;
        dispose();

      }
    });
    pnlButtons.add(btnFinish);
    contentPane.add(pnlButtons, BorderLayout.SOUTH);

    setLocationRelativeTo(null);

  }

  private void addToDataBase() {
    db.getForeignKeys().add(foreignKey);
  }

  /**
   * updates the selectable Attributes according to the given
   * RelationName
   *
   * @param relationName the relation to take the attributes from
   */
  private void updateAttributeList(String relationName) {
    listMAttribute.clear();
    RelationSchema relation = db.getRelationSchemaByName(relationName);
    if (relation == null) {
      relation = sourceRelation;
    }

    for (Attribute attribute : relation.getAttributes()) {
      if (attribute.getIsPrimaryKey()) {
        listMAttribute.addElement(attribute.getName());
      }
    }
  }

  /**
   * enables the "finish"-Button when all critera are met
   */
  private void checkForButton() {
    if (listRelation.getSelectedIndices().length != 0 && listAttribute.getSelectedIndices().length != 0) {
      btnFinish.setEnabled(true);
    } else {
      btnFinish.setEnabled(false);
    }
  }

  /**
   * returns if the database has been changed (e.g. FK-added)
   *
   * @return true if the database changed, false if not
   */
  public boolean isDataBaseChanged() {
    return dataBaseChanged;
  }
}
