package dba.gui.newRelation.auxClasses;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Custom Document listener for monitoring changes in Textfield with
 * Relationname
 * 
 * @author Andreas Freitag
 * 
 */
public class CustomDocumentListener implements DocumentListener {

  private JButton button;

  /**
   * Constructor
   * 
   * @param b
   *          Button which will be disabled
   */
  public CustomDocumentListener(JButton b) {
	button = b;
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
	disableIfEmpty(e);
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
	disableIfEmpty(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
	disableIfEmpty(e);
  }

  /**
   * Disables button if textfield is empty
   * 
   * @param e
   *          Event
   */
  public void disableIfEmpty(DocumentEvent e) {
	button.setEnabled(e.getDocument().getLength() > 0);
  }

}
