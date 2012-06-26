package dba.gui.newRelation.auxClasses;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Custom Cell Renderer to provide a Combobox support for Attribute
 * type changes
 */
public class CustomTableComboboxRenderer extends JComboBox<String> implements
    TableCellRenderer {

  /**
   * 
   */
  private static final long serialVersionUID = -8508891862656940969L;

  public CustomTableComboboxRenderer(String[] items) {
	super(items);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
	  boolean isSelected, boolean hasFocus, int row, int column) {
	if (isSelected) {
	  setForeground(table.getSelectionForeground());
	  super.setBackground(table.getSelectionBackground());
	} else {
	  setForeground(table.getForeground());
	  setBackground(table.getBackground());
	}

	// Select the current value
	setSelectedItem(value);
	return this;
  }
}
