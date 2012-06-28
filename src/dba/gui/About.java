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

import dba.utils.GetIcons;
import dba.utils.Localization;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Class to show the JDialog for the about Window
 *
 * @author Andreas Freitag
 */
public class About extends JDialog {

  /**
   *
   */
  private static final long serialVersionUID = 3383162762318308818L;
  private JDialog jDialog;

  /**
   * Create the dialog.
   */
  @SuppressWarnings("static-access")
  public About() {
    super();
    Localization locale = Localization.getInstance();
    GetIcons getIcons = GetIcons.getInstance();

    jDialog = this;
    jDialog.setTitle(locale.getString("GUI_About"));
    jDialog.setModal(true);
    jDialog.setResizable(false);
    jDialog.setIconImage(getIcons.getIconAboutFrame().getImage());

    getContentPane().setLayout(new BorderLayout());
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    this.setDefaultCloseOperation(jDialog.DISPOSE_ON_CLOSE);
    getContentPane().add(contentPanel, BorderLayout.CENTER);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jDialog.dispose();
      }
    });

    Properties prop = new Properties();
    try {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream("res/version_num.properties");
      prop.load(is);
      is.close();
    } catch (NullPointerException | IOException e) {
      e.printStackTrace();
    }

    String version_number = "v" + prop.getProperty("versionnumber") +
      " " + prop.getProperty("state");

    String text = "<html><FONT FACE=Courier New>Database " +
      "Analyzer<br>" + version_number + "<br><br>Authors: " +
      "Andreas Freitag, Sebastian Theuermann<br><br>DBA is " +
      "licensed under the GPL v3 license.<br>For more informations " +
      "visit: " + "<br>http://www.gnu.org/licenses/</html>";
    JLabel lblText = new JLabel(text);
    lblText.setBackground(Color.white);
    lblText.setOpaque(true);
    lblText.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.add(lblText, BorderLayout.CENTER);
    jDialog.pack();

    setLocationRelativeTo(null);
  }
}
