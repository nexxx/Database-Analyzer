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

import javax.swing.*;

/**
 * Util Class to open a Url in Browser
 *
 * @author Andreas Freitag
 */
public class OpenUrl {
  public static void openURL(String url) {
    String osName = System.getProperty("os.name");
    try {
      if (osName.startsWith("Windows"))
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      else {
        String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "chrome", "chromium"};
        String browser = null;
        for (int count = 0; count < browsers.length && browser == null; count++)
          if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0)
            browser = browsers[count];
        Runtime.getRuntime().exec(new String[]{browser, url});
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, Localization.getInstance().getString("OU_Error") + ":\n" + e.getLocalizedMessage());
    }
  }
}
