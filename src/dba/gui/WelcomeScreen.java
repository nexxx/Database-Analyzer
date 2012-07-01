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

import data.Database;
import data.dBTypes.TypeEnum;
import dba.gui.auxClasses.GuiLogic;
import dba.options.FeedbackEnum;
import dba.utils.GetIcons;
import dba.utils.Localization;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * (Description)
 *
 * @author Andreas Freitag
 */
public class WelcomeScreen extends JDialog {

  private static final long serialVersionUID = 3804928181363080738L;
  private JDialog jDialog;
  private GuiLogic guiLogic;
  private JComboBox<String> cb;
  private Database database;
  private boolean open;
  private FeedbackEnum retVal;


  /**
   * Defaultconstructor to create the dialog.
   */
  public WelcomeScreen(GuiLogic gL, Database db) {
    super();
    open = true;
    guiLogic = gL;
    database = db;
    Localization locale = Localization.getInstance();
    this.setTitle("Welcome to Database Analyzer");
    GetIcons getIcons = GetIcons.getInstance();
    ImageIcon iconFrame = getIcons.getIconFrame();
    this.setIconImage(iconFrame.getImage());
    jDialog = this;
    setModal(true);
    getContentPane().setLayout(new BorderLayout());
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton okButton = new JButton(locale.getString("Ok"));
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        database.setType(TypeEnum.getEnumByValue((String) cb.getSelectedItem()));
        open = false;
        jDialog.dispose();
      }
    });

    JButton cancelButton = new JButton(locale.getString("Cancel"));
    buttonPane.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        System.exit(0);
      }
    });

    JPanel panel = new JPanel(new MigLayout("fillx"));
    contentPanel.add(panel, BorderLayout.CENTER);

    JLabel lblWelcome = new JLabel("Welcome to");
    lblWelcome.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    JLabel lblDba = new JLabel("Database Analyzer");
    lblDba.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
    panel.add(lblWelcome, "spanx, growx");
    panel.add(lblDba, "spanx, growx");
    JButton btnOpen = new JButton("Open Existing Project");
    btnOpen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        retVal = guiLogic.open();
        open = true;
        jDialog.dispose();
      }
    });
    panel.add(btnOpen, "spanx, growx");

    panel.add(new JSeparator(JSeparator.VERTICAL), "growx, spanx");

    JLabel lblType = new JLabel("Please select the Database type:");
    panel.add(lblType, "growx, spanx");

    cb = new JComboBox<>();
    for (TypeEnum e : TypeEnum.values()) {
      cb.addItem(e.getName());
    }

    panel.add(cb, "growx, spanx");


    pack();
    setLocationRelativeTo(null);

  }

  public void showScreen() {
    jDialog.setVisible(true);
  }

  public boolean openClicked() {
    return open;
  }

  public FeedbackEnum getRetVal() {
    return retVal;
  }

}
