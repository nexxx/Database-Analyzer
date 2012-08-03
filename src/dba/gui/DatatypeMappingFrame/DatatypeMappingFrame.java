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

package dba.gui.DatatypeMappingFrame;

import dba.options.FeedbackEnum;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dbaCore.data.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * (Description)
 *
 * @author Andreas Freitag
 */
public class DatatypeMappingFrame extends JDialog {
  private JDialog jDialog;
  private FeedbackEnum retVal;
  private Database database;
  private Database databaseOld;
  private Localization locale;

  public DatatypeMappingFrame(Database db, Database dbOld) {
    super();
    jDialog = this;
    retVal = FeedbackEnum.CANCEL;
    database = db;
    databaseOld = dbOld;
    locale = Localization.getInstance();
    this.setTitle(locale.getString("WSC_Title"));
    GetIcons getIcons = GetIcons.getInstance();
    ImageIcon iconFrame = getIcons.getIconFrame();
    this.setIconImage(iconFrame.getImage());
    jDialog = this;
    setModal(true);
    getContentPane().setLayout(new BorderLayout());
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        retVal = FeedbackEnum.CANCEL;
        jDialog.dispose();
      }
    });
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);


    JButton okButton = new JButton(locale.getString("Ok"));
    buttonPane.add(okButton);
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        //DO SOMETHING
        retVal = FeedbackEnum.SUCCESSFUL;
        jDialog.dispose();

      }
    });

    JButton cancelButton = new JButton(locale.getString("Cancel"));
    buttonPane.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        retVal = FeedbackEnum.CANCEL;
        jDialog.dispose();
      }
    });

    JPanel panel = new JPanel(new BorderLayout());
    contentPanel.add(panel, BorderLayout.CENTER);

    JTable table = new JTable();


    setSize(400, 400);
    setLocationRelativeTo(null);
  }

  public FeedbackEnum getRetVal() {
    return retVal;
  }
}
