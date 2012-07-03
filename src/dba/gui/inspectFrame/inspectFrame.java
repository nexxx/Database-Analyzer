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

package dba.gui.inspectFrame;

import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.RelationInspectorGui;
import dbaCore.data.Database;
import dbaCore.data.RelationSchema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class to provide the Inspect Frame
 *
 * @author Andreas Freitag
 */
public class inspectFrame extends JDialog {

  private static final long serialVersionUID = 3550885401691020211L;
  private final JPanel contentPanel = new JPanel();
  private JDialog jDialog;
  private Localization locale;

  /**
   * Defaultconstructor to create the frame
   */
  @SuppressWarnings("static-access")
  private inspectFrame() {
    super();
    locale = Localization.getInstance();
    GetIcons getIcons = GetIcons.getInstance();

    jDialog = this;
    jDialog.setModal(true);
    jDialog.setIconImage(getIcons.getIconInspectFrame().getImage());

    setSize(450, 300);
    setMinimumSize(new Dimension(450, 300));
    setLocationRelativeTo(null);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setLayout(new BorderLayout());
    // contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    this.setDefaultCloseOperation(jDialog.DISPOSE_ON_CLOSE);
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
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
      }
    }

  }

  /**
   * Create the dialog.
   */
  public inspectFrame(RelationSchema relation) {
    this();
    jDialog.setTitle(locale.getString("TREE_RelInspect"));
    JScrollPane scrollPane = initRelInspectPane(relation);
    contentPanel.add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * Create the dialog.
   */
  public inspectFrame(Database database) {
    this();
    jDialog.setTitle(locale.getString("TREE_DBInspect"));
    JScrollPane scrollPane = initDbInspectPane(database);
    contentPanel.add(scrollPane, BorderLayout.CENTER);
  }

  private JScrollPane initRelInspectPane(RelationSchema relation) {
    RelationInspectorGui inspector = new RelationInspectorGui();
    String result = inspector.inspectRelation(relation);
    JTextArea resultField = new JTextArea(result);
    resultField.setEditable(false);

    return new JScrollPane(resultField);
  }

  private JScrollPane initDbInspectPane(Database database) {
    String result = "";
    for (RelationSchema relation : database.getDatabase()) {
      RelationInspectorGui inspector = new RelationInspectorGui();
      result = result + relation.getName() + ":\n";
      result = result + inspector.inspectRelation(relation) + "\n";
    }

    JTextArea resultField = new JTextArea(result);
    resultField.setEditable(false);

    return new JScrollPane(resultField);
  }
}
