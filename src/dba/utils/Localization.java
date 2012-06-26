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
