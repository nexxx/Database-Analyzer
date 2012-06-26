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
public class FdPopupFactory implements constants {
  private CustomTree tree;
  private Localization locale;
  private ImageIcon iconEdit;
  private ImageIcon iconDelete;
  private FdLogic fdLogic;

  /**
   * Defaultconstructor
   * 
   * @param jtree
   *          JTree with the DB
   */
  public FdPopupFactory(CustomTree jtree) {
	fdLogic = new FdLogic();
	tree = jtree;
	locale = Localization.getInstance();
	GetIcons getIcons = GetIcons.getInstance();
	iconEdit = getIcons.getPopupEdit();
	iconDelete = getIcons.getPopupDelete();
  }

  /**
   * Getter for the PopupMenu with all Listeners
   * 
   * @return JPopupMenu for Database
   */
  public JPopupMenu getFdPopupMenu() {
	JPopupMenu fdPopUpMenu = new JPopupMenu();

	JMenuItem editFunctionalDependencyMenuItem = new JMenuItem(
	    locale.getString("TREE_FDEdit"), iconEdit);
	editFunctionalDependencyMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.EditFd);
		fdLogic.editFd();
		tree.setSelectedItem(x);
	  }
	});
	fdPopUpMenu.add(editFunctionalDependencyMenuItem);

	JMenuItem deleteFunctionalDependencyMenuItem = new JMenuItem(
	    locale.getString("TREE_FDDelete"), iconDelete);
	deleteFunctionalDependencyMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.DelFD);
		fdLogic.deleteFd();
		tree.setSelectedItem(x);
	  }
	});
	fdPopUpMenu.add(deleteFunctionalDependencyMenuItem);

	return fdPopUpMenu;
  }

}
