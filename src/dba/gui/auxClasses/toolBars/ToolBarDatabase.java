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

package dba.gui.auxClasses.toolBars;

import dba.gui.CustomTree;
import dba.gui.DatatypeMappingFrame.DatatypeMappingFrame;
import dba.gui.auxClasses.*;
import dba.options.FeedbackEnum;
import dba.utils.TreeEnum;
import dbaCore.data.Attribute;
import dbaCore.data.Database;
import dbaCore.data.RelationSchema;
import dbaCore.data.TimeLine;
import dbaCore.data.dBTypes.TypeEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class to provide the ToolBar for the Database (extends ToolBar
 * class)
 *
 * @author Andreas Freitag
 */
public class ToolBarDatabase extends ToolBar {
  private CustomTree tree;
  private DatabaseLogic dbLogic;
  private JButton btnInspect;
  private JComboBox<String> cb;
  private GuiLogic guiLogic;
  /**
   *
   */
  private static final long serialVersionUID = -3843390455939145286L;

  public ToolBarDatabase(DatabaseTreePanel dbTreePanel, RelationView relationView,
                         RelationDetailsView relationDetailsView, GuiLogic gL) {
    super(relationView, relationDetailsView, dbTreePanel);

    tree = dbTreePanel.getTree();
    guiLogic = gL;
    JButton btnNewRelation = new JButton(super.getIcons.getTbWizard());
    JButton btnEmptyRelation = new JButton(super.getIcons.getTbRelation());
    btnInspect = new JButton(super.getIcons.getTbInspect());

    btnNewRelation.setToolTipText(super.locale.getString("NewRelWiz"));
    btnEmptyRelation.setToolTipText(super.locale.getString("TREE_DBAddEmptyRel"));
    btnInspect.setToolTipText(super.locale.getString("TREE_DBInspect"));

    dbLogic = new DatabaseLogic();
    btnNewRelation.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
        dbLogic.newWizardRelation();
        tree.setSelectedItem(x);
      }
    });

    btnEmptyRelation.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
        dbLogic.newEmptyRelation();
        tree.setSelectedItem(x);
      }
    });

    btnInspect.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.InspectDb);
        dbLogic.inspectRelation();
        tree.setSelectedItem(x);
      }
    });

    cb = new JComboBox<>();
    for (TypeEnum e : TypeEnum.values()) {
      cb.addItem(e.getName());
    }
    cb.setSelectedItem(tree.getDatabase().getType().getName());
    cb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (!((String) cb.getSelectedItem()).equalsIgnoreCase(tree.getDatabase().getType().getName())) {
          Database dbOld = tree.getDatabase().getClone();
          tree.getDatabase().setType(TypeEnum.getEnumByValue((String) cb.getSelectedItem()));
          resetAllDataTypes();

          DatatypeMappingFrame frame = new DatatypeMappingFrame(tree.getDatabase(), dbOld);
          frame.setVisible(true);
          if (frame.getRetVal() == FeedbackEnum.CANCEL) {
            guiLogic.setDatabase(dbOld);
            dbOld.initPropertyChangeListeners();
            TimeLine.getInstance().initialize(dbOld);
            tree.setSelectedItem(0);
            cb.setSelectedItem(dbOld.getType().getName());
          }
        }
      }
    });

    add(btnNewRelation);
    add(btnEmptyRelation);
    add(btnInspect);
    add(cb);
  }

  /**
   * Enable disable inspect button
   *
   * @param enabled true/false
   */
  public void setEnabledInspect(boolean enabled) {
    btnInspect.setEnabled(enabled);
  }


  private void resetAllDataTypes() {
    for (RelationSchema relationSchema : tree.getDatabase().getDatabase()) {
      for (Attribute attribute : relationSchema.getAttributes()) {
        attribute.setType("---");
      }

    }

  }

  public void updateDatatype() {
    cb.setSelectedItem(tree.getDatabase().getType().getName());
  }
}
