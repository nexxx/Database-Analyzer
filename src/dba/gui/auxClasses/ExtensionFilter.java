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

package dba.gui.auxClasses;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Custom Extension Filter for FileChooser
 *
 * @author Andreas Freitag
 */
public class ExtensionFilter extends FileFilter {
  private String extensions[];

  private String description;

  public ExtensionFilter(String description, String extension) {
    this(description, new String[]{extension});
  }

  public ExtensionFilter(String description, String extensions[]) {
    super();
    this.description = description;
    this.extensions = extensions.clone();
  }

  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    String path = file.getAbsolutePath();
    for (String ext : extensions) {
      if (path.endsWith(ext) && path.charAt(path.length() - ext.length()) == '.') {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getDescription() {
    return description == null ? extensions[0] : description;
  }
}
