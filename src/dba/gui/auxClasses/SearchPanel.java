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

package dba.gui.auxClasses;

import dba.gui.CustomTree;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.options.FeedbackEnum;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.Observable;
import dbaCore.data.Database;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observer;

/**
 * Provide the search panel
 *
 * @author Andreas Freitag
 */
public class SearchPanel extends JPanel implements Observable {
  private GetIcons getIcon;
  private JPanel panel;
  private JTextField txtSearch;
  private ArrayList<Object> searchResults;
  private JButton btnNext;
  private JButton btnPrev;
  private int searchIterator;
  private String lastSearchName = "THISWILLNEVERBEANAMEINADATABASE123!\"§@";
  private FeedbackbarPanel feedbackbarPanel;
  private ArrayList<Observer> observers;
  private Localization locale;


  public SearchPanel() {
    super();
    this.panel = this;
    this.observers = new ArrayList<>();
    this.searchResults = new ArrayList<>();
    this.getIcon = GetIcons.getInstance();
    this.feedbackbarPanel = FeedbackbarPanel.getInstance();
    this.locale = Localization.getInstance();

    panel.setLayout(new MigLayout("wrap 3", "[grow, fill][grow,fill,38:38:38][grow,fill,38:38:38]"));
    JLabel lblsearch = new JLabel(locale.getString("GUI_Search"));
    JButton btnClose = new JButton(getIcon.getButtonClose());
    btnClose.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        notifyObservers();
      }
    });

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

    panel.add(new JSeparator(), "span");
    panel.add(lblsearch, "span 2");
    panel.add(btnClose);
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
      feedbackbarPanel.showFeedback(txtSearch.getText() + " " + locale.getString("GUI_NotFound"), FeedbackEnum.FAILED);
    }
  }


  /**
   * Add a Observer to the Collection
   *
   * @param observer the observer to add
   * @return true for success
   */
  public boolean addObserver(Observer observer) {
    return observers.add(observer);
  }

  /**
   * Notifies Observers about change
   */
  protected void notifyObservers() {
    for (Observer stalker : observers) {
      stalker.update(null, this);
    }
  }
}
