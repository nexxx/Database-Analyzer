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

import dba.fileIO.ReadFromXML;
import dba.fileIO.SaveToXml;
import dba.gui.CustomTree;
import dba.gui.ImportDbFrame;
import dba.gui.auxClasses.events.GraphicalExportRequested;
import dba.gui.auxClasses.events.GraphicalExportRequestedListener;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.gui.auxClasses.navBarPanels.DatabaseTreePanel;
import dba.options.FeedbackEnum;
import dba.options.Options;
import dba.utils.Localization;
import dba.utils.RemoveExtension;
import dbaCore.data.*;
import dbaCore.data.dBTypes.TypeEnum;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class GuiLogic extends Observable {
  private static Database database;
  private DatabaseTreePanel dbTree;
  private Localization locale;
  private Options options;
  private static String lastFileName = null;
  private static String lastFileNameBackup = null;
  private CustomTree tree;
  private Configuration cfg;
  private ArrayList<GraphicalExportRequestedListener> listeners;


  public GuiLogic(DatabaseTreePanel dbTreePanel) {
    super();
    tree = CustomTree.getInstance();
    database = tree.getDatabase();
    dbTree = dbTreePanel;
    locale = Localization.getInstance();
    options = Options.getInstance();

    listeners = new ArrayList<>();

    //freemaker init
    cfg = new Configuration();
    try {
      cfg.setClassForTemplateLoading(this.getClass(), "/res/templates/" + Options.getInstance().getLanguage());

    } catch (Exception e) {
      //TODO FILL me (catch)!
    }
    cfg.setObjectWrapper(new DefaultObjectWrapper());
  }

  public String getLastFileName() {
    return lastFileName;
  }

  private void setLastFileName(String fileName) {
    lastFileName = fileName;
    if (lastFileName != null && !fileName.endsWith(".xml")) {
      lastFileName += ".xml";
    }
    super.setChanged();
    super.notifyObservers(lastFileName);
  }


  /**
   * Loads the database from the xml file
   */
  public FeedbackEnum open() {
    int result;
    if (TimeLine.getInstance().isDirty()) {
      Object[] options = {locale.getString("Yes"), locale.getString("No"), locale.getString("Cancel")};
      result = JOptionPane.showOptionDialog(null, locale.getString("TREE_NewMsg"), locale.getString("Confirm"),
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
      switch (result) {
        case JOptionPane.YES_OPTION:
          save();
          return openDB();
        case JOptionPane.NO_OPTION:
          return openDB();
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    } else {
      return openDB();
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
          TimeLine.getInstance().setDirty(false);
          lastFileName = path;
          lastFileNameBackup = path;
          TimeLine.getInstance().initialize(database);

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
          setLastFileName(path);
          return ret;
        } catch (IOException ex) {
          return FeedbackEnum.FAILED;
        }
      } else {
        setLastFileName(lastFileNameBackup);
        return FeedbackEnum.CANCEL;
      }
    } else {
      return saveWithoutFilePicker(lastFileName);
    }
  }

  public FeedbackEnum saveAs() {
    setLastFileName(null);
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
      TimeLine.getInstance().setDirty(false);
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
      Object[] options = {locale.getString("Yes"), locale.getString("No")};
      int result = JOptionPane.showOptionDialog(null, locale.getString("GUI_TheFile") + " " + outputFile.getName() +
        " " + locale.getString("GUI_AlreadyExisting"), locale.getString("Confirm"), JOptionPane.YES_NO_OPTION,
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
    TimeLine.getInstance().setDirty(false);
    return returnVal;
  }

  public FeedbackEnum newDatabase() {
    int result;
    if (TimeLine.getInstance().isDirty()) {
      Object[] options = {locale.getString("Yes"), locale.getString("No"), locale.getString("Cancel")};
      result = JOptionPane.showOptionDialog(null, locale.getString("TREE_NewMsg"), locale.getString("Confirm"),
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
      switch (result) {
        case JOptionPane.YES_OPTION:
          save();
          return createNewDb();
        case JOptionPane.NO_OPTION:
          return createNewDb();
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    } else {
      return createNewDb();
    }
    return FeedbackEnum.CANCEL;

  }

  private FeedbackEnum createNewDb() {
    Database dbBack = database;
    database = new Database();
    if (showDbQuestion() == FeedbackEnum.CANCEL) {
      database = dbBack;
      return FeedbackEnum.CANCEL;
    }
    dbTree.setDatabase(database);
    database.initPropertyChangeListeners();
    TimeLine.getInstance().setDirty(false);
    lastFileName = null;
    TimeLine.getInstance().initialize(database);
    tree.setSelectedItem(0);
    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum showDbQuestion() {
    ArrayList<String> types = new ArrayList<>();
    for (TypeEnum e : TypeEnum.values()) {
      types.add(e.getName());
    }
    Object[] possibilities = types.toArray();
    String s = (String) JOptionPane.showInputDialog(null, "Please Select the correct Database type", "Data Type",
      JOptionPane.PLAIN_MESSAGE, null, possibilities, TypeEnum.MYSQL.getName());

    if (s != null) {
      database.setType(TypeEnum.getEnumByValue(s));
      return FeedbackEnum.SUCCESSFUL;
    } else {
      return FeedbackEnum.CANCEL;
    }

  }

  public FeedbackEnum export() {
    String path;
    JFileChooser fc = new JFileChooser(options.getExportFolder());
    fc.setDialogTitle(locale.getString("GUI_Export"));
    FileFilter typeImage = new ExtensionFilter(".png", ".png");
    FileFilter typeText = new ExtensionFilter(".txt", ".txt");
    FileFilter typeHtml = new ExtensionFilter(".html", ".html");
    FileFilter typeSql = new ExtensionFilter(".sql", ".sql");
    fc.addChoosableFileFilter(typeImage);
    fc.addChoosableFileFilter(typeText);
    fc.addChoosableFileFilter(typeHtml);
    fc.addChoosableFileFilter(typeSql);
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
          fireGraphicalExportRequested(path);
        } else if (fc.getFileFilter() == typeText) {
          if (!path.endsWith(".txt")) {
            path = path + ".txt";
          }
          return exportText(path);
        } else if (fc.getFileFilter() == typeHtml) {

          return exportHtml(path);
        } else if (fc.getFileFilter() == typeSql) {
          if (!path.endsWith(".sql")) {
            path = path + ".sql";
          }
          return exportSqlDump(path);
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
      Object[] options = {locale.getString("Yes"), locale.getString("No")};
      int result = JOptionPane.showOptionDialog(null, locale.getString("GUI_TheFile") + " " + outputFile.getName() +
        " " + locale.getString("GUI_AlreadyExisting"), "Export", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

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
      Object[] options = {locale.getString("Yes"), locale.getString("No")};
      int result = JOptionPane.showOptionDialog(null, locale.getString("GUI_TheFile") + " " + outputFile.getName() +
        " " + locale.getString("GUI_AlreadyExisting"), "Export", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      switch (result) {
        case JOptionPane.YES_OPTION:
          return writeHtml(path);
        case JOptionPane.NO_OPTION:
          return export();
        default:
          return FeedbackEnum.CANCEL;
      }
    } else {
      return writeHtml(path);
    }
  }

  @SuppressWarnings("ConstantConditions")
  private FeedbackEnum writeText(String path) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(path));
      for (RelationSchema relation : database.getDatabase()) {
        out.write(relation.getName() + ":\n");
        out.write("|-> Attributes\n");
        for (Attribute attr : relation.getAttributes()) {
          if (attr.getConstraints().isEmpty()) {
            out.write("|  |-> " + attr.getName() + "\n");
          } else {
            out.write("|  |-> " + attr.getName() + " (" + attr.getConstraints() + ")\n");
          }
        }
        out.write("|-> Functional Dependencies\n");
        for (FunctionalDependency fd : relation.getFunctionalDependencies()) {
          out.write("|  |-> " + fd.toString() + "\n");
        }
        out.write("|-> Primary Key Attributes\n");
        for (Attribute attr : relation.getAttributes()) {
          if (attr.getIsPrimaryKey()) {
            out.write("|  |->" + attr.getName() + "\n");
          }
        }
        out.write("|-> Foreign Key Attributes\n");
        for (Attribute attr : relation.getAttributes()) {
          if (attr.getIsForeignKey()) {
            String target = "";
            for (ForeignKeyConstraint keyConstraint : database.getForeignKeys()) {
              if (keyConstraint.getSourceRelationName().equalsIgnoreCase(relation.getName()) && keyConstraint
                .getSourceAttributeName().equalsIgnoreCase(attr.getName())) {
                target = keyConstraint.getTargetRelationName() + "(" + keyConstraint.getTargetAttributeName() + ")";
              }
            }
            out.write("   |->" + attr.getName() + " - Target: " + target + "\n");
          }
        }
        out.write("\n");
      }
      out.close();
      return FeedbackEnum.SUCCESSFUL;
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
  }


  public void undo() {
    if (!TimeLine.getInstance().travelBackward()) {
      FeedbackbarPanel.getInstance().showFeedback(locale.getString("FB_UndoFailed"), FeedbackEnum.FAILED);
    }
    tree.setSelectedItem(0);
  }

  public void redo() {
    if (!TimeLine.getInstance().travelForward()) {
      FeedbackbarPanel.getInstance().showFeedback(locale.getString("FB_RedoFailed"), FeedbackEnum.FAILED);
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
    boolean writeNotes = !CustomTree.getInstance().getDatabase().getNotes().isEmpty();
    boolean writeContacts = !CustomTree.getInstance().getDatabase().getPersons().isEmpty();
    boolean writeRelations = !CustomTree.getInstance().getDatabase().getDatabase().isEmpty();

    path = RemoveExtension.removeExtension(path, ".html");

    folder = path;
    if (!path.endsWith(".html")) {
      path = path + ".html";
    }

    if (writeContacts || writeNotes || writeRelations) {
      if (makeDirs(folder) == FeedbackEnum.FAILED) {
        return FeedbackEnum.FAILED;
      }
    }

    if (writeIndex(folder, path) == FeedbackEnum.FAILED) {
      return FeedbackEnum.FAILED;
    }

    if (writeNotes) {
      if (writeNotes(folder) == FeedbackEnum.FAILED) {
        return FeedbackEnum.FAILED;
      }
    }

    if (writeContacts) {
      if (writeContacts(folder) == FeedbackEnum.FAILED) {
        return FeedbackEnum.FAILED;
      }
    }
    if (writeRelations) {
      if (writeImages(folder) == FeedbackEnum.FAILED) {
        return FeedbackEnum.FAILED;
      }

      if (writeTextDescription(folder) == FeedbackEnum.FAILED) {
        return FeedbackEnum.FAILED;
      }
    }

    return FeedbackEnum.SUCCESSFUL;
  }

  private FeedbackEnum makeDirs(String folder) {
    File dir = new File(folder);

    boolean success = true;
    if (dir.exists()) {
      success = deleteDir(dir);
    }

    success = success && (dir.mkdir());
    if (!success) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  private boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  }

  private FeedbackEnum writeIndex(String folder, String path) {
    File absFolder = new File(folder);

    Map root = new HashMap();

    String company = CustomTree.getInstance().getDatabase().getCustCompany();
    company = company.replace("\n", "<br>");
    String address = CustomTree.getInstance().getDatabase().getCustAdress();
    address = address.replace("\n", "<br>");

    if (!company.isEmpty()) {
      root.put("CustInfo", company);
    }
    if (!address.isEmpty()) {
      root.put("CustAddress", address);
    }
    if (!CustomTree.getInstance().getDatabase().getNotes().isEmpty()) {
      root.put("NotesUrl", absFolder.getName() + "/Notes.html");
    }
    if (!CustomTree.getInstance().getDatabase().getPersons().isEmpty()) {
      root.put("ContactsUrl", absFolder.getName() + "/Contacts.html");
    }
    if (!CustomTree.getInstance().getDatabase().getDatabase().isEmpty()) {
      root.put("RelViewUrl", absFolder.getName() + "/Relations.html");
      root.put("FdsViewUrl", absFolder.getName() + "/FDs.html");
      root.put("TextUrl", absFolder.getName() + "/txtDescription.html");
    }


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
      Writer out = new OutputStreamWriter(new FileOutputStream(folder + "/Notes.html"));
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
      Writer out = new OutputStreamWriter(new FileOutputStream(folder + "/Contacts.html"));
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
    fireGraphicalExportRequested(folder + "/db.png");

    Template temp;
    try {
      temp = cfg.getTemplate("template_relations.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(folder + "/Relations.html"));
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
      Writer out = new OutputStreamWriter(new FileOutputStream(folder + "/FDs.html"));
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
      text = text + relation.getName() + "<br>";
      text = text + "|-> Attributes<br>";
      for (Attribute attr : relation.getAttributes()) {
        if (attr.getConstraints().isEmpty()) {
          text = text + "|  |-> " + attr.getName() + "<br>";
        } else {
          text = text + "|  |-> " + attr.getName() + " (" + attr.getConstraints() + ")<br>";
        }
      }
      text = text + "|-> Functional Dependencies<br>";
      for (FunctionalDependency fd : relation.getFunctionalDependencies()) {
        text = text + "|  |-> " + fd.toString() + "<br>";
      }
      text = text + "|-> Primary Key Attributes<br>";
      for (Attribute attr : relation.getAttributes()) {
        if (attr.getIsPrimaryKey()) {
          text = text + "|  |->" + attr.getName() + "<br>";
        }
      }
      text = text + "|-> Foreign Key Attributes<br>";
      for (Attribute attr : relation.getAttributes()) {
        if (attr.getIsForeignKey()) {
          String target = "";
          for (ForeignKeyConstraint keyConstraint : database.getForeignKeys()) {
            if (keyConstraint.getSourceRelationName().equalsIgnoreCase(relation.getName()) && keyConstraint
              .getSourceAttributeName().equalsIgnoreCase(attr.getName())) {
              target = keyConstraint.getTargetRelationName() + "(" + keyConstraint.getTargetAttributeName() + ")";
            }
          }
          text = text + "   |->" + attr.getName() + " - Target: " + target + "<br>";
        }
      }
      text = text + "<br>";
    }

    root.put("Text", text);

    Template temp;
    try {
      temp = cfg.getTemplate("template_txtDescription.ftl");
    } catch (IOException e) {
      return FeedbackEnum.FAILED;
    }

    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(folder + "/txtDescription.html"));
      temp.process(root, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
    return FeedbackEnum.SUCCESSFUL;
  }

  /**
   * Set the Database
   *
   * @param db Database
   */
  public void setDatabase(Database db) {
    dbTree.setDatabase(db);
  }

  public FeedbackEnum importDb() {

    int result;
    ImportDbFrame frame = new ImportDbFrame(this);
    if (TimeLine.getInstance().isDirty()) {
      Object[] options = {locale.getString("Yes"), locale.getString("No"), locale.getString("Cancel")};
      result = JOptionPane.showOptionDialog(null, locale.getString("TREE_NewMsg"), locale.getString("Confirm"),
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
      switch (result) {
        case JOptionPane.YES_OPTION:
          save();
          frame.setVisible(true);
          return frame.getRetVal();
        case JOptionPane.NO_OPTION:
          frame.setVisible(true);
          return frame.getRetVal();
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    } else {
      frame.setVisible(true);
      return frame.getRetVal();
    }
    return FeedbackEnum.CANCEL;
  }

  // GraphicalExportRequested-Methods
  public void addGraphicalExportRequestedListener(GraphicalExportRequestedListener listener) {
    listeners.add(listener);
  }

  /**
   * Tells all listeners that a graphical export is requested
   */
  private void fireGraphicalExportRequested(String path) {
    GraphicalExportRequested request = new GraphicalExportRequested(this, path);

    for (GraphicalExportRequestedListener listener : listeners) {
      listener.GraphicalExportRequested(request);
    }
  }

  /**
   * Write SQL Dump to disk
   */
  public FeedbackEnum exportSqlDump(String path) {
    File outputFile = new File(path);

    if (!path.endsWith(".sql")) {
      outputFile = new File(path + ".sql");
    }

    if (outputFile.exists()) {
      Object[] options = {locale.getString("Yes"), locale.getString("No")};
      int result = JOptionPane.showOptionDialog(null, locale.getString("GUI_TheFile") + " " + outputFile.getName() +
        " " + locale.getString("GUI_AlreadyExisting"), "Export", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      switch (result) {
        case JOptionPane.YES_OPTION:
          return writeSqlDump(path);
        case JOptionPane.NO_OPTION:
          return export();
        default:
          return FeedbackEnum.CANCEL;
      }
    } else {
      return writeSqlDump(path);
    }
  }

  private FeedbackEnum writeSqlDump(String path) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(path));
      CreateSqlDump createSqlDump = new CreateSqlDump();
      out.write(createSqlDump.getDump());
      out.close();
      return FeedbackEnum.SUCCESSFUL;
    } catch (Exception e) {
      return FeedbackEnum.FAILED;
    }
  }

}
