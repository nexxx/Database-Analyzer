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

package dba.fileIO;

import dbaCore.data.Database;

import javax.xml.bind.JAXB;
import java.io.File;

/**
 * Class to read the db from a xml file
 *
 * @author Andreas Freitag
 */
public class ReadFromXML {

  public ReadFromXML() {
    super();
  }

  /**
   * Unmarshall a xml file and return the Database
   *
   * @param file which will be opened
   * @return Database read from xml
   */
  public Database ReadDbNow(File file) {
    return JAXB.unmarshal(file, Database.class);

  }
}
