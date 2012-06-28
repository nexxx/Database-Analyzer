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

package dba.utils;

/**
 * @author Andreas Freitag
 *         Class to remove File Extension
 */
public class RemoveExtension {
  /**
   * Remove given file extension
   *
   * @param s           Filename or Path
   * @param extToRemove Extension which will be removed,
   *                    inlcuding '.' -> e.g. ".html"
   * @return new filename or path without extension
   */
  public static String removeExtension(String s, String extToRemove) {
    if (!s.endsWith(extToRemove)) {
      return s;
    }

    int extensionIndex = s.lastIndexOf(".");
    if (extensionIndex == -1) {
      return s;
    }
    return s.substring(0, extensionIndex);
  }
}
