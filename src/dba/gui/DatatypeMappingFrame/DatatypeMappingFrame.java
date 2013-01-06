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

import dba.gui.CustomTree;
import dba.options.FeedbackEnum;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dbaCore.data.Database;
import dbaCore.data.RelationSchema;
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
    this.setTitle(locale.getString("DTM_Title"));
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
        CustomTree.getInstance().setSelectedItem(0);
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
        retVal = FeedbackEnum.SUCCESSFUL;
        CustomTree.getInstance().setSelectedItem(0);
        jDialog.dispose();

      }
    });

    JButton cancelButton = new JButton(locale.getString("Cancel"));
    buttonPane.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        retVal = FeedbackEnum.CANCEL;
        CustomTree.getInstance().setSelectedItem(0);
        jDialog.dispose();
      }
    });

    JPanel panel = new JPanel(new MigLayout("fillx, wrap 1"));
    JScrollPane scrollPane = new JScrollPane(panel);
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    if (database.getDatabase().isEmpty()) {
      panel.add(new JLabel(locale.getString("DTM_ReallyApply")));
    } else {
      for (RelationSchema relation : database.getDatabase()) {
        panel.add(new JLabel(relation.getName()));
        DTMTableModel tableModel = new DTMTableModel(relation.getAttributes(), databaseOld.getRelationSchemaByName
          (relation.getName()).getAttributes(), database.getType(), databaseOld.getType());
        JTable table = new JTable(tableModel);

        //DbType dbType = (new DbTypeFactory(CustomTree.getInstance().getDatabase())).getType();
        //JComboBox<String> comboBox = dbType.getCombobox();
        //TableColumn col = table.getColumnModel().getColumn(2);
        //col.setCellEditor(new DefaultCellEditor(comboBox));

        JScrollPane scrollpane = new JScrollPane(table);
        setVisibleRowCount(table, table.getRowCount());
        table.getTableHeader().setResizingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(scrollpane);
      }
    }

    pack();
    setLocationRelativeTo(null);
  }

  public FeedbackEnum getRetVal() {
    return retVal;
  }

  private void setVisibleRowCount(JTable table, int rows) {
    int height = 0;
    for (int row = 0; row < rows; row++) {
      height += table.getRowHeight(row);
    }

    table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredScrollableViewportSize().width, height));
  }
}
