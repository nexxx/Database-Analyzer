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

import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.options.FeedbackEnum;
import dba.options.Options;
import dba.utils.GetIcons;
import dba.utils.Localization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


/**
 * Class which shows a Tip of the Day Frame. Tips are loaded from
 * /res/tips and can easily be extended by adding new lines
 *
 * @author Andreas Freitag
 */
public class tippOfTheDayFrame extends JDialog {
  private ArrayList<String> tips;
  private Options options;
  private JDialog jDialog;
  private JCheckBox box;
  private String text;
  private JTextPane pane;
  private int lastTipIndex;
  private Localization locale;

  private static final long serialVersionUID = 8212600312769042926L;

  /**
   * Default constructor which shows the TipOfTheDay Frame when the
   * option is enables
   */
  public tippOfTheDayFrame() {
    super();
    options = Options.getInstance();
    locale = Localization.getInstance();

    if (!options.getShowTippsOnStartup()) {
      return;
    }

    lastTipIndex = 0;
    jDialog = this;

    tips = new ArrayList<>();
    setModal(true);
    setTitle(locale.getString("TIP_FrameTitle"));
    GetIcons getIcons = GetIcons.getInstance();
    jDialog.setIconImage(getIcons.getIconTipFrame().getImage());

    JPanel basic = new JPanel();
    basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
    add(basic);

    JPanel topPanel = new JPanel(new BorderLayout(0, 0));
    topPanel.setMaximumSize(new Dimension(450, 0));
    JPanel pnlHint = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel hint = new JLabel(locale.getString("TIP_Msg"));
    hint.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
    pnlHint.add(new JLabel(getIcons.getInfoIcon()));
    pnlHint.add(hint);
    topPanel.add(pnlHint);

    JSeparator separator = new JSeparator();
    separator.setForeground(Color.gray);

    topPanel.add(separator, BorderLayout.SOUTH);

    basic.add(topPanel);

    JPanel textPanel = new JPanel(new BorderLayout());
    textPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
    pane = new JTextPane();

    text = getRandomTip();
    pane.setText(text);
    pane.setEditable(false);
    textPanel.add(new JScrollPane(pane));

    basic.add(textPanel);

    JPanel boxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));

    box = new JCheckBox(locale.getString("TIP_ShowTips"));
    box.setToolTipText(locale.getString("TIP_ShowTipsTooltip"));
    box.setSelected(true);

    boxPanel.add(box);
    basic.add(boxPanel);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton ntip = new JButton(locale.getString("TIP_NextTip"));
    ntip.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        text = getRandomTip();
        pane.setText(text);
      }
    });
    JButton close = new JButton(locale.getString("Close"));

    close.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        options.setShowTippsOnStartup(box.isSelected());
        options.writeOptions();
        jDialog.dispose();
      }
    });

    bottom.add(ntip);
    bottom.add(close);
    basic.add(bottom);

    bottom.setMaximumSize(new Dimension(450, 0));

    setSize(new Dimension(450, 350));
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  /**
   * Show the tip of the day frame
   */
  public void showTOD() {
    if (options.getShowTippsOnStartup()) {
      jDialog.setVisible(true);
    }
  }

  private String getRandomTip() {
    readTip();
    Random randomGenerator = new Random();
    int currentTip;
    do {
      currentTip = randomGenerator.nextInt(tips.size());
    } while (currentTip == lastTipIndex);
    lastTipIndex = currentTip;
    return tips.get(currentTip);
  }

  private void readTip() {

    try {
      InputStream is = getTipsFile();
      DataInputStream in = new DataInputStream(is);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      tips.clear();
      while ((strLine = br.readLine()) != null) {
        tips.add(strLine);
      }
    } catch (Exception e) {
      FeedbackbarPanel.getInstance().showFeedback(locale.getString("FB_ReadTipsFailed"), FeedbackEnum.FAILED);
      e.printStackTrace();
    }
  }

  private String getCurrentLang() {
    return options.getLanguage();
  }

  private InputStream getTipsFile() {
    InputStream is;

    is = getClass().getResourceAsStream("/res/tips/" + "tips_" + getCurrentLang() + ".txt");
    if (is == null) {
      is = getClass().getResourceAsStream("/res/tips/tips_en.txt");
    }

    return is;
  }
}
