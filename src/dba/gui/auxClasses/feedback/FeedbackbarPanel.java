package dba.gui.auxClasses.feedback;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import dba.options.FeedbackEnum;


/**
 * Class to print a Feedbackmessage for 2sec on a given panel
 * (Borderlayout south)
 * 
 * @author Andreas Freitag
 * 
 */
public class FeedbackbarPanel extends JPanel {
  private static FeedbackbarPanel instance = null;

  /**
   * 
   */
  private static final long serialVersionUID = 8602501853286337338L;

  private FeedbackbarPanel() {
	this.setLayout(new BorderLayout());
  }

  public void showFeedback(String text, FeedbackEnum type) {
	FeedbackbarThread feedbackbarThread = new FeedbackbarThread(this);
	feedbackbarThread.printText(text, type);
  }

  /**
   * Getter for the singelton FeedbackbarPanel (thread-save)
   * */
  public synchronized static FeedbackbarPanel getInstance() {
	if (instance == null) {
	  synchronized (FeedbackbarPanel.class) {
		instance = new FeedbackbarPanel();
	  }
	}
	return instance;
  }
}
