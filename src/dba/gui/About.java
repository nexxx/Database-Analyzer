package dba.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dba.utils.GetIcons;
import dba.utils.Localization;


/**
 * Class to show the JDialog for the about Window
 * 
 * @author Andreas Freitag
 */
public class About extends JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 3383162762318308818L;
  private final JPanel contentPanel = new JPanel();
  private JDialog jDialog;
  private Localization locale;
  private GetIcons getIcons;

  /**
   * Create the dialog.
   */
  @SuppressWarnings("static-access")
  public About() {
	locale = Localization.getInstance();
	getIcons = GetIcons.getInstance();

	jDialog = this;
	jDialog.setTitle(locale.getString("GUI_About"));
	jDialog.setModal(true);
	jDialog.setResizable(false);
	jDialog.setIconImage(getIcons.getIconAboutFrame().getImage());

	getContentPane().setLayout(new BorderLayout());
	contentPanel.setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	this.setDefaultCloseOperation(jDialog.DISPOSE_ON_CLOSE);
	getContentPane().add(contentPanel, BorderLayout.CENTER);

	JPanel buttonPane = new JPanel();
	buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
	getContentPane().add(buttonPane, BorderLayout.SOUTH);

	JButton okButton = new JButton("OK");
	okButton.setActionCommand("OK");
	buttonPane.add(okButton);
	getRootPane().setDefaultButton(okButton);
	okButton.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		jDialog.dispose();
	  }
	});

	String text = "<html>Database Analyzer<br><br>Version: 1.0<br>Authors: Andreas Freitag, Sebastian Theuermann,<br>Tanja Pongratz, Patrick Prinster, Matej Kollar<br><br>Visit http://www.databasenormalizer.com<br>(c) Copyright, 2012.  All rights reserved.</html>";
	JLabel lblText = new JLabel(text);
	lblText.setBackground(Color.white);
	lblText.setOpaque(true);
	lblText.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPanel.add(lblText, BorderLayout.CENTER);
	jDialog.pack();
	setLocationRelativeTo(null);
  }
}
