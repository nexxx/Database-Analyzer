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
