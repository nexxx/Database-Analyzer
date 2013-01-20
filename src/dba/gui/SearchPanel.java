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
import dba.utils.GetIcons;
import dba.utils.Localization;
import dbaCore.data.Database;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Provide the search panel
 *
 * @author Andreas Freitag
 */
public class SearchPanel extends JPanel{
  private GetIcons getIcon;
  private JPanel panel;
  private JTextField txtSearch;
  private ArrayList<Object> searchResults;
  private JButton btnNext;
  private JButton btnPrev;
  private int searchIterator;
  private String lastSearchName = "THISWILLNEVERBEANAMEINADATABASE123!\"§@";
  private FeedbackbarPanel feedbackbarPanel;


  public SearchPanel() {
    super();
    this.panel = this;
    this.searchResults = new ArrayList<>();
    this.getIcon = GetIcons.getInstance();
    feedbackbarPanel = FeedbackbarPanel.getInstance();
    panel.setLayout(new MigLayout("wrap 3", "[grow, fill][grow,fill,38:38:38][grow,fill,38:38:38]"));

    btnNext = new JButton(getIcon.getNext());
    btnNext.setBorderPainted(false);
    btnNext.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if(searchResults.isEmpty() || !lastSearchName.equals(txtSearch.getText())) {
          lastSearchName = txtSearch.getText();
          search();
          return;
        }
        if((++searchIterator) < searchResults.size()) {
          CustomTree.getInstance().setSelectedNode(searchResults.get(searchIterator));
        } else {
          searchIterator = 0;
          CustomTree.getInstance().setSelectedNode(searchResults.get(searchIterator));
        }
      }
    });
    btnPrev = new JButton(getIcon.getPrev());
    btnPrev.setBorderPainted(false);
    btnPrev.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if(searchResults.isEmpty() || !lastSearchName.equals(txtSearch.getText())) {
          lastSearchName = txtSearch.getText();
          search();
          return;
        }
        if((--searchIterator) >= 0) {
          CustomTree.getInstance().setSelectedNode(searchResults.get(searchIterator));
        } else {
          searchIterator = searchResults.size()-1;
          CustomTree.getInstance().setSelectedNode(searchResults.get(searchIterator));
        }
      }
    });

    txtSearch = new JTextField();
    txtSearch.setColumns(Integer.MAX_VALUE);
    txtSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        lastSearchName = txtSearch.getText();
        search();
      }
    });
    panel.add(txtSearch);
    panel.add(btnPrev);
    panel.add(btnNext);

  }

  private void search(){
    Database database = CustomTree.getInstance().getDatabase();

    if (txtSearch.getText().isEmpty()) {
      return;
    }
    searchResults = database.search(txtSearch.getText());
    searchIterator = 0;
    if (!searchResults.isEmpty()) {
      CustomTree.getInstance().setSelectedNode(searchResults.get(searchIterator));
    } else {
      feedbackbarPanel.showFeedback(txtSearch.getText() + " " + Localization.getInstance().getString("GUI_NotFound"), FeedbackEnum.FAILED);
    }
  }
}
