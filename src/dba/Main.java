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

package dba;

import dba.gui.MainWindow;
import dba.gui.tippOfTheDayFrame;
import dba.init.Initialize;


public class Main {

  /**
   * Main Class
   *
   * @param args Arguments ( -cmd to start as commandline application)
   */
  public static void main(String[] args) {

    // Initialize - Load options ETC
    Initialize initialize = new Initialize();
    initialize.init();

    MainWindow mainWindow = new MainWindow();
    mainWindow.getFrame().setVisible(true);
    tippOfTheDayFrame tippFrame = new tippOfTheDayFrame();
    tippFrame.showTOD();
  }

}
