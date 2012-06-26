/******************************************************************************
 * Copyright: GPL v3                                                          *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package dba.gui.auxClasses.toolBars;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import data.TimeLine;
import data.events.ChangeListener;
import data.events.Time;
import dba.gui.auxClasses.DatabaseTreePanel;
import dba.gui.auxClasses.GuiLogic;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.options.FeedbackEnum;
import dba.utils.GetIcons;
import dba.utils.Localization;

/**
 * Class which extends JToolBar and implements Observer. Listens to
 * zoom changes from JGraph Panel
 * 
 * @author Andreas Freitag
 * 
 */
public class ToolBar extends JToolBar implements Observer {
  protected GetIcons getIcons;
  protected Localization locale;

  /**
   * 
   */
  private static final long serialVersionUID = -4014383640978265021L;
  private GuiLogic guiLogic;
  private RelationView relationView;
  private RelationDetailsView relationDetailView;
  private JButton btnSave;
  private JButton btnNew;
  private JButton btnOpen;
  private JButton btnUndo;
  private JButton btnRedo;
  private JComboBox<String> cmbZoom;
  private boolean allowCmbZoom;
  private FeedbackbarPanel feedbackbarPanel;

  /**
   * Defaultconstructor to create Toolbar
   */
  public ToolBar(RelationView relView, RelationDetailsView relDetailView,
	  DatabaseTreePanel dbTreePanel) {
	super();
	feedbackbarPanel = FeedbackbarPanel.getInstance();
	guiLogic = new GuiLogic(dbTreePanel);
	getIcons = GetIcons.getInstance();
	locale = Localization.getInstance();
	relationView = relView;
	relationDetailView = relDetailView;
	allowCmbZoom = true;

	setLayout(new FlowLayout(FlowLayout.LEFT));

	TimeLine.getInstance().addChangeListener(new ChangeListener() {
	  @Override
	  public void Change(data.events.Change change) {
		if (change.getTime() == Time.AFTERCHANGE) {
		  btnUndo.setEnabled(TimeLine.getInstance().getBackwardPossible());
		  btnRedo.setEnabled(TimeLine.getInstance().getForwardPossible());
		}
	  }
	});

	btnNew = new JButton(getIcons.getTbNew());
	btnOpen = new JButton(getIcons.getTbOpen());
	btnSave = new JButton(getIcons.getTbSave());
	btnUndo = new JButton(getIcons.getTbUndo());
	btnRedo = new JButton(getIcons.getTbRedo());
	cmbZoom = new JComboBox<String>(relationView.getZoomFactors());

	btnNew.setToolTipText(locale.getString("GUI_New"));
	btnOpen.setToolTipText(locale.getString("GUI_Open"));
	btnSave.setToolTipText(locale.getString("GUI_Save"));
	btnUndo.setToolTipText(locale.getString("GUI_Undo"));
	btnRedo.setToolTipText(locale.getString("GUI_Redo"));

	cmbZoom.setToolTipText(locale.getString("GUI_ZoomFactor"));
	cmbZoom.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

	relationView.addObserver(this);
	relationDetailView.addObserver(this);

	btnNew.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		FeedbackEnum returnVal = guiLogic.newDatabase();
		if (returnVal == FeedbackEnum.SUCCESSFUL) {
		  feedbackbarPanel.showFeedback(locale.getString("FB_NewDB"),
			  FeedbackEnum.SUCCESSFUL);
		}
	  }
	});

	btnOpen.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		FeedbackEnum returnVal = guiLogic.open();
		if (returnVal == FeedbackEnum.FAILED) {
		  feedbackbarPanel.showFeedback(locale.getString("FB_OpenFailed"),
			  FeedbackEnum.FAILED);
		}
	  }
	});

	btnSave.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		FeedbackEnum returnVal = guiLogic.save();
		if (returnVal == FeedbackEnum.SUCCESSFUL) {
		  feedbackbarPanel.showFeedback(locale.getString("FB_Save"),
			  FeedbackEnum.SUCCESSFUL);
		} else if (returnVal == FeedbackEnum.FAILED) {
		  feedbackbarPanel.showFeedback(locale.getString("FB_SaveFailed"),
			  FeedbackEnum.FAILED);
		}
	  }
	});

	btnUndo.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		guiLogic.undo();
	  }
	});

	btnRedo.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		guiLogic.redo();
	  }
	});

	cmbZoom.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (allowCmbZoom) {
		  relationView.zoom((String) cmbZoom.getSelectedItem());
		  relationDetailView.zoom((String) cmbZoom.getSelectedItem());
		}
	  }
	});

	add(btnNew);
	add(btnOpen);
	add(btnSave);
	add(btnUndo);
	add(btnRedo);
	add(cmbZoom);

	setFloatable(false);

  }

  private void updateZoom(Object observable) {
	if (observable == relationView) {
	  updateComboZoom(relationView.getZoomFactors());
	  relationDetailView.zoom(relationView.getZoomFactors()[0]);
	} else if (observable == relationDetailView) {
	  updateComboZoom(relationDetailView.getZoomFactors());
	  relationView.zoom(relationDetailView.getZoomFactors()[0]);
	}
  }

  /**
   * Updates the selectable zoom-factors
   */
  private void updateComboZoom(String[] zoomFactors) {
	allowCmbZoom = false;

	cmbZoom.removeAllItems();
	for (String zoomFactor : zoomFactors) {
	  cmbZoom.addItem(zoomFactor);
	}
	allowCmbZoom = true;
  }

  @Override
  public void update(Observable arg0, Object arg1) {
	if (arg1 != null) {
	  updateZoom(arg1);
	}
  }

}
