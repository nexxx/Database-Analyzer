package dba.gui.newRelation.auxClasses;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;
import data.Database;
import data.RelationSchema;
import data.dBTypes.mySql;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.WizardEnum;
import dba.utils.constants;

/**
 * Class to provide a JPanel with all Functions needed for the Wizard
 * Card0
 * 
 * @author Andreas Freitag
 * 
 */
public class AddRelationNameAttributePanel extends JPanel implements constants {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JTextField txtRelName;
  private JTextField txtAttrName;
  private JTable table;
  private AttributeTableModel tableModel;
  private RelationSchema tmpRelation;
  private Database database;
  private JLabel lblAlreadyExisting;
  private JButton btnAdd;
  private JButton btnDelete;
  private Localization locale;
  private WizardEnum type;

  /**
   * Create the panel to set Relationname and adding Attributes
   * 
   * @param relation
   *          Relation to edit (empty relation for the 'New Relation
   *          Wizard')
   * @param db
   *          Database Object
   */
  public AddRelationNameAttributePanel(RelationSchema relation, Database db,
	  WizardEnum wizardType) {
	locale = Localization.getInstance();

	GetIcons getIcons = GetIcons.getInstance();

	ImageIcon iconAdd = getIcons.getButtonAdd();
	ImageIcon iconDelete = getIcons.getButtonDelete();

	type = wizardType;

	database = db;
	tmpRelation = relation;
	setLayout(new MigLayout("wrap 2, fillx", "[grow][grow, 160:160:160]"));
	lblAlreadyExisting = new JLabel(locale.getString("WIZ_AttrExisting"));
	btnAdd = new JButton(locale.getString("WIZ_Add"), iconAdd);
	btnAdd.setEnabled(false);
	btnAdd.setHorizontalAlignment(SwingConstants.LEFT);
	btnDelete = new JButton(locale.getString("WIZ_Delete"), iconDelete);
	btnDelete.setEnabled(false);
	btnDelete.setHorizontalAlignment(SwingConstants.LEFT);
	btnDelete.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int selectedRow = table.getSelectedRow();
		tableModel.removeRow(selectedRow);
		txtAttrName.selectAll();
		txtAttrName.requestFocus();
	  }
	});
	tableModel = new AttributeTableModel(tmpRelation.getAttributes(), database,
	    tmpRelation);
	table = new JTable(tableModel);

	JComboBox<String> comboBox = new JComboBox<String>(mySql.getInstance()
	    .getTypes());

	TableColumn col = table.getColumnModel().getColumn(1);
	col.setCellEditor(new DefaultCellEditor(comboBox));

	txtRelName = new JTextField();

	// Label Relation Name
	JLabel lblRelationName = new JLabel(locale.getString("WIZ_AttrRelName"));
	add(lblRelationName, "span");

	// Textfield Relation Name
	if (relation.getName() == null || relation.getName().isEmpty()) {
	  txtRelName.setText(locale.getString("WIZ_AttrNameQuestion"));
	} else {
	  txtRelName.setText(relation.getName());
	}
	txtRelName.requestFocus();
	txtRelName.selectAll();

	txtRelName.addFocusListener(new FocusAdapter() {
	  @Override
	  public void focusLost(FocusEvent e) {
		String oldName = null;
		if (type == WizardEnum.EDIT) {
		  oldName = tmpRelation.getName();
		}

		if (checkIfRelationExists(txtRelName.getText(), oldName)
		    || txtRelName.getText().isEmpty()
		    || txtRelName.getText().equals(
		        locale.getString("WIZ_AttrNameQuestion"))) {
		  lblAlreadyExisting.setVisible(true);
		  AddRelationNameAttributePanel.this.firePropertyChange("RelName",
			  null, false);
		} else {
		  database.updateFkRelationNames(tmpRelation.getName(),
			  txtRelName.getText());
		  tmpRelation.setName(txtRelName.getText());
		  lblAlreadyExisting.setVisible(false);
		  AddRelationNameAttributePanel.this.firePropertyChange("RelName",
			  null, true);
		}
	  }
	});
	add(txtRelName, "growx");

	// Label Relation already existing
	lblAlreadyExisting.setToolTipText(locale.getString("WIZ_AttrTooltip"));
	lblAlreadyExisting.setForeground(Color.RED);
	add(lblAlreadyExisting, "growx");
	lblAlreadyExisting.setVisible(false);

	// Label Attribute Name
	JLabel lblAttributeName = new JLabel(locale.getString("WIZ_AttrName"));
	add(lblAttributeName, "spanx, growx");

	// Textfield Attribute Name
	txtAttrName = new JTextField();
	txtAttrName.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		addAttribute();
	  }
	});
	txtAttrName.getDocument().addDocumentListener(
	    new CustomDocumentListener(btnAdd));
	add(txtAttrName, "growx");

	// Button Add
	btnAdd.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		addAttribute();
		txtAttrName.requestFocus();
	  }
	});
	add(btnAdd, "growx");

	// Table Attributes
	JScrollPane scrollpane = new JScrollPane(table);
	table.getTableHeader().setResizingAllowed(false);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.getTableHeader().setReorderingAllowed(false);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	table.getColumnModel().getColumn(1).setPreferredWidth(120);
	table.getColumnModel().getColumn(1).setMinWidth(120);
	table.getColumnModel().getColumn(1).setMaxWidth(120);
	table.getColumnModel().getColumn(2).setPreferredWidth(30);
	table.getColumnModel().getColumn(2).setMinWidth(30);
	table.getColumnModel().getColumn(2).setMaxWidth(30);
	table.getColumnModel().getColumn(3).setPreferredWidth(30);
	table.getColumnModel().getColumn(3).setMinWidth(30);
	table.getColumnModel().getColumn(3).setMaxWidth(30);
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	add(scrollpane, "spany, growx, growy");

	ListSelectionModel listSelectionModel = table.getSelectionModel();
	listSelectionModel.addListSelectionListener(new ListSelectionListener() {
	  @Override
	  public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		btnDelete.setEnabled(!lsm.isSelectionEmpty());
	  }
	});

	// Button Delete
	add(btnDelete, "aligny top, growx");
  }

  /**
   * Add Attribute (with name from Textfield) to Relation
   */
  private void addAttribute() {
	if (txtAttrName.getText().isEmpty()) {
	  return;
	}
	if (!tmpRelation.addAttribute(txtAttrName.getText())) {
	  JOptionPane.showMessageDialog(this,
		  locale.getString("WIZ_Attr") + " '" + txtAttrName.getText() + "' "
		      + locale.getString("WIZ_AttrDialogExisting"));
	  txtAttrName.selectAll();
	  return;
	}

	table.revalidate();
	txtAttrName.selectAll();
  }

  /**
   * @param relName
   *          Relation Name to check, if existing in database
   * @param oldName
   *          Current name of relation (only needed in EditMode.) In
   *          NewMode give null
   * @return true if name exists or false if name does not exist
   */
  private boolean checkIfRelationExists(String relName, String oldName) {

	if (oldName != null) {
	  for (RelationSchema relation : database.getDatabase()) {
		if (relation.getName().equalsIgnoreCase(relName)
		    && !relation.getName().equalsIgnoreCase(oldName)) {
		  return true;
		}
	  }
	  return false;
	} else {
	  for (RelationSchema relation : database.getDatabase()) {
		if (relation.getName().equalsIgnoreCase(relName)) {
		  return true;
		}
	  }
	  return false;
	}
  }
}
