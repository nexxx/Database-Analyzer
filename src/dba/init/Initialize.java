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

import javax.swing.UIManager;

import dba.options.Options;


/**
 * Class to initialize the program - load options, create folders, etc
 * 
 * @author Andreas Freitag
 * 
 */
public class Initialize {

  Options options;

  public Initialize() {
	options = Options.getInstance();
  }

  /**
   * Run all needed initial methods for program
   */
  public void init() {
	this.checkOptionsFolderFiles();
	options.readOptions();
	this.checkSaveFolder();
	this.checkExportFolder();
	this.setLookAndFeel();
  }

  private void checkOptionsFolderFiles() {
	if (options.getOptionsFolder().exists() == false) {
	  options.getOptionsFolder().mkdirs();
	}

	if (options.getOptionsFile().exists() == false) {
	  // Write default options
	  options.writeOptions();
	}
  }

  private void checkSaveFolder() {
	if (options.getSaveFolder().exists() == false) {
	  options.getSaveFolder().mkdirs();
	}

  }

  private void checkExportFolder() {
	if (options.getExportFolder().exists() == false) {
	  options.getExportFolder().mkdirs();
	}

  }

  private void setLookAndFeel() {
	// Set LookAndFeel
	try {
	  // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	  UIManager
		  .setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	} catch (Exception e) {
	  System.out
		  .println("Nimbus LookAndFeel not found. Using standard LookAndFeel instead");
	  e.printStackTrace();
	}
  }

}
