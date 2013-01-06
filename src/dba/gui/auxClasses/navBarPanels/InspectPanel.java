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

package dba.gui.auxClasses.navBarPanels;

import dba.gui.CustomTree;
import dba.utils.RelationInspectorGui;
import dbaCore.data.Database;
import dbaCore.data.RelationSchema;

import javax.swing.*;
import java.awt.*;

/**
 * Panel which contains the inspect sumary.
 * For use in Navigation Tabbedpane
 *
 * @author Andreas Freitag
 */
public class InspectPanel extends JPanel {
  private JTextArea resultField;
  private JScrollPane scrollpane;


  public InspectPanel() {
    super();
    this.setLayout(new BorderLayout());

    resultField = new JTextArea(getInspectText());
    resultField.setEditable(false);

    scrollpane = new JScrollPane(resultField);
    this.add(scrollpane);
  }

  /**
   * Set update the Text inside the inspect panel
   */
  public void updateScrollpane() {
    resultField.setText(getInspectText());
  }

  private String getInspectText() {
    Database database = CustomTree.getInstance().getDatabase();
    String result = "";
    for (RelationSchema relation : database.getDatabase()) {
      RelationInspectorGui inspector = new RelationInspectorGui();
      result = result + relation.getName() + ":\n";
      result = result + inspector.inspectRelation(relation) + "\n";
    }
    return result;
  }
}
