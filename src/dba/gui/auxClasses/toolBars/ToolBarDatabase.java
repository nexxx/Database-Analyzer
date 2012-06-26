package dba.gui.auxClasses.toolBars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dba.gui.auxClasses.CustomTree;
import dba.gui.auxClasses.DatabaseLogic;
import dba.gui.auxClasses.DatabaseTreePanel;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.utils.TreeEnum;


/**
 * Class to provide the ToolBar for the Database (extends ToolBar
 * class)
 * 
 * @author Andreas Freitag
 */
public class ToolBarDatabase extends ToolBar {
  private CustomTree tree;
  private DatabaseLogic dbLogic;
  private JButton btnInspect;
  /**
   * 
   */
  private static final long serialVersionUID = -3843390455939145286L;

  public ToolBarDatabase(DatabaseTreePanel dbTreePanel,
	  RelationView relationView, RelationDetailsView relationDetailsView) {
	super(relationView, relationDetailsView, dbTreePanel);

	tree = dbTreePanel.getTree();
	JButton btnNewRelation = new JButton(super.getIcons.getTbWizard());
	JButton btnEmptyRelation = new JButton(super.getIcons.getTbRelation());
	btnInspect = new JButton(super.getIcons.getTbInspect());

	btnNewRelation.setToolTipText(super.locale.getString("TREE_DBNewRelWiz"));
	btnEmptyRelation.setToolTipText(super.locale
	    .getString("TREE_DBAddEmptyRel"));
	btnInspect.setToolTipText(super.locale.getString("TREE_DBInspect"));

	dbLogic = new DatabaseLogic();
	btnNewRelation.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
		dbLogic.newWizardRelation();
		tree.setSelectedItem(x);
	  }
	});

	btnEmptyRelation.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.AddRelation);
		dbLogic.newEmptyRelation();
		tree.setSelectedItem(x);
	  }
	});

	btnInspect.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		int x = tree.getNewSelectedItem(TreeEnum.InspectDb);
		dbLogic.inspectRelation();
		tree.setSelectedItem(x);
	  }
	});

	add(btnNewRelation);
	add(btnEmptyRelation);
	add(btnInspect);
  }

  /**
   * Enable disable inspect button
   * 
   * @param enabled
   *          true/false
   */
  public void setEnabledInspect(boolean enabled) {
	btnInspect.setEnabled(enabled);
  }

}
