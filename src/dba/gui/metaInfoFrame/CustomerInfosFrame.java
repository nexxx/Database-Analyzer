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

package dba.gui.metaInfoFrame;

import dba.gui.auxClasses.ExtensionFilter;
import dba.options.Feedback;
import dba.options.FeedbackEnum;
import dba.options.Options;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.Observable;
import dbaCore.data.Database;
import dbaCore.data.Person;
import dbaCore.data.RelationSchema;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observer;

/**
 * Class to print the Customer information frame
 *
 * @author Andreas Freitag
 */
public class CustomerInfosFrame extends JDialog implements Observable {

  /**
   *
   */
  private static final long serialVersionUID = 2585219690194457945L;
  private JDialog frame;
  private JTextArea txtCompany;
  private JTextArea txtAdress;
  private JTextArea txtNotes;
  private CustomTable table;
  private PersonTableModel tableModel;
  private Database database;
  private Localization locale;
  private JButton btnDelete;
  private ListSelectionModel listSelectionModel;
  private JComboBox<String> cbPerson;
  private JComboBox<String> cbRelations;
  private final String DATE_FORMAT_NOW = "EE, dd.MMM yyyy - HH:mm";
  private ArrayList<Observer> observers;

  /**
   * Create the dialog.
   */
  public CustomerInfosFrame(Database db) {
    super();
    database = db;
    locale = Localization.getInstance();
    frame = this;
    frame.setTitle(locale.getString("MI_FrameTitle"));
    frame.setIconImage(GetIcons.getInstance().getIconEditInfosFrame().getImage());
    frame.setModal(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setSize(700, 450);
    frame.setMinimumSize(new Dimension(400, 400));

    observers = new ArrayList<>();

    JPanel contentPanel = new JPanel(new BorderLayout());
    frame.add(contentPanel);
    // contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

    frame.add(createButtonPanel(), BorderLayout.SOUTH);

    JTabbedPane tab = new JTabbedPane();
    tab.addTab(locale.getString("MI_CustInfo"), createCustomerPanel());
    tab.addTab(locale.getString("MI_Notes"), createNotesPanel());
    contentPanel.add(tab, BorderLayout.CENTER);

    setLocationRelativeTo(null);
  }

  private JPanel createButtonPanel() {
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    JButton okButton = new JButton(locale.getString("Ok"));
    okButton.setActionCommand(locale.getString("Ok"));
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        frame.dispose();
        database.setCustCompany(txtCompany.getText());
        database.setCustAdress(txtAdress.getText());
        database.setNotes(txtNotes.getText());
      }
    });
    JButton cancelButton = new JButton(locale.getString("Cancel"));
    cancelButton.setActionCommand(locale.getString("Cancel"));
    buttonPane.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        frame.dispose();
      }
    });
    return buttonPane;
  }

  private JPanel createCustomerPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JPanel pnlCustomer = new JPanel(new MigLayout("wrap 2, filly", "[grow 50][grow 50]"));
    JPanel pnlPersons = new JPanel(new BorderLayout());
    JPanel pnlPersonButton = new JPanel(new GridLayout());

    pnlCustomer.add(new JLabel(locale.getString("MI_Company")), "growx");
    pnlCustomer.add(new JLabel(locale.getString("MI_Address")), "growx");
    txtCompany = new JTextArea(database.getCustCompany());
    txtCompany.setRows(5);
    JScrollPane spCompany = new JScrollPane(txtCompany);
    pnlCustomer.add(spCompany, "grow");
    txtAdress = new JTextArea(database.getCustAdress());
    txtAdress.setRows(5);
    JScrollPane spAdress = new JScrollPane(txtAdress);
    pnlCustomer.add(spAdress, "grow");

    tableModel = new PersonTableModel(database.getPersons());
    //    if (tableModel.getRowCount() == 0) {
    //      tableModel.addRow(new Person());
    //    }
    table = new CustomTable(tableModel);
    table.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName())) {
          fillPersonCombobox();
        }
      }
    });

    JScrollPane scrollpane = new JScrollPane(table);
    table.getTableHeader().setResizingAllowed(false);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.getTableHeader().setReorderingAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pnlPersons.add(scrollpane, BorderLayout.CENTER);
    listSelectionModel = table.getSelectionModel();
    listSelectionModel.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        // ListSelectionModel lsm = (ListSelectionModel)
        // e.getSource();
        btnDelete.setEnabled(!listSelectionModel.isSelectionEmpty());
      }
    });

    btnDelete = new JButton(locale.getString("MI_Delete"));
    btnDelete.setEnabled(false);
    btnDelete.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        tableModel.removeRow(table.getSelectedRow());
        fillPersonCombobox();
      }
    });
    JButton btnExpCSV = new JButton(locale.getString("MI_Export"));
    btnExpCSV.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        exportToCSV();
      }
    });
    JButton btnAdd = new JButton(locale.getString("MI_Add"));
    btnAdd.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        tableModel.addRow(new Person());
      }
    });
    pnlPersonButton.add(btnAdd);
    pnlPersonButton.add(btnDelete);
    pnlPersonButton.add(btnExpCSV);

    panel.add(pnlCustomer, BorderLayout.NORTH);
    panel.add(pnlPersons, BorderLayout.CENTER);
    panel.add(pnlPersonButton, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createNotesPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

    txtNotes = new JTextArea(database.getNotes());
    txtNotes.setLineWrap(true);
    txtNotes.setWrapStyleWord(true);
    JScrollPane scrollBar = new JScrollPane(txtNotes);
    panel.add(scrollBar, BorderLayout.CENTER);

    JButton btnCurrDate = new JButton(locale.getString("MI_CurrDate"));
    btnCurrDate.setToolTipText(locale.getString("MI_CurrDateTooltip"));
    btnCurrDate.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        txtNotes.insert(sdf.format(cal.getTime()), txtNotes.getCaretPosition());
        txtNotes.requestFocus();
      }
    });

    cbPerson = new JComboBox<>();
    fillPersonCombobox();
    cbPerson.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (cbPerson.getItemCount() >= 1 && cbPerson.getSelectedIndex() > 0) {
          txtNotes.insert((String) cbPerson.getSelectedItem(), txtNotes.getCaretPosition());
          cbPerson.setSelectedIndex(0);
          txtNotes.requestFocus();
        }
      }
    });

    cbRelations = new JComboBox<>();
    fillRelationCombobox();
    cbRelations.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (cbRelations.getItemCount() >= 1 && cbRelations.getSelectedIndex() > 0) {
          txtNotes.insert((String) cbRelations.getSelectedItem(), txtNotes.getCaretPosition());
          cbRelations.setSelectedIndex(0);
          txtNotes.requestFocus();
        }
      }
    });

    GridLayout layout = new GridLayout(1, 3);
    JPanel pnlButtons = new JPanel(layout);
    pnlButtons.add(btnCurrDate);
    pnlButtons.add(cbPerson);
    pnlButtons.add(cbRelations);
    panel.add(pnlButtons, BorderLayout.SOUTH);

    return panel;
  }

  private void fillPersonCombobox() {
    cbPerson.removeAllItems();
    cbPerson.addItem(locale.getString("MI_Persons"));
    for (Person p : database.getPersons()) {
      cbPerson.addItem(p.getName());
    }
  }

  private void fillRelationCombobox() {
    cbRelations.removeAllItems();
    cbRelations.addItem(locale.getString("Relations"));
    for (RelationSchema d : database.getDatabase()) {
      cbRelations.addItem(d.getName());
    }
  }

  private void exportToCSV() {
    JFileChooser fc = new JFileChooser(Options.getInstance().getExportFolder());
    FileFilter type = new ExtensionFilter(".csv", ".csv");
    fc.addChoosableFileFilter(type);
    fc.setFileFilter(type);
    fc.setAcceptAllFileFilterUsed(false);
    int returnVal = fc.showSaveDialog(fc);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String path;
      try {
        path = fc.getSelectedFile().getCanonicalPath();
        FeedbackEnum ret = saveWithFilePicker(path);
        switch (ret) {
          case SUCCESSFUL:
            notify(new Feedback(locale.getString("FB_Export"), FeedbackEnum.SUCCESSFUL));
            break;
          case FAILED:
            notify(new Feedback(locale.getString("FB_ExportFailed"), FeedbackEnum.FAILED));
            break;

          default:
            break;
        }

      } catch (IOException e) {
        notify(new Feedback(locale.getString("FB_ExportFailed"), FeedbackEnum.FAILED));
      }

    }
  }

  private FeedbackEnum saveWithFilePicker(String path) {
    File outputFile = new File(path);
    FeedbackEnum returnVal = FeedbackEnum.FAILED;

    if (!path.endsWith(".csv")) {
      outputFile = new File(path + ".csv");
    }

    if (outputFile.exists()) {
      Object[] options = {locale.getString("Yes"), locale.getString("No")};
      int result = JOptionPane.showOptionDialog(null, locale.getString("GUI_TheFile") + " " + outputFile.getName() +
        " " + locale.getString("GUI_AlreadyExisting"), locale.getString("Confirm"), JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      switch (result) {
        case JOptionPane.YES_OPTION:
          returnVal = writeFile(outputFile);
          break;
        case JOptionPane.NO_OPTION:
          exportToCSV();
      }

    } else {
      returnVal = writeFile(outputFile);
    }
    return returnVal;
  }

  private FeedbackEnum writeFile(File outputFile) {
    FeedbackEnum returnVal;
    try {
      FileWriter writer = new FileWriter(outputFile);
      String toWrite = "Name, Job, E-Mail, Telefon, Fax\n";
      for (Person p : database.getPersons()) {
        toWrite = toWrite + p.getName() + ",";
        toWrite = toWrite + p.getJob() + ",";
        toWrite = toWrite + p.getMail() + ",";
        toWrite = toWrite + p.getTel() + ",";
        toWrite = toWrite + p.getFax() + "\n";
      }
      writer.write(toWrite);
      writer.close();
      returnVal = FeedbackEnum.SUCCESSFUL;
    } catch (Exception e) {
      returnVal = FeedbackEnum.FAILED;
    }
    return returnVal;
  }

  // Observable Methods
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  private void notify(Object arg) {
    for (Observer observer : observers) {
      observer.update(null, arg);
    }
  }
}
