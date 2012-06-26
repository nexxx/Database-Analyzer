package dba.gui.auxClasses;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;

/**
 * Helper class to the the current selected radiobutton from
 * buttongroup
 */
public class GetSelectedRadioButton {
  /**
   * Get the selected button in group
   * 
   * @param group
   *          Buttongroup
   * @return selected button in group
   */
  public static JRadioButtonMenuItem getSelection(ButtonGroup group) {
	for (Enumeration<AbstractButton> e = group.getElements(); e
	    .hasMoreElements();) {
	  JRadioButtonMenuItem b = (JRadioButtonMenuItem) e.nextElement();
	  if (b.getModel() == group.getSelection()) {
		return b;
	  }
	}
	return null;
  }

}
