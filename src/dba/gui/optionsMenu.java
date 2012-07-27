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

import dba.init.Initialize;
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
  private String currentArrowFkColor;
  private String currentArrowFdColor;
  private Localization locale;
  private String colorBG;
  private String colorAttr;
  private String colorRel;
  private String colorFont;
  private String colorArrowFk;
  private String colorArrowFd;
  private JPanel pnlBG;
  private JPanel pnlAttr;
  private JPanel pnlRel;
  private JPanel pnlFont;
  private JPanel pnlArrowFk;
  private JPanel pnlArrowFd;
  private JComboBox<String> cbLaf;
  private JFrame mainFrame;

  /**
   * Create the dialog.
   */
  public optionsMenu(JFrame mFrame) {
    super();
    mainFrame = mFrame;
    locale = Localization.getInstance();
    options = Options.getInstance();
    currentLocale = options.getLanguage();
    currentAttrColor = options.getAttributeColor();
    currentBgColor = options.getBackgroundColor();
    currentFontColor = options.getFontColor();
    currentRelColor = options.getRelationColor();
    currentArrowFkColor = options.getArrowFKColor();
    currentArrowFdColor = options.getArrowFDColor();
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
        String selectedLang = options.getKeyByValue(options.getAvailableLocale(),
          (String) comboBobxLocale.getSelectedItem());
        String selectedLAF = options.getKeyByValue(options.getAvailLAF(), (String) cbLaf.getSelectedItem());
        options.setLanguage(selectedLang);
        options.setShowTippsOnStartup(checkBoxTipOfTheDay.isSelected());
        options.setAttributeColor(colorAttr);
        options.setBackgroundColor(colorBG);
        options.setFontColor(colorFont);
        options.setRelationColor(colorRel);
        options.setArrowFKColor(colorArrowFk);
        options.setArrowFDColor(colorArrowFd);
        options.setLookAndFeel(selectedLAF);

        boolean needToRestart = !currentLocale.equalsIgnoreCase(options.getLanguage());
        needToRestart = needToRestart || !currentAttrColor.equalsIgnoreCase(options.getAttributeColor());
        needToRestart = needToRestart || !currentBgColor.equalsIgnoreCase(options.getBackgroundColor());
        needToRestart = needToRestart || !currentRelColor.equalsIgnoreCase(options.getRelationColor());
        needToRestart = needToRestart || !currentArrowFkColor.equalsIgnoreCase(options.getArrowFKColor());
        needToRestart = needToRestart || !currentArrowFdColor.equalsIgnoreCase(options.getArrowFDColor());
        needToRestart = needToRestart || !currentFontColor.equalsIgnoreCase(options.getFontColor());
        if (needToRestart) {
          JOptionPane.showMessageDialog(null, locale.getString("OPT_Restart"));
        }
        options.writeOptions();

        Initialize.getInstance().setLookAndFeel();
        SwingUtilities.updateComponentTreeUI(mainFrame);

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

    checkBoxTipOfTheDay = new JCheckBox();
    checkBoxTipOfTheDay.setSelected(options.getShowTippsOnStartup());

    cbLaf = new JComboBox<>();
    for (String laf : options.getAvailLAF().values()) {
      cbLaf.addItem(laf);
    }
    cbLaf.setSelectedItem(options.getAvailLAF().get(options.getLookAndFeel()));

    comboBobxLocale = new JComboBox<>();
    for (String locale : options.getAvailableLocale().values()) {
      comboBobxLocale.addItem(locale);
    }
    comboBobxLocale.setSelectedItem(options.getAvailableLocale().get(options.getLanguage()));

    panel.add(new JLabel(locale.getString("OPT_Language")), "growx");
    panel.add(comboBobxLocale, "alignx right, wrap");
    panel.add(new JLabel(locale.getString("OPT_ShowTOD")), "alignx left, growx");
    panel.add(checkBoxTipOfTheDay, "alignx right, wrap");
    panel.add(new JLabel("LookAndFeel"), "growx");
    panel.add(cbLaf, "alignx right, wrap");

    return panel;
  }

  private JPanel createThemePanel() {
    colorBG = options.getBackgroundColor();
    colorAttr = options.getAttributeColor();
    colorFont = options.getFontColor();
    colorRel = options.getRelationColor();
    colorArrowFk = options.getArrowFKColor();
    colorArrowFd = options.getArrowFDColor();

    JPanel panel = new JPanel(new BorderLayout());
    JPanel panelLeft = new JPanel(new GridLayout(0, 2));
    panel.add(panelLeft, BorderLayout.CENTER);

    pnlBG = new JPanel();
    pnlBG.setBackground(Color.decode(options.getBackgroundColor()));
    JButton btnBG = new JButton(locale.getString("OPT_Background"));
    btnBG.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, locale.getString("OPT_BackgroundColor"),
          Color.decode(options.getBackgroundColor()));
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
    JButton btnAttr = new JButton(locale.getString("Attribute"));
    btnAttr.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, locale.getString("OPT_AttributeColor"),
          Color.decode(options.getAttributeColor()));
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
    JButton btnRel = new JButton(locale.getString("Relation"));
    btnRel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, locale.getString("OPT_RelationColor"),
          Color.decode(options.getRelationColor()));
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
    JButton btnFont = new JButton(locale.getString("OPT_Font"));
    btnFont.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, locale.getString("OPT_FontColor"),
          Color.decode(options.getFontColor()));
        if (tmpColor != null) {
          colorFont = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlFont.setBackground(Color.decode(colorFont));
          pnlFont.revalidate();
        }
      }
    });
    panelLeft.add(btnFont);
    panelLeft.add(pnlFont);

    pnlArrowFk = new JPanel();
    pnlArrowFk.setBackground(Color.decode(options.getArrowFKColor()));
    JButton btnArrowFk = new JButton(locale.getString("OPT_ArrowFk"));
    btnArrowFk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, locale.getString("OPT_ArrowFkColor"),
          Color.decode(options.getArrowFKColor()));
        if (tmpColor != null) {
          colorArrowFk = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlArrowFk.setBackground(Color.decode(colorArrowFk));
          pnlArrowFk.revalidate();
        }
      }
    });
    panelLeft.add(btnArrowFk);
    panelLeft.add(pnlArrowFk);

    pnlArrowFd = new JPanel();
    pnlArrowFd.setBackground(Color.decode(options.getArrowFDColor()));
    JButton btnArrowFd = new JButton(locale.getString("OPT_ArrowFd"));
    btnArrowFd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        Color tmpColor = JColorChooser.showDialog(null, locale.getString("OPT_ArrowFdColor"),
          Color.decode(options.getArrowFDColor()));
        if (tmpColor != null) {
          colorArrowFd = "#" + (Integer.toHexString(tmpColor.getRGB())).substring(2);
          pnlArrowFd.setBackground(Color.decode(colorArrowFd));
          pnlArrowFd.revalidate();
        }
      }
    });
    panelLeft.add(btnArrowFd);
    panelLeft.add(pnlArrowFd);

    JButton btnDefault = new JButton(locale.getString("OPT_ResetTheme"));
    btnDefault.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        int n = JOptionPane.showConfirmDialog(null, locale.getString("OPT_Confirm"),
          locale.getString("OPT_ConfirmTitle"), JOptionPane.YES_NO_OPTION);
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

          colorArrowFk = "#0095C7";
          pnlArrowFk.setBackground(Color.decode(colorArrowFk));
          pnlArrowFk.revalidate();

          colorArrowFd = "#000000";
          pnlArrowFd.setBackground(Color.decode(colorArrowFd));
          pnlArrowFd.revalidate();
        }
      }
    });
    panelLeft.add(btnDefault);

    return panel;
  }
}
