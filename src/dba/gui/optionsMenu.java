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
  private String currentBgColor;
  private String currentAttrColor;
  private String currentRelColor;
  private String currentFontColor;
  private Localization locale;
  private String colorBG;
  private String colorAttr;
  private String colorRel;
  private String colorFont;
  private JPanel pnlBG;
  private JPanel pnlAttr;
  private JPanel pnlRel;
  private JPanel pnlFont;

  /**
   * Create the dialog.
   */
  public optionsMenu() {
    super();
    locale = Localization.getInstance();
    options = Options.getInstance();
    currentLocale = options.getLanguage();
    currentAttrColor = options.getAttributeColor();
    currentBgColor = options.getBackgroundColor();
    currentFontColor = options.getFontColor();
    currentRelColor = options.getRelationColor();
    frame = this;
    setResizable(false);
    this.setModal(true);
    frame.setTitle(locale.getString("OPT_FrameTitle"));
    GetIcons getIcons = GetIcons.getInstance();
    frame.setIconImage(getIcons.getIconOptionsFrame().getImage());
    setMinimumSize(new Dimension(450, 150));
    getContentPane().setLayout(new BorderLayout());
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    getContentPane().add(contentPanel, BorderLayout.CENTER);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton okButton = new JButton(locale.getString("Ok"));
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedLang = options.getKeyByLanguage(options.getAvailableLocale(), (String) comboBobxLocale.getSelectedItem());
        options.setLanguage(selectedLang);
        options.setShowTippsOnStartup(checkBoxTipOfTheDay.isSelected());
        options.setAttributeColor(colorAttr);
        options.setBackgroundColor(colorBG);
        options.setFontColor(colorFont);
        options.setRelationColor(colorRel);

        boolean needToRestart = !currentLocale.equalsIgnoreCase(options.getLanguage());
        needToRestart = needToRestart || !currentAttrColor.equalsIgnoreCase(options.getAttributeColor());
        needToRestart = needToRestart || !currentBgColor.equalsIgnoreCase(options.getBackgroundColor());
        needToRestart = needToRestart || !currentRelColor.equalsIgnoreCase(options.getRelationColor());
        needToRestart = needToRestart || !currentFontColor.equalsIgnoreCase(options.getFontColor());
        if (needToRestart) {
          JOptionPane.showMessageDialog(null, locale.getString("OPT_Restart"));
        }
        options.writeOptions();
        frame.dispose();
      }
    });
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);

    JButton cancelButton = new JButton(locale.getString("Cancel"));
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        frame.dispose();
      }
    });
    buttonPane.add(cancelButton);

    JTabbedPane tabbedPane = new JTabbedPane();

    contentPanel.add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.addTab(locale.getString("OPT_GeneralTab"), createGeneralPanel());
    tabbedPane.addTab(locale.getString("OPT_ThemeTab"), createThemePanel());

    pack();
    setLocationRelativeTo(null);
  }

  private JPanel createGeneralPanel() {
    JPanel panel = new JPanel(new MigLayout("fillx"));
    comboBobxLocale = new JComboBox<>();
    checkBoxTipOfTheDay = new JCheckBox();

    checkBoxTipOfTheDay.setSelected(options.getShowTippsOnStartup());
    for (String locale : options.getAvailableLocale().values()) {
      comboBobxLocale.addItem(locale);
    }
    comboBobxLocale.setSelectedItem(options.getAvailableLocale().get(options.getLanguage()));

    panel.add(new JLabel(locale.getString("OPT_Language")), "growx");
    panel.add(comboBobxLocale, "alignx right, wrap");
    panel.add(new JLabel(locale.getString("OPT_ShowTOD")), "alignx left, growx");
    panel.add(checkBoxTipOfTheDay, "alignx right, wrap");

    return panel;
  }

  private JPanel createThemePanel() {
    colorBG = options.getBackgroundColor();
    colorAttr = options.getAttributeColor();
    colorFont = options.getFontColor();
    colorRel = options.getRelationColor();

    JPanel panel = new JPanel(new BorderLayout());
    JPanel panelLeft = new JPanel(new GridLayout(0, 2));
    panel.add(panelLeft, BorderLayout.CENTER);

    pnlBG = new JPanel();
    pnlBG.setBackground(Color.decode(options.getBackgroundColor()));
    JButton btnBG = new JButton("Background");
    btnBG.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, "Background Color", Color.decode(options.getBackgroundColor()));
        if (tmpColor != null) {
          colorBG = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlBG.setBackground(Color.decode(colorBG));
          pnlBG.revalidate();
        }
      }
    });
    panelLeft.add(btnBG);
    panelLeft.add(pnlBG);

    pnlAttr = new JPanel();
    pnlAttr.setBackground(Color.decode(options.getAttributeColor()));
    JButton btnAttr = new JButton("Attribute");
    btnAttr.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, "Attribute Color", Color.decode(options.getAttributeColor()));
        if (tmpColor != null) {
          colorAttr = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlAttr.setBackground(Color.decode(colorAttr));
          pnlAttr.revalidate();
        }
      }
    });
    panelLeft.add(btnAttr);
    panelLeft.add(pnlAttr);

    pnlRel = new JPanel();
    pnlRel.setBackground(Color.decode(options.getRelationColor()));
    JButton btnRel = new JButton("Relation");
    btnRel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, "Relation Color", Color.decode(options.getRelationColor()));
        if (tmpColor != null) {
          colorRel = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlRel.setBackground(Color.decode(colorRel));
          pnlRel.revalidate();
        }
      }
    });
    panelLeft.add(btnRel);
    panelLeft.add(pnlRel);

    pnlFont = new JPanel();
    pnlFont.setBackground(Color.decode(options.getFontColor()));
    JButton btnFont = new JButton("Font");
    btnFont.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, "Font Color", Color.decode(options.getFontColor()));
        if (tmpColor != null) {
          colorFont = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlFont.setBackground(Color.decode(colorFont));
          pnlFont.revalidate();
        }
      }
    });
    panelLeft.add(btnFont);
    panelLeft.add(pnlFont);

    JButton btnDefault = new JButton("Reset Theme");
    btnDefault.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        int n = JOptionPane.showConfirmDialog(null, locale.getString("OPT_Confirm"), locale.getString("OPT_ConfirmTitle"), JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
          colorBG = "#A7E2FF";
          pnlBG.setBackground(Color.decode(colorBG));
          pnlBG.revalidate();

          colorAttr = "#00FF00";
          pnlAttr.setBackground(Color.decode(colorAttr));
          pnlAttr.revalidate();

          colorRel = "#00CD00";
          pnlRel.setBackground(Color.decode(colorRel));
          pnlRel.revalidate();

          colorFont = "#000000";
          pnlFont.setBackground(Color.decode(colorFont));
          pnlFont.revalidate();
        }
      }
    });
    panelLeft.add(btnDefault);

    return panel;
  }
}
