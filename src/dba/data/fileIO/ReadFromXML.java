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

package dba.data.fileIO;

import java.io.File;

import javax.xml.bind.JAXB;

import data.Database;
import dba.options.Options;

/**
 * Class to read the db from a xml file
 * 
 * @author Andreas Freitag
 * 
 */
public class ReadFromXML {
  Options options;

  public ReadFromXML() {
	options = Options.getInstance();
  }

  /**
   * Unmarshall a xml file and return the Database
   * 
   * @param name
   *          of the file which will be opened
   * @return Database read from xml
   * @throws Exception
   *           JAXB & File Exceptions
   */
  public Database ReadDbNow(String name) throws Exception {
	return ReadDbNow(new File(options.getSaveFolder() + "/" + name));

  }

  /**
   * Unmarshall a xml file and return the Database
   * 
   * @param file
   *          which will be opened
   * @return Database read from xml
   * @throws Exception
   *           JAXB & File Exceptions
   */
  public Database ReadDbNow(File file) throws Exception {
	return JAXB.unmarshal(file, Database.class);

  }
}
