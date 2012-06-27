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

package dba.gui.auxClasses;

import data.*;
import dba.data.fileIO.ReadFromXML;
import dba.data.fileIO.SaveToXml;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.options.FeedbackEnum;
import dba.options.Options;
import dba.utils.Localization;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

public class GuiLogic {
  private static Database database;
  private DatabaseTreePanel dbTree;
  private Localization locale;
  private Options options;
  private static String lastFileName;
  private static String lastFileNameBackup;
  private CustomTree tree;
  private static ArrayList<Observer> observers = new ArrayList<>();
  private Configuration cfg;


  public GuiLogic(DatabaseTreePanel dbTreePanel) {
    super();
    tree = CustomTree.getInstance();
    database = tree.getDatabase();
    dbTree = dbTreePanel;
    locale = Localization.getInstance();
    lastFileName = null;
    lastFileNameBackup = null;
    options = Options.getInstance();

    //freemaker init
    cfg = new Configuration();
    try {
      cfg.setClassForTemplateLoading(this.getClass(), "/res/templates");
    } catch (Exception e) {
      //TODO Handle catch clause!
    }
    cfg.setObjectWrapper(new DefaultObjectWrapper());
  }

  /**
   * Loads the database from the xml file
   */
  public FeedbackEnum open() {
    int result;
    if (TimeLine.getInstance().getCurrentElement().isDirty()) {
      Object[] options = {locale.getString("GUI_Yes"),
              locale.getString("GUI_No"), locale.getString("TREE_Cancel")};
      result = JOptionPane.showOptionDialog(null,
              locale.getString("TREE_NewMsg"), locale.getString("TREE_NewTitle"),
              JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
              options, options[2]);
      switch (result) {
        case JOptionPane.YES_OPTION:
          save();
          openDB();
          return FeedbackEnum.SUCCESSFUL;
        case JOptionPane.NO_OPTION:
          openDB();
          return FeedbackEnum.SUCCESSFUL;
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    } else {
      openDB();
      return FeedbackEnum.SUCCESSFUL;
    }
    return FeedbackEnum.CANCEL;
  }

  private FeedbackEnum openDB() {
    String path;

    JFileChooser fc = new JFileChooser(options.getSaveFolder());
    FileFilter type = new ExtensionFilter(".xml", ".xml");
    fc.addChoosableFileFilter(type);
    fc.setFileFilter(type);
    fc.setAcceptAllFileFilterUsed(false);
    int returnVal = fc.showOpenDialog(fc);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      try {
        path = fc.getSelectedFile().getCanonicalPath();
        File inputFile = new File(path);
        ReadFromXML reader = new ReadFromXML();
        try {
          dbTree.disselectItems();
          database = reader.ReadDbNow(inputFile);
          dbTree.setDatabase(database);
          database.initPropertyChangeListeners();
          database.setDirty(false);
          lastFileName = path;
          lastFileNameBackup = path;
          TimeLine.getInstance().addHistoricObject(database);

          tree.setSelectionRow(0);
          return FeedbackEnum.SUCCESSFUL;

        } catch (Exception e) {
          return FeedbackEnum.FAILED;
        }

      } catch (IOException ex) {
        return FeedbackEnum.FAILED;
      }
    } else {
      return FeedbackEnum.CANCEL;
    }

  }

