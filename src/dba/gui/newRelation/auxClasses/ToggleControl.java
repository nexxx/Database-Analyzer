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

package dba.gui.newRelation.auxClasses;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A Control that contains several ToggleButtons
 *
 * @author Sebastian Theuermann
 */
public class ToggleControl extends JPanel {

  private static final long serialVersionUID = -7899127881352799141L;
  private Orientation orientation;
  private ArrayList<JToggleButton> buttons;
  private JToggleButton selectedButton;
  private ButtonGroup btnGroup;

  // @wbp.parser.constructor

  /**
   * A ToggleButton is created and added for each buttonName
   *
   * @param buttonNames the names of the Buttons to create
   */
  public ToggleControl(String[] buttonNames) {
    super();
    orientation = Orientation.HORIZONTAL;
    refresh(buttonNames);
  }

  /**
   * Refreshes the displayed Strings
   *
   * @param buttonNames to display
   */
  private void refresh(String[] buttonNames) {
    if (buttons != null) {
      for (JToggleButton btn : buttons) {
        this.remove(btn);
      }
    }

    buttons = new ArrayList<>();
    btnGroup = new ButtonGroup();
    selectedButton = new JToggleButton();

    initGui(buttonNames);
  }

  /**
   * Sets the Layout and adds new Buttons
   *
   * @param buttonNames the name of the buttons to display
   */
  private void initGui(String[] buttonNames) {
    if (orientation == Orientation.HORIZONTAL) {
      this.setLayout(new GridLayout(1, 0, 0, 0));
    } else if (orientation == Orientation.VERTICAL) {
      this.setLayout(new GridLayout(0, 1, 0, 0));
    }

    for (String btnName : buttonNames) {
      addNewButton(btnName);
    }

  }

  /**
   * Adds a new JToggleButton to the Control
   *
   * @param name of the Button to add
   */
  private void addNewButton(String name) {
    JToggleButton newTglBtn = new JToggleButton(name);

    newTglBtn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        JToggleButton thisButton = (JToggleButton) e.getComponent();
        selectButton(thisButton);
      }
    });

    this.add(newTglBtn);
    buttons.add(newTglBtn);
    btnGroup.add(newTglBtn);
  }

  /**
   * Selects a given Button (deselects all others)
   *
   * @param btn the Button to select
   */
  private void selectButton(JToggleButton btn) {
    selectedButton = btn;
    ToggleControl.this.firePropertyChange("SelectedButton", btn,
            getSelectedButtonIndex());
  }

  /**
   * Returns the Index of the Selected Button
   *
   * @return the Index starting left with 0
   */
  public int getSelectedButtonIndex() {
    return buttons.indexOf(selectedButton);
  }

  /**
   * Sets the SelectedButton via his index
   *
   * @param index of the Button to select
   */
  public void setSelectedButtonIndex(int index) {
    buttons.get(index).setSelected(true);
    selectButton(buttons.get(index));
  }

  /**
   * Returns the Text of the Selected Button
   *
   * @return the Text of the Button
   */
  public String getSelectedButtonText() {
    return selectedButton.getText();
  }

  /**
   * Sets the Icon of a ToggleButton
   *
   * @param index of the Button
   * @param image to apply to the Button
   */
  public void setIconOfButtonAtIndex(int index, ImageIcon image) {
    buttons.get(index).setIcon(image);
  }

}
