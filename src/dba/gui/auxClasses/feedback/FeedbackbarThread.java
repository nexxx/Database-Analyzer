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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dba.options.FeedbackEnum;


/**
 * Thread which prints the message for 5sec
 * 
 * @author Andreas Freitag
 */
public class FeedbackbarThread implements Runnable {

  private Thread runner;
  private String text;
  private JPanel panel;
  private FeedbackEnum type;

  public FeedbackbarThread(JPanel feedbackPanel) {
	panel = feedbackPanel;
	runner = new Thread(this, "Feedbackbar");
  }

  /**
   * Print the text as message or warning (red)
   * 
   * @param textToDisplay
   *          Text which will be printed
   * @param t
   *          Success/Failed
   */
  public void printText(String textToDisplay, FeedbackEnum t) {
	text = textToDisplay;
	type = t;
	runner.start();
  }

  @Override
  public void run() {
	JLabel lblText = new JLabel(text);
	if (type == FeedbackEnum.FAILED) {
	  lblText.setForeground(Color.RED);
	}
	panel.add(lblText, BorderLayout.CENTER);
	panel.revalidate();
	try {
	  Thread.sleep(5000); // in ms
	} catch (InterruptedException e) {
	  System.out.println("Exception in Thread");
	}
	panel.removeAll();
	panel.revalidate();
  }
}
