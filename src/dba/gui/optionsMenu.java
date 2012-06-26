package dba.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dba.options.Options;
import dba.utils.GetIcons;
import dba.utils.Localization;

import net.miginfocom.swing.MigLayout;

/**
 * Class to print the options menu frame
 * 
 * @author Andreas Freitag
 */
public class optionsMenu extends JDialog {
  /**
   * 
   */
  private static final long serialVersionUID = 7322481083260268322L;
  private JDialog frame;
  private final JPanel contentPanel = new JPanel();
  private GetIcons getIcons;
  private Options options;
  private JComboBox<String> comboBobxLocale;
  private JCheckBox checkBoxTipOfTheDay;
  private String currentLocale;
  private Localization locale;

  /**
   * Create the dialog.
   */
  public optionsMenu() {
	locale = Localization.getInstance();
	options = Options.getInstance();
	currentLocale = options.getLanguage();
	frame = this;
	this.setModal(true);
	frame.setTitle(locale.getString("OPT_FrameTitle"));
	getIcons = GetIcons.getInstance();
	frame.setIconImage(getIcons.getIconOptionsFrame().getImage());
	setMinimumSize(new Dimension(450, 150));
	setSize(350, 150);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setLayout(new MigLayout("fillx"));
	// contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);

	JPanel buttonPane = new JPanel();
	buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	getContentPane().add(buttonPane, BorderLayout.SOUTH);

	JButton okButton = new JButton(locale.getString("OPT_OK"));
	okButton.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent e) {
		String selectedLang = options.getKeyByLanguage(
		    options.getAvailableLocale(),
		    (String) comboBobxLocale.getSelectedItem());
		options.setLanguage(selectedLang);
		if (!currentLocale.equalsIgnoreCase(options.getLanguage())) {
		  JOptionPane.showMessageDialog(null, locale.getString("OPT_Restart"));
		}
		options.setShowTippsOnStartup(checkBoxTipOfTheDay.isSelected());
		options.writeOptions();
		frame.dispose();
	  }
	});
	buttonPane.add(okButton);
	getRootPane().setDefaultButton(okButton);

	JButton cancelButton = new JButton(locale.getString("OPT_Cancel"));
	cancelButton.addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent arg0) {
		frame.dispose();
	  }
	});
	buttonPane.add(cancelButton);

	comboBobxLocale = new JComboBox<String>();

	checkBoxTipOfTheDay = new JCheckBox();

	initComponentsStatus();
	contentPanel.add(new JLabel(locale.getString("OPT_Language")), "growx");
	contentPanel.add(comboBobxLocale, "alignx right, wrap");
	contentPanel.add(new JLabel(locale.getString("OPT_ShowTOD")),
	    "alignx left, growx");
	contentPanel.add(checkBoxTipOfTheDay, "alignx right, wrap");

	setLocationRelativeTo(null);
  }

  private void initComponentsStatus() {
	checkBoxTipOfTheDay.setSelected(options.getShowTippsOnStartup());

	for (String locale : options.getAvailableLocale().values()) {
	  comboBobxLocale.addItem(locale);
	}
	comboBobxLocale.setSelectedItem(options.getAvailableLocale().get(
	    options.getLanguage()));
  }
}
