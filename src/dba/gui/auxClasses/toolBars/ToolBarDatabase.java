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
import dba.gui.auxClasses.DatabaseLogic;
import dba.gui.auxClasses.DatabaseTreePanel;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.utils.TreeEnum;
import dbaCore.data.Attribute;
import dbaCore.data.RelationSchema;
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
  /**
   *
   */
  private static final long serialVersionUID = -3843390455939145286L;

  public ToolBarDatabase(DatabaseTreePanel dbTreePanel, RelationView relationView,
                         RelationDetailsView relationDetailsView) {
    super(relationView, relationDetailsView, dbTreePanel);

    tree = dbTreePanel.getTree();
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
    cb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (!((String) cb.getSelectedItem()).equalsIgnoreCase(tree.getDatabase().getType().getName())) {
          tree.getDatabase().setType(TypeEnum.getEnumByValue((String) cb.getSelectedItem()));
          resetAllDataTypes();
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
}
