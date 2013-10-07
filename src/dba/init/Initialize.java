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

package dba.init;

import dba.options.Options;
import dbaCore.logging.MyLogger;

import javax.swing.*;
import java.io.*;


/**
 * Class to initialize the program - load options, create folders, etc
 *
 * @author Andreas Freitag
 */
public class Initialize {

  private Options options;
  private static Initialize instance = null;

  private Initialize() {
    super();
    try {
      MyLogger.setup();
    } catch (IOException e) {
      e.printStackTrace();
    }
    options = Options.getInstance();
  }

  /**
   * Getter for the singelton inizialize (thread-save)
   */
  public synchronized static Initialize getInstance() {
    if (instance == null) {
      synchronized (Initialize.class) {
        instance = new Initialize();
      }
    }
    return instance;
  }

  /**
   * Run all needed initial methods for program
   */
  public void init() {
    this.checkOptionsFolderFiles();
    options.readOptions();
    this.checkSaveFolder();
    this.checkExportFolder();
    this.checkLogFolder();
    this.checkSchemeFolder();
    this.setLookAndFeel();
  }

  private void checkOptionsFolderFiles() {
    if (!options.getOptionsFolder().exists()) {
      options.getOptionsFolder().mkdirs();
    }

    if (!options.getOptionsFile().exists()) {
      // Write default options
      options.writeOptions();
    }
  }

  private void checkSaveFolder() {
    if (!options.getSaveFolder().exists()) {
      options.getSaveFolder().mkdirs();
    }

  }

  private void checkExportFolder() {
    if (!options.getExportFolder().exists()) {
      options.getExportFolder().mkdirs();
    }

  }

  private void checkLogFolder(){
    if (!options.getLogFolder().exists()) {
      options.getLogFolder().mkdirs();
    }
  }

  private void checkSchemeFolder(){
    if (!options.getSchemeFolder().exists()) {
      options.getSchemeFolder().mkdirs();
    }
    writeSchemeFile("res/colorScheme/Default.dtc");
    writeSchemeFile("res/colorScheme/Noir.dtc");
    writeSchemeFile("res/colorScheme/BlackAndWhite.dtc");
  }

  private void writeSchemeFile(String path){
    try {
      InputStream ddlStream = this.getClass().getClassLoader().getResourceAsStream(path);
      String file = new File(path).getName();
      FileOutputStream fos = new FileOutputStream(options.getSchemeFolder()+"/"+file);
      byte[] buf = new byte[4096];
      int r = ddlStream.read(buf);
      while(r != -1) {
        fos.write(buf, 0, r);
        r = ddlStream.read(buf);
      }
      fos.close();
      ddlStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void setLookAndFeel() {

    // Set LookAndFeel
    try {
      if (options.getLookAndFeel().equalsIgnoreCase("SYSTEMLAF")) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } else {
        UIManager.setLookAndFeel(options.getLookAndFeel());
      }
    } catch (Exception e) {
      System.out.println("LookAndFeel not found. Using standard LookAndFeel instead");
      //e.printStackTrace();
    }
  }

}
