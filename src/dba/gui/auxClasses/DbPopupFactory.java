package dba.gui.auxClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
public class DbPopupFactory implements constants {
  private CustomTree tree;
  private Localization locale;
  private ImageIcon iconWizard;
  private ImageIcon iconRelation;
  private ImageIcon iconInspect;
  private DatabaseLogic dbLogic;
  private JMenuItem inspectDatabaseMenuItem;

  /**
   * Defaultconstructor
   * 
   * @param jtree
   *          JTree with the DB
   */
  public DbPopupFactory(CustomTree jtree) {
	tree = jtree;
	locale = Localization.getInstance();
	GetIcons getIcon = GetIcons.getInstance();
	iconWizard = getIcon.getPopupWizard();
	iconRelation = getIcon.getPopupRelation();
	iconInspect = getIcon.getPopupInspect();
	dbLogic = new DatabaseLogic();

  }

  /**
   * Getter for the PopupMenu with all Listeners
   * 
   * @return JPopupMenu for Database
   */
  public JPopupMenu getDbPopupMenu() {
	JPopupMenu dbPopUpMenu = new JPopupMenu();

	JMenuItem wizardDatabaseMenuItem = new JMenuItem(
	    locale.getString("TREE_DBNewRelWiz"), iconWizard);
	wizardDatabaseMenuItem.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
		dbLogic.newWizardRelation();
		tree.setSelectedItem(x);
	  }
	});
	dbPopUpMenu.add(wizardDatabaseMenuItem);

	JMenuItem addDatabaseMenuItem = new JMenuItem(
	    locale.getString("TREE_DBAddEmptyRel"), iconRelation);
	addDatabaseMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
		dbLogic.newEmptyRelation();
		tree.setSelectedItem(x);
	  }
	});
	dbPopUpMenu.add(addDatabaseMenuItem);

	inspectDatabaseMenuItem = new JMenuItem(locale.getString("TREE_DBInspect"),
	    iconInspect);
	inspectDatabaseMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.InspectDb);
		dbLogic.inspectRelation();
		tree.setSelectedItem(x);
	  }
	});
	dbPopUpMenu.add(inspectDatabaseMenuItem);

	return dbPopUpMenu;

  }

  /**
   * Enable/Disable the inspect button
   * 
   * @param enabled
   *          true/false
   */
  public void setEnabledInspect(boolean enabled) {
	inspectDatabaseMenuItem.setEnabled(enabled);
  }
}
