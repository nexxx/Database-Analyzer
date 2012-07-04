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

package dbaCore.utils;

import java.util.ArrayList;

/**
 * Provides common functions for all Classes
 *
 * @author Sebastian Theuermann
 */
public class Utilities {

  private static Utilities instance = new Utilities();

  // Avoid initialization with "new Utils();"
  private Utilities() {
    super();
  }

  // Threadsafety first!
  public static synchronized Utilities getInstance() {
    return instance;
  }

  /**
   * Tries to parse a String to an Integer
   *
   * @param str the String to be parsed to an Integer
   * @return a Integer when successful, null when not (e.g. no number)
   */
  public static Integer tryParseInt(String str) {
    Integer n = null;

    try {
      return new Integer(str);

    } catch (NumberFormatException nfe) {
      return n;
    }
  }

  /**
   * Returns a String containing all elements of the list, separated
   * by ","
   *
   * @param arr the ArrayList to work with
   * @return a String with the names of all elements
   */
  public static String getStringFromArrayList(ArrayList<?> arr) {
    String result = "";
    for (int i = 0; i < arr.size(); i++) {
      if (i == 0) {
        result = result + arr.get(i).toString();
      } else {
        result = result + "," + arr.get(i).toString();
      }
    }

    return result;
  }
}