  /**
   * Saves the database to the xml file
   */
  public FeedbackEnum save() {
    if (lastFileName == null) {
      JFileChooser fc = new JFileChooser(options.getSaveFolder());
      FileFilter type = new ExtensionFilter(".xml", ".xml");
      fc.addChoosableFileFilter(type);
      fc.setFileFilter(type);
      fc.setAcceptAllFileFilterUsed(false);
      int returnVal = fc.showSaveDialog(fc);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          String path = fc.getSelectedFile().getCanonicalPath();
          FeedbackEnum ret = saveWithFilePicker(path);
          lastFileName = path;
          return ret;
        } catch (IOException ex) {
          return FeedbackEnum.FAILED;
        }
      } else {
        lastFileName = lastFileNameBackup;
        return FeedbackEnum.CANCEL;
      }
    } else {
      return saveWithoutFilePicker(lastFileName);
    }
  }

  public FeedbackEnum saveAs() {
    lastFileName = null;
    return save();
  }

  private FeedbackEnum saveWithoutFilePicker(String path) {
    File outputFile = new File(path);
    if (!path.endsWith(".xml")) {
      outputFile = new File(path + ".xml");
    }
    SaveToXml saveToXML = new SaveToXml();
    try {
      saveToXML.SaveDbNow(database, outputFile);
      database.setDirty(false);
      return FeedbackEnum.SUCCESSFUL;
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
  }

  private FeedbackEnum saveWithFilePicker(String path) {
    File outputFile = new File(path);
    FeedbackEnum returnVal = FeedbackEnum.FAILED;

    if (!path.endsWith(".xml")) {
      outputFile = new File(path + ".xml");
    }

    if (outputFile.exists()) {
      Object[] options = {locale.getString("GUI_Yes"),
              locale.getString("GUI_No")};
      int result = JOptionPane.showOptionDialog(null,
              locale.getString("GUI_TheFile") + " " + outputFile.getName() + " "
                      + locale.getString("GUI_AlreadyExisting"),
              locale.getString("GUI_SaveTitle"), JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      switch (result) {
        case JOptionPane.YES_OPTION:
          SaveToXml saveToXML = new SaveToXml();
          try {
            saveToXML.SaveDbNow(database, outputFile);
            returnVal = FeedbackEnum.SUCCESSFUL;
          } catch (Exception e) {
            returnVal = FeedbackEnum.FAILED;
          }
          break;
        case JOptionPane.NO_OPTION:
          returnVal = save();
      }

    } else {
      SaveToXml saveToXML = new SaveToXml();
      try {
        saveToXML.SaveDbNow(database, outputFile);
        returnVal = FeedbackEnum.SUCCESSFUL;
      } catch (Exception e) {
        returnVal = FeedbackEnum.FAILED;
      }
    }
    database.setDirty(false);
    return returnVal;
  }

  public FeedbackEnum newDatabase() {
    int result;
    if (TimeLine.getInstance().getCurrentElement().isDirty()) {
      Object[] options = {locale.getString("GUI_Yes"),
              locale.getString("GUI_No"), locale.getString("TREE_Cancel")};
      result = JOptionPane.showOptionDialog(null,
              locale.getString("TREE_NewMsg"), locale.getString("TREE_NewTitle"),
              JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
              options, options[2]);
      switch (result) {
        case JOptionPane.YES_OPTION:
          save();
          createNewDb();
          return FeedbackEnum.SUCCESSFUL;
        case JOptionPane.NO_OPTION:
          createNewDb();
          return FeedbackEnum.SUCCESSFUL;
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    } else {
      createNewDb();
      return FeedbackEnum.SUCCESSFUL;
    }
    return FeedbackEnum.CANCEL;

  }

  private void createNewDb() {
    database = new Database();
    dbTree.setDatabase(database);
    database.initPropertyChangeListeners();
    database.setDirty(false);
    lastFileName = null;
    TimeLine.getInstance().addHistoricObject(database);
    tree.setSelectedItem(0);
  }

  public FeedbackEnum export() {
    String path;
    JFileChooser fc = new JFileChooser(options.getExportFolder());
    fc.setDialogTitle(locale.getString("GUI_Export"));
    FileFilter typeImage = new ExtensionFilter(".png", ".png");
    FileFilter typeText = new ExtensionFilter(".txt", ".txt");
    FileFilter typeHtml = new ExtensionFilter(".html", ".html");
    fc.addChoosableFileFilter(typeImage);
    fc.addChoosableFileFilter(typeText);
    fc.addChoosableFileFilter(typeHtml);
    fc.setFileFilter(typeHtml);
    fc.setAcceptAllFileFilterUsed(false);
    int returnVal = fc.showSaveDialog(fc);

    if (returnVal == JFileChooser.APPROVE_OPTION) {

      try {
        path = fc.getSelectedFile().getCanonicalPath();
        if (fc.getFileFilter() == typeImage) {
          if (!path.endsWith(".png")) {
            path = path + ".png";
          }
          notifyObservers(path);
        } else if (fc.getFileFilter() == typeText) {
          if (!path.endsWith(".txt")) {
            path = path + ".txt";
          }
          return exportText(path);
        } else if (fc.getFileFilter() == typeHtml) {

          return exportHtml(path);
        } else {
          // Should not happen
        }

      } catch (IOException e1) {
        return FeedbackEnum.FAILED;
      }
      return FeedbackEnum.SUCCESSFUL;
    } else {
      return FeedbackEnum.CANCEL;
    }

  }

  private FeedbackEnum exportText(String path) {
    File outputFile = new File(path);

    if (!path.endsWith(".xml")) {
      outputFile = new File(path + ".xml");
    }

    if (outputFile.exists()) {
      Object[] options = {locale.getString("GUI_Yes"),
              locale.getString("GUI_No")};
      int result = JOptionPane.showOptionDialog(null,
              locale.getString("GUI_TheFile") + " " + outputFile.getName() + " "
                      + locale.getString("GUI_AlreadyExisting"), "Export",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
              options, options[1]);

      switch (result) {
        case JOptionPane.YES_OPTION:
          return writeText(path);
        case JOptionPane.NO_OPTION:
          return export();
        default:
          return FeedbackEnum.CANCEL;
      }
    } else {
      return writeText(path);
    }
  }

  private FeedbackEnum exportHtml(String path) {
    File outputFile = new File(path);

    if (!path.endsWith(".html")) {
      outputFile = new File(path + ".html");
    }

    if (outputFile.exists()) {
      Object[] options = {locale.getString("GUI_Yes"),
              locale.getString("GUI_No")};
      int result = JOptionPane.showOptionDialog(null,
              locale.getString("GUI_TheFile") + " " + outputFile.getName() + " "
                      + locale.getString("GUI_AlreadyExisting"), "Export",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
              options, options[1]);

      switch (result) {
        case JOptionPane.YES_OPTION:
          return writeText(path);
        case JOptionPane.NO_OPTION:
          return export();
        default:
          return FeedbackEnum.CANCEL;
      }
    } else {
      return writeHtml(path);
    }
  }

  private FeedbackEnum writeText(String path) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(path));
      for (RelationSchema relation : database.getDatabase()) {
        out.write(relation.toString() + "\n");
        for (FunctionalDependency fd : relation.getFunctionalDependencies()) {
          out.write("(" + fd.toString() + ")" + "\n");
        }
        out.write("\n");
      }
      out.close();
      return FeedbackEnum.SUCCESSFUL;
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
  }

  public class ExtensionFilter extends FileFilter {
    private String extensions[];

    private String description;

    public ExtensionFilter(String description, String extension) {
      this(description, new String[]{extension});
    }

    public ExtensionFilter(String description, String extensions[]) {
      super();
      this.description = description;
      this.extensions = extensions.clone();
    }

    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      String path = file.getAbsolutePath();
      for (String ext : extensions) {
        if (path.endsWith(ext)
                && path.charAt(path.length() - ext.length()) == '.') {
          return true;
        }
      }
      return false;
    }

    @Override
    public String getDescription() {
      return description == null ? extensions[0] : description;
    }
  }

  // Observer methods

  /**
   * Add a Observer to the Collection
   *
   * @param observer the observer to add
   * @return true/false
   */
  public boolean addObserver(Observer observer) {
    return observers.add(observer);
  }

  /**
   * Notifies Observers about change
   */
  private void notifyObservers(Object argument) {
    for (Observer stalker : observers) {
      stalker.update(null, argument);
    }
  }

  public void undo() {
    if (!TimeLine.getInstance().travelBackward()) {
      FeedbackbarPanel.getInstance().showFeedback(
              locale.getString("FB_UndoFailed"), FeedbackEnum.FAILED);
    }
    tree.setSelectedItem(0);
  }

  public void redo() {
    if (!TimeLine.getInstance().travelForward()) {
      FeedbackbarPanel.getInstance().showFeedback(
              locale.getString("FB_RedoFailed"), FeedbackEnum.FAILED);
    }
    tree.setSelectedItem(0);
  }

  /**
   * Create needed Folders, write Images and HTML File
   *
   * @return FeedbackEnum
   */
  private FeedbackEnum writeHtml(String path) {
    String folder;

    folder = path;
    if (!path.endsWith(".html")) {
      path = path + ".html";
    }

    if (makeDirs(folder) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    if (writeIndex(folder, path) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    if (writeNotes(folder) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    if (writeContacts(folder) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    if (writeImages(folder) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    if (writeTextDescription(folder) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum makeDirs(String folder) {
    boolean success = (
            new File(folder)).mkdir();
    if (!success) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum writeIndex(String folder, String path) {
    File absFolder = new File(folder);

    Map root = new HashMap();

    String company = CustomTree.getInstance().getDatabase()
            .getCustCompany();
    company = company.replace("\n", "<br>");
    String address = CustomTree.getInstance().getDatabase()
            .getCustAdress();
    address = address.replace("\n", "<br>");

    root.put("CustInfo", company);
    root.put("CustAddress", address);
    root.put("NotesUrl", absFolder.getName() + "/Notes.html");
    root.put("ContactsUrl", absFolder.getName() + "/Contacts.html");
    root.put("RelViewUrl", absFolder.getName() + "/Relations.html");
    root.put("FdsViewUrl", absFolder.getName() + "/FDs.html");
    root.put("TextUrl", absFolder.getName() + "/txtDescription.html");

    Template temp;
    try {
      temp = cfg.getTemplate("template_index.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(path));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum writeNotes(String folder) {
    Map root = new HashMap();

    String notes = CustomTree.getInstance().getDatabase().getNotes();
    notes = notes.replace("\n", "<br>");
    root.put("Notes", notes);

    Template temp;
    try {
      temp = cfg.getTemplate("template_notes.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream
              (folder + "/Notes.html"));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum writeContacts(String folder) {
    Map root = new HashMap();
    String contacts = "";

    for (Person p : CustomTree.getInstance().getDatabase().getPersons()) {
      contacts = contacts + "Name: " + p.getName() + "<br>";
      contacts = contacts + "Job:  " + p.getJob() + "<br>";
      contacts = contacts + "Mail: " + p.getMail() + "<br>";
      contacts = contacts + "Tel:  " + p.getTel() + "<br>";
      contacts = contacts + "Fax:  " + p.getFax() + "<br><br>";
    }
    root.put("Contacts", contacts);

    Template temp;
    try {
      temp = cfg.getTemplate("template_contacts.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream
              (folder + "/Contacts.html"));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum writeImages(String folder) {
    Map root = new HashMap();
    notifyObservers(folder + "/db.png");

    Template temp;
    try {
      temp = cfg.getTemplate("template_relations.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream
              (folder + "/Relations.html"));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }

    try {
      temp = cfg.getTemplate("template_fds.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream
              (folder + "/FDs.html"));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum writeTextDescription(String folder) {
    Map root = new HashMap();

    String text = "";

    for (RelationSchema relation : database.getDatabase()) {
      text = text + relation.toString() + "<br>";
      for (FunctionalDependency fd : relation.getFunctionalDependencies()) {
        text = text + "(" + fd.toString() + ")" + "<br>";
      }
      text = text + "<br><br>";
    }

    root.put("Text", text);

    Template temp;
    try {
      temp = cfg.getTemplate("template_txtDescription.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream
              (folder + "/txtDescription.html"));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }
}
