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

package dba.gui;

import dba.options.Options;
import dba.utils.GetIcons;
import dba.utils.Localization;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
  private Options options;
  private JComboBox<String> comboBobxLocale;
  private JCheckBox checkBoxTipOfTheDay;
  private String currentLocale;
  private Localization locale;

  /**
   * Create the dialog.
   */
  public optionsMenu() {
    super();
    locale = Localization.getInstance();
    options = Options.getInstance();
    currentLocale = options.getLanguage();
    frame = this;
    this.setModal(true);
    frame.setTitle(locale.getString("OPT_FrameTitle"));
    GetIcons getIcons = GetIcons.getInstance();
    frame.setIconImage(getIcons.getIconOptionsFrame().getImage());
    setMinimumSize(new Dimension(450, 150));
    setSize(350, 150);
    getContentPane().setLayout(new BorderLayout());
    JPanel contentPanel = new JPanel();
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

    comboBobxLocale = new JComboBox<>();

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
