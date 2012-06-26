package dba.gui.auxClasses.toolBars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import data.NormalForm;
import dba.gui.auxClasses.CustomTree;
import dba.gui.auxClasses.DatabaseTreePanel;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationLogic;
import dba.gui.auxClasses.RelationView;
import dba.utils.TreeEnum;

/**
 * Class to provide the ToolBar for Relations (extends ToolBar class)
 * 
 * @author Andreas Freitag
 */
public class ToolBarRelation extends ToolBar {
  private CustomTree tree;
  private RelationLogic relLogic;
  private JButton btn2NF;
  private JButton btn3NF;
  private JButton btnBCNF;
  private JButton btnNewFd;

  /**
   * 
   */
  private static final long serialVersionUID = -3843390455939145286L;

  public ToolBarRelation(DatabaseTreePanel dbTreePanel,
	  RelationView relationView, RelationDetailsView relationDetailsView) {
	super(relationView, relationDetailsView, dbTreePanel);

	tree = dbTreePanel.getTree();
	relLogic = new RelationLogic();
	JButton btnNewAttribute = new JButton(super.getIcons.getTbAttribute());
	btnNewFd = new JButton(super.getIcons.getTbFd());
	JButton btnEdit = new JButton(super.getIcons.getTbEdit());
	JButton btnDelete = new JButton(super.getIcons.getTbDelete());
	JButton btnRename = new JButton(super.getIcons.getTbRename());
	JButton btnInspect = new JButton(super.getIcons.getTbInspect());
	btn2NF = new JButton(super.getIcons.getTb2NF());
	btn3NF = new JButton(super.getIcons.getTb3NF());
	btnBCNF = new JButton(super.getIcons.getTbBCNF());

	btnNewAttribute.setToolTipText(super.locale.getString("TREE_RelAttr"));
	btnNewFd.setToolTipText(super.locale.getString("TREE_RelFD"));
	btnEdit.setToolTipText(super.locale.getString("TREE_RelEditWizard"));
	btnDelete.setToolTipText(super.locale.getString("TREE_RelDel"));
	btnRename.setToolTipText(super.locale.getString("TREE_RelRen"));
	btnInspect.setToolTipText(super.locale.getString("TREE_RelInspect"));
	btn2NF.setToolTipText(super.locale.getString("TREE_RelOptimize") + " - "
	    + super.locale.getString("TREE_Rel2NF"));
	btn3NF.setToolTipText(super.locale.getString("TREE_RelOptimize") + " - "
	    + super.locale.getString("TREE_Rel3NF"));
	btnBCNF.setToolTipText(super.locale.getString("TREE_RelOptimize") + " - "
	    + super.locale.getString("TREE_RelBCNF"));

	btnNewAttribute.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddAttribute);
		relLogic.addAttribute();
		tree.setSelectedItem(x);
	  }
	});

	btnNewFd.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddFD);
		relLogic.addFd();
		tree.setSelectedItem(x);
	  }
	});

	btnEdit.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.EditRel);
		relLogic.editRelationWizard();
		tree.setSelectedItem(x);
	  }
	});

	btnDelete.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.DelRelation);
		relLogic.deleteRelation();
		tree.setSelectedItem(x);
	  }
	});

	btnRename.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.RenameRel);
		relLogic.renameRelation();
		tree.setSelectedItem(x);
	  }
	});

	btnInspect.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.InspectRel);
		relLogic.inspectRelation();
		tree.setSelectedItem(x);
	  }
	});

	btn2NF.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent e) {
		int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
		relLogic.optimizeRelation(NormalForm.SECOND);
		tree.setSelectedItem(x);
	  }
	});

	btn3NF.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent e) {
		int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
		relLogic.optimizeRelation(NormalForm.THIRD);
		tree.setSelectedItem(x);
	  }
	});

	btnBCNF.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent e) {
		int x = tree.getNewSelectedItem(TreeEnum.OptimizeRel);
		relLogic.optimizeRelation(NormalForm.BOYCECODD);
		tree.setSelectedItem(x);
	  }
	});

	add(btnNewAttribute);
	add(btnNewFd);
	add(btnEdit);
	add(btnDelete);
	add(btnRename);
	add(btnInspect);
	add(btn2NF);
	add(btn3NF);
	add(btnBCNF);
	// add(cbOptimize);
  }

  public void setEnabledOpti2NF(boolean enabled) {
	btn2NF.setEnabled(enabled);
  }

  public void setEnabledOpti3NF(boolean enabled) {
	btn3NF.setEnabled(enabled);
  }

  public void setEnabledOpti(boolean enabled) {
	btnBCNF.setEnabled(enabled);
  }

  public void setEnabledFD(boolean enabled) {
	btnNewFd.setEnabled(enabled);
  }

}
