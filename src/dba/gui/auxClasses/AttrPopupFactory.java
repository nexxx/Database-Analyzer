package dba.gui.auxClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import data.Attribute;
import data.dBTypes.mySql;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.TreeEnum;
import dba.utils.constants;

/**
 * Class which provides the PopupContextmenu for the JTree, inlcuding
 * all Listeners. This class is a Observable and notifies all
 * Observers when something has changed!
 * 
 * @author Andreas Freitag
 * 
 */
public class AttrPopupFactory implements constants {
  private Localization locale;
  private CustomTree tree;
  private JCheckBoxMenuItem primaryKeyAttributeMenuItem;
  private JCheckBoxMenuItem foreignKeyAttributeMenuItem;
  private ImageIcon iconDelete;
  private ImageIcon iconRename;
  private ImageIcon iconPk;
  private ImageIcon iconFk;
  private AttrLogic attrLogic;
  private ButtonGroup grp;

  /**
   * Defaultconstructor
   * 
   * @param jtree
   *          JTree with the DB
   */
  public AttrPopupFactory(CustomTree jtree) {
	locale = Localization.getInstance();
	tree = jtree;
	attrLogic = new AttrLogic();

	GetIcons getIcons = GetIcons.getInstance();
	iconDelete = getIcons.getPopupDelete();
	iconRename = getIcons.getPopupRename();
	iconPk = getIcons.getPopupPK();
	iconFk = getIcons.getPopupFK();

	primaryKeyAttributeMenuItem = new JCheckBoxMenuItem(
	    locale.getString("TREE_AttrChkBoxPK"), iconPk);
	foreignKeyAttributeMenuItem = new JCheckBoxMenuItem(
	    locale.getString("TREE_AttrChkBoxFK"), iconFk);
  }

  /**
   * Getter for the PopupMenu with all Listeners
   * 
   * @return JPopupMenu for Attributes
   */
  public JPopupMenu getAttrPopupMenu() {

	JPopupMenu attrPopUpMenu = new JPopupMenu();

	JMenuItem deleteAttributeMenuItem = new JMenuItem(
	    locale.getString("TREE_AttrDelete"), iconDelete);
	// deleteAttributeMenuItem.setHorizontalAlignment(JMenuItem.RIGHT);
	deleteAttributeMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.DelAttribute);
		attrLogic.deleteAttribute();
		tree.setSelectedItem(x);
	  }
	});
	attrPopUpMenu.add(deleteAttributeMenuItem);

	JMenuItem renameAttributeMenuItem = new JMenuItem(
	    locale.getString("TREE_AttrRename"), iconRename);
	renameAttributeMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.RenameAttr);
		attrLogic.renameAttribute();
		tree.setSelectedItem(x);
	  }
	});
	attrPopUpMenu.add(renameAttributeMenuItem);

	primaryKeyAttributeMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.TogglePk);
		attrLogic.togglePK();
		tree.setSelectedItem(x);
	  }
	});
	attrPopUpMenu.add(primaryKeyAttributeMenuItem);

	foreignKeyAttributeMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.ToggleFk);
		attrLogic.toggleFK();
		tree.setSelectedItem(x);
	  }
	});
	attrPopUpMenu.add(foreignKeyAttributeMenuItem);

	JMenu typeRelationMenu = new JMenu(locale.getString("TREE_AttrType"));
	attrPopUpMenu.add(typeRelationMenu);
	grp = new ButtonGroup();
	for (String s : mySql.getInstance().getTypes()) {
	  JMenuItem menuItem = new JRadioButtonMenuItem(s);
	  menuItem.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
		  int x = tree.getNewSelectedItem(TreeEnum.SwitchType);
		  attrLogic.setType(GetSelectedRadioButton.getSelection(grp).getText());
		  tree.setSelectedItem(x);
		}
	  });
	  grp.add(menuItem);
	  typeRelationMenu.add(menuItem);
	}

	return attrPopUpMenu;

  }

  /**
   * Method which updates the Checkboxes in the JPopupMenu according
   * to the attributes PK/FK status
   */
  public void updateElements() {
	Attribute attr = tree.getAttribute();
	if (attr.getIsPrimaryKey()) {
	  primaryKeyAttributeMenuItem.setSelected(true);
	} else {
	  primaryKeyAttributeMenuItem.setSelected(false);
	}

	if (attr.getIsForeignKey()) {
	  foreignKeyAttributeMenuItem.setSelected(true);
	} else {
	  foreignKeyAttributeMenuItem.setSelected(false);
	}

	for (Enumeration<AbstractButton> e = grp.getElements(); e.hasMoreElements();) {
	  JRadioButtonMenuItem b = (JRadioButtonMenuItem) e.nextElement();
	  if (b.getText().equalsIgnoreCase(attr.getType())) {
		b.setSelected(true);
	  }
	}
  }
}
