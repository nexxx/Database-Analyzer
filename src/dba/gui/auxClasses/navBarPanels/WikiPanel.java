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

package dba.gui.auxClasses.navBarPanels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel which contains the wiki page.
 * For use in Navigation Tabbedpane
 *
 * @author Andreas Freitag
 */
public class WikiPanel extends JPanel {
  private JScrollPane scrollpane;


  public WikiPanel() {
    super();
    this.setLayout(new BorderLayout());


    scrollpane = new JScrollPane();
    this.add(scrollpane);
  }
}
