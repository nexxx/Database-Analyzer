/**
 * 
 */
package dba.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import dba.options.Options;


/**
 * @author andreas
 * 
 */
public class Localization {
  private static Localization instance = null;
  private Options options;
  private ResourceBundle captions;

  private Localization() {
	options = Options.getInstance();
	Locale locale = new Locale(options.getLanguage());
	Locale.setDefault(new Locale(options.getLanguage()));
	captions = ResourceBundle.getBundle(
	    "res.localization.localizationMessages", locale);
  }

  /**
   * Getter for the singelton localization (thread-safe)
   * */
  public synchronized static Localization getInstance() {
	if (instance == null) {
	  synchronized (Localization.class) {
		instance = new Localization();
	  }
	}
	return instance;
  }

  /**
   * A little convenience for getting the Localized strings
   * 
   * @param name
   *          of the element to search for
   * @return the localized String
   */
  public String getString(String name) {
	return captions.getString(name);
  }

}
