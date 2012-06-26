package dba.gui.auxClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import data.NormalForm;
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
public class RelPopupFactory implements constants {
  private CustomTree tree;
  private Localization locale;
  private ImageIcon iconAddAttr;
  private ImageIcon iconAddFd;
  private ImageIcon iconEdit;
  private ImageIcon iconDelete;
  private ImageIcon iconRename;
  private ImageIcon iconInspect;
  private ImageIcon icon2NF;
  private ImageIcon icon3NF;
  private ImageIcon iconBCNF;
  private JMenuItem secondNormalFormRelationMenuItem;
  private JMenuItem thirdNormalFormRelationMenuItem;
  private JMenuItem boyceCoddNormalFormRelationMenuItem;
  private JMenu optimizeRelationMenu;
  private RelationLogic relLogic;
  private JMenuItem functionalDependencyMenuItem;

  /**
   * Defaultconstructor
   * 
   * @param jtree
   *          JTree with the DB
   */
  public RelPopupFactory(CustomTree jtree) {
	relLogic = new RelationLogic();
	tree = jtree;
	locale = Localization.getInstance();
	GetIcons getIcons = GetIcons.getInstance();
	iconAddAttr = getIcons.getPopupAttribute();
	iconAddFd = getIcons.getButtonFd();
	iconEdit = getIcons.getPopupEdit();
	iconDelete = getIcons.getPopupDelete();
	iconRename = getIcons.getPopupRename();
	iconInspect = getIcons.getPopupInspect();
	icon2NF = getIcons.getPopupOptimize2NF();
	icon3NF = getIcons.getPopupOptimize3NF();
	iconBCNF = getIcons.getPopupOptimizeBCNF();
  }

  /**
   * Getter for the PopupMenu with all Listeners
   * 
   * @return JPopupMenu for RelationSchemes
   */
  public JPopupMenu getRelPopupMenu() {

	JPopupMenu relPopUpMenu = new JPopupMenu();

	JMenu addRelationMenu = new JMenu(locale.getString("TREE_RelAdd"));
	relPopUpMenu.add(addRelationMenu);

	JMenuItem attributeRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_RelAttr"), iconAddAttr);
	attributeRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddAttribute);
		relLogic.addAttribute();
		tree.setSelectedItem(x);
	  }
	});
	addRelationMenu.add(attributeRelationMenuItem);

	functionalDependencyMenuItem = new JMenuItem(
	    locale.getString("TREE_RelFD"), iconAddFd);
	functionalDependencyMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddFD);
		relLogic.addFd();
		tree.setSelectedItem(x);
	  }
	});
	addRelationMenu.add(functionalDependencyMenuItem);

	JMenuItem wizardRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_RelEditWizard"), iconEdit);
	wizardRelationMenuItem.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.EditRel);
		relLogic.editRelationWizard();
		tree.setSelectedItem(x);
	  }
	});
	relPopUpMenu.add(wizardRelationMenuItem);

	JMenuItem deleteRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_RelDel"), iconDelete);
	deleteRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.DelRelation);
		relLogic.deleteRelation();
		tree.setSelectedItem(x);
	  }
	});
	relPopUpMenu.add(deleteRelationMenuItem);

	JMenuItem renameRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_RelRen"), iconRename);
	renameRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.RenameRel);
		relLogic.renameRelation();
		tree.setSelectedItem(x);
	  }
	});
	relPopUpMenu.add(renameRelationMenuItem);

	JMenuItem inspectRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_RelInspect"), iconInspect);
	inspectRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.InspectRel);
		relLogic.inspectRelation();
		tree.setSelectedItem(x);
	  }
	});
	relPopUpMenu.add(inspectRelationMenuItem);

	optimizeRelationMenu = new JMenu(locale.getString("TREE_RelOptimize"));
	relPopUpMenu.add(optimizeRelationMenu);

	secondNormalFormRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_Rel2NF"), icon2NF);
	secondNormalFormRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
		relLogic.optimizeRelation(NormalForm.SECOND);
		tree.setSelectedItem(x);
	  }
	});
	optimizeRelationMenu.add(secondNormalFormRelationMenuItem);

	thirdNormalFormRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_Rel3NF"), icon3NF);
	thirdNormalFormRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
		relLogic.optimizeRelation(NormalForm.THIRD);
		tree.setSelectedItem(x);
	  }
	});
	optimizeRelationMenu.add(thirdNormalFormRelationMenuItem);

	boyceCoddNormalFormRelationMenuItem = new JMenuItem(
	    locale.getString("TREE_RelBCNF"), iconBCNF);
	boyceCoddNormalFormRelationMenuItem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
		relLogic.optimizeRelation(NormalForm.BOYCECODD);
		tree.setSelectedItem(x);
	  }
	});
	optimizeRelationMenu.add(boyceCoddNormalFormRelationMenuItem);

	return relPopUpMenu;
  }

  public void setEnabledOpti2NF(boolean enabled) {
	secondNormalFormRelationMenuItem.setEnabled(enabled);
  }

  public void setEnabledOpti3NF(boolean enabled) {
	thirdNormalFormRelationMenuItem.setEnabled(enabled);
  }

  public void setEnabledOpti(boolean enabled) {
	optimizeRelationMenu.setEnabled(enabled);
  }

  public void setEnabledFD(boolean enabled) {
	functionalDependencyMenuItem.setEnabled(enabled);
  }
}
