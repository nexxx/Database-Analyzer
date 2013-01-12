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

package dbaCore.logging;

import dba.options.Options;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inizializer for Java Logging API
 *
 * @author Andreas Freitag
 */
public class MyLogger {
  //static private FileHandler fileTxt;
  //static private SimpleFormatter formatterTxt;

  static private FileHandler fileHTML;
  static private Formatter formatterHTML;

  static public void setup() throws IOException {
    // Create Logger
    Logger logger = Logger.getLogger("");
    logger.setLevel(Level.INFO);
    //fileTxt = new FileHandler("Logging.txt");
    fileHTML = new FileHandler(Options.getInstance().getLogFile().getCanonicalPath());

    // Create txt Formatter
    //formatterTxt = new SimpleFormatter();
    //fileTxt.setFormatter(formatterTxt);
    //logger.addHandler(fileTxt);

    // Create HTML Formatter
    formatterHTML = new MyHtmlFormatter();
    fileHTML.setFormatter(formatterHTML);
    logger.addHandler(fileHTML);
  }


}
