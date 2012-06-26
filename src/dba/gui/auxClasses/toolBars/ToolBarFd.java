package dba.gui.auxClasses.toolBars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dba.gui.auxClasses.CustomTree;
import dba.gui.auxClasses.DatabaseTreePanel;
import dba.gui.auxClasses.FdLogic;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.utils.TreeEnum;


/**
 * Class to provide the ToolBar for FDs (extends ToolBar class)
 * 
 * @author Andreas Freitag
 */
public class ToolBarFd extends ToolBar {
  private CustomTree tree;
  private FdLogic fdLogic;

  /**
   * 
   */
  private static final long serialVersionUID = -3843390455939145286L;

  public ToolBarFd(DatabaseTreePanel dbTreePanel, RelationView relationView,
	  RelationDetailsView relationDetailsView) {
	super(relationView, relationDetailsView, dbTreePanel);

	tree = dbTreePanel.getTree();
	fdLogic = new FdLogic();

	JButton btnDelete = new JButton(super.getIcons.getTbDelete());
	JButton btnEdit = new JButton(super.getIcons.getTbEdit());

	btnDelete.setToolTipText(super.locale.getString("TREE_FDDelete"));
	btnEdit.setToolTipText(super.locale.getString("TREE_FDEdit"));

	btnDelete.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.DelFD);
		fdLogic.deleteFd();
		tree.setSelectedItem(x);
	  }
	});

	btnEdit.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.EditFd);
		fdLogic.editFd();
		tree.setSelectedItem(x);
	  }
	});

	add(btnDelete);
	add(btnEdit);
  }

}
