package dba.gui.metaInfoFrame;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Class provide a custom JTable
 * 
 * @author Andreas Freitag
 */
public class CustomTable extends JTable {

  /**
   * 
   */
  private static final long serialVersionUID = -6377053654548068024L;

  public CustomTable(PersonTableModel model) {
	super(model);
  }

  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	Component comp = super.prepareRenderer(renderer, row, col);
	JComponent jcomp = (JComponent) comp;
	if (comp == jcomp) {
	  jcomp.setToolTipText((String) getValueAt(row, col));
	}
	return comp;
  }

}
