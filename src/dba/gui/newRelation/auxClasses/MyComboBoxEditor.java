package dba.gui.newRelation.auxClasses;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * Custom Combobox editor for Comboboxes
 */
public class MyComboBoxEditor extends DefaultCellEditor {
  /**
   * 
   */
  private static final long serialVersionUID = 3963606942715306972L;

  public MyComboBoxEditor(String[] items) {
	super(new JComboBox<String>(items));
  }
}