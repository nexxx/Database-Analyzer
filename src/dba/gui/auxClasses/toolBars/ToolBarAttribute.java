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
import dba.gui.auxClasses.AttrLogic;
import dba.gui.auxClasses.GuiLogic;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.utils.TreeEnum;
import dbaCore.data.Attribute;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class to provide the ToolBar for Attributes (extends ToolBar class)
 *
 * @author Andreas Freitag
 */
public class ToolBarAttribute extends ToolBar {
  private AttrLogic attrLogic;
  private CustomTree tree;
  private static final long serialVersionUID = -3843390455939145286L;
  private JToggleButton btnPK;
  private JToggleButton btnFK;
  private JTextField txtConstraints;

  public ToolBarAttribute(RelationView relationView, RelationDetailsView relationDetailsView, GuiLogic logic) {
    super(relationView, relationDetailsView, logic);

    attrLogic = new AttrLogic();
    tree = CustomTree.getInstance();

    JButton btnDelete = new JButton(super.getIcons.getTbDelete());
    JButton btnRename = new JButton(super.getIcons.getTbRename());
    btnPK = new JToggleButton(super.getIcons.getTbPK());
    btnFK = new JToggleButton(super.getIcons.getTbFK());

    btnDelete.setToolTipText(super.locale.getString("Delete"));
    btnRename.setToolTipText(super.locale.getString("Rename"));
    btnPK.setToolTipText(super.locale.getString("TREE_AttrChkBoxPK"));
    btnFK.setToolTipText(super.locale.getString("TREE_AttrChkBoxFK"));


    btnDelete.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.DelAttribute);
        attrLogic.deleteAttribute();
        tree.setSelectedItem(x);
      }
    });

    btnRename.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.RenameAttr);
        attrLogic.renameAttribute();
        tree.setSelectedItem(x);
      }
    });

    btnPK.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.TogglePk);
        attrLogic.togglePK();
        tree.setSelectedItem(x);
      }
    });

    btnFK.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.ToggleFk);
        attrLogic.toggleFK();
        tree.setSelectedItem(x);
        updateElements();
      }
    });

    txtConstraints = new JTextField("", 60);
    txtConstraints.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        int x = tree.getNewSelectedItem(TreeEnum.SetConstraints);
        attrLogic.setConstraints(txtConstraints.getText());
        tree.setSelectedItem(x);
      }
    });
    add(btnDelete);
    add(btnRename);
    add(btnPK);
    add(btnFK);
    add(new JLabel(locale.getString("AttrConst") + ": "));
    add(txtConstraints);
  }

  /**
   * Method which updates the Checkboxes in the JPopupMenu according
   * to the attributes PK/FK status
   */
  public void updateElements() {
    Attribute attr = tree.getAttribute();

    if (attr.getIsPrimaryKey()) {
      btnPK.setSelected(true);
    } else {
      btnPK.setSelected(false);
    }

    if (attr.getIsForeignKey()) {
      btnFK.setSelected(true);
    } else {
      btnFK.setSelected(false);
    }

    txtConstraints.setText(attr.getConstraints());
  }
}
