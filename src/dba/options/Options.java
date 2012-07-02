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

/**
 *
 */
package dba.options;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Properties;

/**
 * Options Class (singelton pattern) which stores all informations.
 * This class also includes the save and read options methods
 *
 * @author Andreas Freitag
 */
public class Options extends Observable {
  private Properties prop;
  private String language;
  private static Options instance = null;
  final private File optionsFolder;
  final private File optionsFile;
  final private File saveFolder;
  final private File exportFolder;
  private Boolean showTippsOnStartup;
  private HashMap<String, String> availableLocale;
  private String attributeColor;
  private String relationColor;
  private String backgroundColor;
  private String fontColor;

  /**
   * Constructor for the options class
   */
  private Options() {
    super();
    availableLocale = new HashMap<>();
    availableLocale.put("de", "Deutsch");
    availableLocale.put("en", "English");

    prop = new Properties();

    // Set Locale
    language = "en";

    // Show tipps
    showTippsOnStartup = Boolean.TRUE;

    // Files and Folders
    File homeFolder = new File(System.getProperty("user.home") + "/.dba");
    // Options Folder and Files
    optionsFolder = new File(homeFolder + "/Options");
    optionsFile = new File(optionsFolder + "/dba.properties");
    saveFolder = new File(homeFolder + "/Save");
    exportFolder = new File(homeFolder + "/Export");

    attributeColor = "#00FF00";
    relationColor = "#00CD00";
    backgroundColor = "#A7E2FF";
    fontColor = "#000000";
  }

  /**
   * Getter for save folder
   *
   * @return save folder file
   */
  public File getSaveFolder() {
    return saveFolder;
  }

  /**
   * Getter for the singelton options (thread-save)
   */
  public synchronized static Options getInstance() {
    if (instance == null) {
      synchronized (Options.class) {
        instance = new Options();
      }
    }
    return instance;
  }

  /**
   * Returns the Location of the Folder containing the Option-Files
   *
   * @return OptionFile-FolderLocation
   */
  public File getOptionsFolder() {
    return optionsFolder;
  }

  /**
   * Returns the location of the OptionsFile
   *
   * @return OptionFile-Location
   */
  public File getOptionsFile() {
    return optionsFile;
  }

  /**
   * Get the current language (en/de/...)
   *
   * @return language string
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Saves all Options to a File
   */
  public void writeOptions() {

    try {
      // Set the properties value
      prop.setProperty("language", language);

      prop.setProperty("showTipOnStartup", showTippsOnStartup.toString());

      prop.setProperty("attributeColor", attributeColor);
      prop.setProperty("relationColor", relationColor);
      prop.setProperty("backgroundColor", backgroundColor);
      prop.setProperty("fontColor", fontColor);

      // Save properties to DBN options folder
      prop.store(new FileOutputStream(optionsFile), "DBA options file");

    } catch (IOException ex) {
      super.notifyObservers(new Feedback("Unable to save options file", FeedbackEnum.FAILED));
    }

  }

  /**
   * Reads all Options from the File
   */
  public void readOptions() {
    try {

      prop.load(new FileInputStream(optionsFile));

      language = prop.getProperty("language");

      showTippsOnStartup = Boolean.valueOf(prop.getProperty("showTipOnStartup"));

      attributeColor = prop.getProperty("attributeColor");
      relationColor = prop.getProperty("relationColor");
      backgroundColor = prop.getProperty("backgroundColor");
      fontColor = prop.getProperty("fontColor");


    } catch (Exception ex) {
      super.notifyObservers(new Feedback("Unable to load options file", FeedbackEnum.FAILED));
    }
  }

  /**
   * Getter for the export folder
   *
   * @return Exportfolder file
   */
  public File getExportFolder() {
    return exportFolder;
  }

  /**
   * Getter for the boolean to show tip of the day
   *
   * @return true/false
   */
  public Boolean getShowTippsOnStartup() {
    return showTippsOnStartup;
  }

  /**
   * Setter for the boolean to show tip of the day
   *
   * @param showTippsOnStartup true or false
   */
  public void setShowTippsOnStartup(Boolean showTippsOnStartup) {
    this.showTippsOnStartup = showTippsOnStartup;
  }

  /**
   * @param language the language to set
   */
  public void setLanguage(String language) {
    this.language = language;
  }

  /**
   * @return the availableLocale
   */
  public HashMap<String, String> getAvailableLocale() {
    return availableLocale;
  }

  /**
   * Get the locale String (de/en/..) by full Locale
   * (Deutsch/English/...)
   *
   * @param map   Hashmap
   * @param value String with Locale (Deutsch/English/...)
   * @return short Locale string (de/en/...)
   */
  public <T, E> T getKeyByLanguage(HashMap<T, E> map, E value) {
    for (Entry<T, E> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
   * @return Attribute Color
   */
  public String getAttributeColor() {
    return attributeColor;
  }

  /**
   * @param attributeColor Attributecolor (e.g. #00FF00)
   */
  public void setAttributeColor(String attributeColor) {
    this.attributeColor = attributeColor;
  }

  /**
   * @return Relation Color
   */
  public String getRelationColor() {
    return relationColor;
  }

  /**
   * @param relationColor Relationcolor (e.g. #00FF00)
   */
  public void setRelationColor(String relationColor) {
    this.relationColor = relationColor;
  }

  /**
   * @return Background Color
   */
  public String getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * @param backgroundColor Backgroundcolor (e.g. #00FF00)
   */
  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  /**
   * @return Font Color
   */
  public String getFontColor() {
    return fontColor;
  }

  /**
   * @param fontColor Fontcolor (e.g. #00FF00)
   */
  public void setFontColor(String fontColor) {
    this.fontColor = fontColor;
  }
}
