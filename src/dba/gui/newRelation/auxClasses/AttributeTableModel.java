package dba.gui.newRelation.auxClasses;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import data.Attribute;
import data.Database;
import data.RelationSchema;
import dba.gui.FkWizard.FkWizard;
import dba.utils.Localization;

/**
 * Custom Tablemodel which can handle our ArrayList with Attributes
 * and contains all Actions to handle it
 * 
 * @author Andreas Freitag
 * 
 */
public class AttributeTableModel extends AbstractTableModel {
  /**
   * 
   */
  private static final long serialVersionUID = 7202056709045229442L;
  private String[] columnNames;
  private ArrayList<Attribute> data;
  private Localization locale;
  private Database database;
  private RelationSchema relation;

  private AttributeTableModel() {
	super();
	locale = Localization.getInstance();
	columnNames = new String[] { locale.getString("WIZ_ColAttrName"),
	    locale.getString("WIZ_ColType"), "PK", "FK" };
	data = new ArrayList<Attribute>();
  }

  /**
   * Constructor
   * 
   * @param data
   *          ArrayList with Attributes
   */
  public AttributeTableModel(ArrayList<Attribute> data) {
	this();
	this.data = data;
  }

  public AttributeTableModel(ArrayList<Attribute> data, Database db,
	  RelationSchema rel) {
	this(data);
	relation = rel;
	database = db;
  }

  @Override
  public int getColumnCount() {
	return columnNames.length;
  }

  @Override
  public int getRowCount() {
	return data.size();
  }

  @Override
  public String getColumnName(int col) {
	return columnNames[col];
  }

  @Override
  public Object getValueAt(int row, int col) {
	if (row < 0 || col < 0) {
	  return null;
	}
	return getAttributeValueAt(data.get(row), col);
  }

  private Object getAttributeValueAt(Attribute attr, int column) {
	switch (column) {
	case 0:
	  return attr.getName();
	case 1:
	  return attr.getType().toUpperCase();
	case 2:
	  return attr.getIsPrimaryKey();
	case 3:
	  return attr.getIsForeignKey();
	default:
	  throw new IllegalArgumentException("ColumnIndex out of bounds");
	}
  }

  @Override
  public Class<? extends Object> getColumnClass(int c) {
	return getValueAt(0, c).getClass();
  }

  @Override
  public boolean isCellEditable(int row, int col) {
	return true;

  }

  @Override
  public void setValueAt(Object value, int row, int col) {
	setAttributeValueAt(data.get(row), col, value);
	fireTableCellUpdated(row, col);
  }

  private void setAttributeValueAt(Attribute attr, int column, Object value) {
	switch (column) {
	case 0:
	  if (value instanceof String) {
		database.updateFkAttributeNames(relation.getName(), attr.getName(),
		    (String) value);
		attr.setName((String) value);
	  }
	  return;
	case 1:
	  if (value instanceof String) {
		attr.setType((String) value);
	  }
	  return;
	case 2:
	  if (value instanceof Boolean) {
		attr.setIsPrimaryKey((Boolean) value);
	  }
	  return;
	case 3:
	  if (value instanceof Boolean) {
		if ((Boolean) value) {
		  FkWizard wizard = new FkWizard(database, relation, attr);
		  wizard.setVisible(true);

		  if (wizard.isDataBaseChanged()) {
			attr.setIsForeignKey((Boolean) value);
		  }
		} else {
		  database.removeForeignKey(relation.getName(), attr.getName());
		  attr.setIsForeignKey((Boolean) value);
		}

		return;
	  }
	default:
	  throw new IllegalArgumentException();
	}
  }

  /**
   * Add new row to our Table
   * 
   * @param attr
   *          Attribute which will be added
   */
  public void addRow(Attribute attr) {
	data.add(attr);
	fireTableRowsInserted(0, getRowCount());
  }

  /**
   * Delete row from our Table
   * 
   * @param row
   *          Row to delete ( 0 until n )
   */
  public void removeRow(int row) {
	data.remove(row); // delete record from file
	fireTableRowsDeleted(row, row);
  }

}