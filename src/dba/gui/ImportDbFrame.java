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

package dba.gui;

import dba.gui.auxClasses.GuiLogic;
import dba.options.FeedbackEnum;
import dba.options.Options;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dbaCore.data.Database;
import dbaCore.data.TimeLine;
import dbaCore.data.dBTypes.TypeEnum;
import dbaCore.dbConnection.DbConnection;
import net.miginfocom.swing.MigLayout;

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
public class ImportDbFrame extends JDialog {
  private JDialog jDialog;
  private Database database;
  private GuiLogic guiLogic;
  private FeedbackEnum retVal;
  private Localization locale;
  private JComboBox<String> cb;
  private JTextField txtAdress;
  private JTextField txtUser;
  private JTextField txtPassword;
  private Options options;

  public ImportDbFrame(GuiLogic gL) {
    super();
    options = Options.getInstance();
    retVal = FeedbackEnum.CANCEL;
    jDialog = this;
    database = new Database();
    guiLogic = gL;
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
        jDialog.dispose();
      }
    });
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);


    JButton importButton = new JButton(locale.getString("Import"));
    buttonPane.add(importButton);
    importButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        importEvent();
      }
    });

    JButton cancelButton = new JButton(locale.getString("Cancel"));
    buttonPane.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jDialog.dispose();
      }
    });

    JPanel panel = new JPanel(new MigLayout("wrap 2", "[fill, grow, 90:90:90][fill, grow]"));
    contentPanel.add(panel, BorderLayout.CENTER);

    txtPassword = new JPasswordField("", 32);
    txtUser = new JTextField(options.getLastUser(), 32);

    cb = new JComboBox<>();
    for (TypeEnum e : TypeEnum.values()) {
      if (e.getName().equalsIgnoreCase("MYSQL") || e.getName().equalsIgnoreCase("SQLite3")) {
        cb.addItem(e.getName());
      }
    }
    cb.setToolTipText(locale.getString("IF_CBTooltip"));
    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (((String) cb.getSelectedItem()).equalsIgnoreCase("SQLite3")) {
          txtPassword.setEnabled(false);
          txtUser.setEnabled(false);
        } else {
          txtPassword.setEnabled(true);
          txtUser.setEnabled(true);
        }
      }
    });
    cb.setSelectedItem(options.getLastType());
    panel.add(cb, "spanx");

    JLabel lblAdress = new JLabel(locale.getString("IF_Adress"));
    panel.add(lblAdress);
    txtAdress = new JTextField(options.getLastAdress(), 32);
    txtAdress.setToolTipText(locale.getString("IF_AdressTooltip"));
    txtAdress.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        importEvent();
      }
    });
    panel.add(txtAdress);

    JLabel lblUser = new JLabel(locale.getString("IF_User"));
    panel.add(lblUser);
    txtUser.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        importEvent();
      }
    });
    panel.add(txtUser);

    JLabel lblPassword = new JLabel(locale.getString("IF_Pwd"));
    panel.add(lblPassword);
    txtPassword.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        importEvent();
      }
    });
    panel.add(txtPassword);

    pack();
    setLocationRelativeTo(null);

    txtAdress.selectAll();
    txtAdress.requestFocus();
  }

  public FeedbackEnum getRetVal() {
    return retVal;
  }

  private void importEvent() {
    DbConnection dbc;

    String adress = txtAdress.getText();
    String user = txtUser.getText();
    String pwd = txtPassword.getText();
    TypeEnum type = TypeEnum.getEnumByValue((String) cb.getSelectedItem());

    String fullAdress = null;
    switch (type) {
      case MYSQL:
        fullAdress = "jdbc:mysql://" + adress;
        break;
      case POSTGRES:
        break;
      case MSDB:
        break;
      case ORACLE:
        break;
      case SQLITE:
        fullAdress = "jdbc:sqlite:" + adress;
        break;
    }

    try {
      dbc = new DbConnection(user, pwd, type, fullAdress);
    } catch (Exception e) {
      //e.printStackTrace();
      JOptionPane.showMessageDialog(null, locale.getString("IF_ErrorMsg"), locale.getString("IF_ErrorTitle"),
        JOptionPane.ERROR_MESSAGE);
      return;
    }

    database = dbc.getDatabase();
    guiLogic.setDatabase(database);

    database.initPropertyChangeListeners();
    TimeLine.getInstance().setDirty(false);
    TimeLine.getInstance().initialize(database);
    retVal = FeedbackEnum.SUCCESSFUL;

    options.setLastAdress(txtAdress.getText());
    options.setLastUser(txtUser.getText());
    options.setLastType((String) cb.getSelectedItem());
    options.writeOptions();

    jDialog.dispose();
  }
}
