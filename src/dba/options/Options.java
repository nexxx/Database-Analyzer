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
  private final String defLanguage = "en";
  ;
  private static Options instance = null;
  final private File optionsFolder;
  final private File optionsFile;
  final private File saveFolder;
  final private File exportFolder;
  private Boolean showTippsOnStartup;
  private final Boolean defShowTippsOnStartup = true;
  private HashMap<String, String> availableLocale;
  private String attributeColor;
  private final String defAttributeColor = "#00FF00";
  private String relationColor;
  private final String defRelationColor = "#00CD00";
  private String backgroundColor;
  private final String defBackgroundColor = "#A7E2FF";
  private String fontColor;
  private final String defFontColor = "#000000";
  private String arrowFKColor;
  private final String defArrowFKColor = "#0095C7";
  private String arrowFDColor;
  private final String defArrowFDColor = "#000000";
  private String lookAndFeel;
  private final String defLookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
  private String lastAdress;
  private final String defLastAdress = "localhost/dba";
  private String lastUser;
  private final String defLastUser = "username";
  private String lastType;
  private final String defLastType = "MYSQL";
  private HashMap<String, String> availLAF;
  private Boolean showTabOutline;
  private Boolean showTabInspect;
  private Boolean showTabToolbox;
  private Boolean showTabTheme;
  private Boolean showTabWiki;
  private final Boolean defShowTabOutline = true;
  private final Boolean defShowTabInspect = true;
  private final Boolean defShowTabToolbox = true;
  private final Boolean defShowTabTheme = true;
  private final Boolean defShowTabWiki = true;


  /**
   * Constructor for the options class
   */
  private Options() {
    super();
    availableLocale = new HashMap<>();
    availableLocale.put("de", "Deutsch");
    availableLocale.put("en", "English");

    availLAF = new HashMap<>();
    availLAF.put("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", "Nimbus LAF");
    availLAF.put("SYSTEMLAF", "System LAF");
    availLAF.put("javax.swing.plaf.metal.MetalLookAndFeel", "Metal LAF");

    prop = new Properties();

    // Set Locale
    language = defLanguage;

    // Show tipps
    showTippsOnStartup = defShowTippsOnStartup;

    // Files and Folders
    File homeFolder = new File(System.getProperty("user.home") + "/.dba");
    // Options Folder and Files
    optionsFolder = new File(homeFolder + "/Options");
    optionsFile = new File(optionsFolder + "/dba.properties");
    saveFolder = new File(homeFolder + "/Save");
    exportFolder = new File(homeFolder + "/Export");

    attributeColor = defAttributeColor;
    relationColor = defRelationColor;
    backgroundColor = defBackgroundColor;
    fontColor = defFontColor;
    arrowFDColor = defArrowFDColor;
    arrowFKColor = defArrowFKColor;

    lookAndFeel = defLookAndFeel;

    lastAdress = defLastAdress;
    lastUser = defLastUser;
    lastType = defLastType;

    showTabInspect = defShowTabInspect;
    showTabOutline = defShowTabOutline;
    showTabTheme = defShowTabTheme;
    showTabToolbox = defShowTabToolbox;
    showTabWiki = defShowTabWiki;
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
      prop.setProperty("arrowFKColor", arrowFKColor);
      prop.setProperty("arrowFDColor", arrowFDColor);
      prop.setProperty("lookAndFeed", lookAndFeel);
      prop.setProperty("lastAdress", lastAdress);
      prop.setProperty("lastUser", lastUser);
      prop.setProperty("lastType", lastType);
      prop.setProperty("showTabInspect", showTabInspect.toString());
      prop.setProperty("showTabOutline", showTabOutline.toString());
      prop.setProperty("showTabTheme", showTabTheme.toString());
      prop.setProperty("showTabToolbox", showTabToolbox.toString());
      prop.setProperty("showTabWiki", showTabWiki.toString());


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
      String tmpString;

      prop.load(new FileInputStream(optionsFile));

      language = prop.getProperty("language");
      if (language == null) {
        language = defLanguage;
      }

      tmpString = prop.getProperty("showTipOnStartup");
      if (tmpString == null) {
        showTippsOnStartup = defShowTippsOnStartup;
      } else {
        showTippsOnStartup = Boolean.valueOf(tmpString);
      }

      attributeColor = prop.getProperty("attributeColor");
      if (attributeColor == null) {
        attributeColor = defAttributeColor;
      }

      relationColor = prop.getProperty("relationColor");
      if (relationColor == null) {
        relationColor = defRelationColor;
      }

      backgroundColor = prop.getProperty("backgroundColor");
      if (backgroundColor == null) {
        backgroundColor = defBackgroundColor;
      }

      fontColor = prop.getProperty("fontColor");
      if (fontColor == null) {
        fontColor = defFontColor;
      }

      arrowFKColor = prop.getProperty("arrowFKColor");
      if (arrowFKColor == null) {
        arrowFKColor = defArrowFKColor;
      }

      arrowFDColor = prop.getProperty("arrowFDColor");
      if (arrowFDColor == null) {
        arrowFDColor = defArrowFDColor;
      }

      lookAndFeel = prop.getProperty("lookAndFeed");
      if (lookAndFeel == null) {
        lookAndFeel = defLookAndFeel;
      }

      lastAdress = prop.getProperty("lastAdress");
      if (lastAdress == null) {
        lastAdress = defLastAdress;
      }

      lastUser = prop.getProperty("lastUser");
      if (lastUser == null) {
        lastUser = defLastUser;
      }

      lastType = prop.getProperty("lastType");
      if (lastType == null) {
        lastType = defLastType;
      }

      tmpString = prop.getProperty("showTabTheme");
      if (tmpString == null) {
        showTabTheme = defShowTabTheme;
      } else {
        showTabTheme = Boolean.valueOf(tmpString);
      }

      tmpString = prop.getProperty("showTabToolbox");
      if (tmpString == null) {
        showTabToolbox = defShowTabToolbox;
      } else {
        showTabToolbox = Boolean.valueOf(tmpString);
      }

      tmpString = prop.getProperty("showTabWiki");
      if (tmpString == null) {
        showTabWiki = defShowTabWiki;
      } else {
        showTabWiki = Boolean.valueOf(tmpString);
      }

      tmpString = prop.getProperty("showTabOutline");
      if (tmpString == null) {
        showTabOutline = defShowTabOutline;
      } else {
        showTabOutline = Boolean.valueOf(tmpString);
      }

      tmpString = prop.getProperty("showTabInspect");
      if (tmpString == null) {
        showTabInspect = defShowTabInspect;
      } else {
        showTabInspect = Boolean.valueOf(tmpString);
      }
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
   * Get the key by value
   *
   * @param map   Hashmap
   * @param value Value to search for
   * @return Key or null if not found
   */
  public <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
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

  /**
   * @return Arrow Color FK
   */
  public String getArrowFKColor() {
    return arrowFKColor;
  }

  /**
   * @param arrowFKColor Arrowcolor FK(e.g. #000000)
   */
  public void setArrowFKColor(String arrowFKColor) {
    this.arrowFKColor = arrowFKColor;
  }

  /**
   * @return Arrow Color FD
   */
  public String getArrowFDColor() {
    return arrowFDColor;
  }

  /**
   * @param arrowFDColor Arrowcolor FD(e.g. #000000)
   */
  public void setArrowFDColor(String arrowFDColor) {
    this.arrowFDColor = arrowFDColor;
  }

  /**
   * @return the lookAndFeed
   */
  public String getLookAndFeel() {
    return lookAndFeel;
  }

  /**
   * @param lookAndFeel LAF String
   */
  public void setLookAndFeel(String lookAndFeel) {
    this.lookAndFeel = lookAndFeel;
  }

  /**
   * @return Available LAFs
   */
  public HashMap<String, String> getAvailLAF() {
    return availLAF;
  }

  /**
   * @return Last used Adress for DB Import
   */
  public String getLastAdress() {
    return lastAdress;
  }

  /**
   * @param lastAdress Last used Adress for DB Import
   */
  public void setLastAdress(String lastAdress) {
    this.lastAdress = lastAdress;
  }

  /**
   * @return Last used Username for DB Import
   */
  public String getLastUser() {
    return lastUser;
  }

  /**
   * @param lastUser Last used Username for DB Import
   */
  public void setLastUser(String lastUser) {
    this.lastUser = lastUser;
  }

  /**
   * @return Last used DB Type for DB Import
   */
  public String getLastType() {
    return lastType;
  }

  /**
   * @param lastType Last used DB Type for DB Import
   */
  public void setLastType(String lastType) {
    this.lastType = lastType;
  }

  /**
   * @return Show outline tab
   */
  public Boolean getShowTabOutline() {
    return showTabOutline;
  }

  /**
   * @param showTabOutline Show outline tab
   */
  public void setShowTabOutline(Boolean showTabOutline) {
    this.showTabOutline = showTabOutline;
  }

  /**
   * @return Show Inspect tab
   */
  public Boolean getShowTabInspect() {
    return showTabInspect;
  }

  /**
   * @param showTabInspect Show Inspect tab
   */
  public void setShowTabInspect(Boolean showTabInspect) {
    this.showTabInspect = showTabInspect;
  }

  /**
   * @return Show Toolbox tab
   */
  public Boolean getShowTabToolbox() {
    return showTabToolbox;
  }

  /**
   * @param showTabToolbox Show Toolbox tab
   */
  public void setShowTabToolbox(Boolean showTabToolbox) {
    this.showTabToolbox = showTabToolbox;
  }

  /**
   * @return Show Theming tab
   */
  public Boolean getShowTabTheme() {
    return showTabTheme;
  }

  /**
   * @param showTabTheme Show Theming tab
   */
  public void setShowTabTheme(Boolean showTabTheme) {
    this.showTabTheme = showTabTheme;
  }

  /**
   * @return Show Wiki tab
   */
  public Boolean getShowTabWiki() {
    return showTabWiki;
  }

  /**
   * @param showTabWiki Show wiki tab
   */
  public void setShowTabWiki(Boolean showTabWiki) {
    this.showTabWiki = showTabWiki;
  }
}
