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
import dba.gui.auxClasses.FdLogic;
import dba.gui.auxClasses.GuiLogic;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.gui.auxClasses.navBarPanels.DatabaseTreePanel;
import dba.utils.TreeEnum;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class to provide the ToolBar for FDs (extends ToolBar class)
 *
 * @author Andreas Freitag
 */
public class ToolBarFd extends ToolBar {
  private CustomTree tree;
  private FdLogic fdLogic;

  /**
   *
   */
  private static final long serialVersionUID = -3843390455939145286L;

  public ToolBarFd(DatabaseTreePanel dbTreePanel, RelationView relationView, RelationDetailsView relationDetailsView,
                   GuiLogic logic) {
    super(relationView, relationDetailsView, logic);

    tree = dbTreePanel.getTree();
    fdLogic = new FdLogic();

    JButton btnDelete = new JButton(super.getIcons.getTbDelete());
    JButton btnEdit = new JButton(super.getIcons.getTbEdit());

    btnDelete.setToolTipText(super.locale.getString("Delete"));
    btnEdit.setToolTipText(super.locale.getString("Edit"));

    btnDelete.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        delFd();
      }
    });
    KeyStroke delete = KeyStroke.getKeyStroke("DELETE");
    InputMap inputMapDel = btnDelete.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMapDel.put(delete, "Delete");
    ActionMap actionMapDel = btnDelete.getActionMap();
    actionMapDel.put("Delete", new CustomActionListenerDelFd());
    btnDelete.setActionMap(actionMapDel);

    btnEdit.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        int x = tree.getNewSelectedItem(TreeEnum.EditFd);
        fdLogic.editFd();
        tree.setSelectedItem(x);
      }
    });

    add(btnDelete);
    add(btnEdit);
  }

  private void delFd() {
    int x = tree.getNewSelectedItem(TreeEnum.DelFD);
    fdLogic.deleteFd();
    tree.setSelectedItem(x);
  }

  private class CustomActionListenerDelFd extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      delFd();
    }
  }

}
