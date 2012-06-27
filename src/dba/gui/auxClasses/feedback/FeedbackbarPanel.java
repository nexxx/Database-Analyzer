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

package dba.gui.auxClasses.feedback;

import dba.options.FeedbackEnum;

import javax.swing.*;
import java.awt.*;


/**
 * Class to print a Feedbackmessage for 2sec on a given panel
 * (Borderlayout south)
 *
 * @author Andreas Freitag
 */
public class FeedbackbarPanel extends JPanel {
  private static FeedbackbarPanel instance = null;

  /**
   *
   */
  private static final long serialVersionUID = 8602501853286337338L;

  private FeedbackbarPanel() {
    super();
    this.setLayout(new BorderLayout());
  }

  public void showFeedback(String text, FeedbackEnum type) {
    FeedbackbarThread feedbackbarThread = new FeedbackbarThread(this);
    feedbackbarThread.printText(text, type);
  }

  /**
   * Getter for the singelton FeedbackbarPanel (thread-save)
   */
  public synchronized static FeedbackbarPanel getInstance() {
    if (instance == null) {
      synchronized (FeedbackbarPanel.class) {
        instance = new FeedbackbarPanel();
      }
    }
    return instance;
  }
}
