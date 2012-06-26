package dba.gui.newRelation.auxClasses;

import javax.swing.DefaultListSelectionModel;

/**
 * Class to provide our needed List Selection Model - Multiselection
 * without CTRL
 * 
 * @author Andreas Freitag
 * 
 */
public class MultiListSelectionModel extends DefaultListSelectionModel {
  /**
   * 
   */
  private static final long serialVersionUID = 3579713089677925078L;

  @Override
  public void setSelectionInterval(int index0, int index1) {
	if (super.isSelectedIndex(index0)) {
	  super.removeSelectionInterval(index0, index1);
	} else {
	  super.addSelectionInterval(index0, index1);
	}
  }

}
