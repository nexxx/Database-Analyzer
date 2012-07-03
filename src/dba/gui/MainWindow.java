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


import dba.gui.auxClasses.DatabaseTreePanel;
import dba.gui.auxClasses.GuiLogic;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.gui.auxClasses.toolBars.*;
import dba.gui.metaInfoFrame.CustomerInfosFrame;
import dba.init.Initialize;
import dba.options.Feedback;
import dba.options.FeedbackEnum;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.OpenUrl;
import dba.utils.constants;
import dbaCore.data.Database;
import dbaCore.data.FunctionalDependency;
import dbaCore.data.NormalForm;
import dbaCore.data.TimeLine;
import dbaCore.data.events.Change;
import dbaCore.data.events.ChangeListener;
import dbaCore.data.events.Time;
import dbaCore.logic.Analysis.GeneralRelationCheck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Class which is a Observable. It represents and Paints the
 * MainWindow
 *
 * @author Andreas Freitag
 */
public class MainWindow implements constants, Observer {
  private JFrame frame;
  private JMenuItem saveMenuItem;
  private JMenuItem undoMenuItem;
  private JMenuItem redoMenuItem;
  private Database database;
  private DatabaseTreePanel dbTreePanel;
  private ImageIcon iconNew;
  private ImageIcon iconOpen;
  private ImageIcon iconSave;
  private ImageIcon iconExport;
  private ImageIcon iconClose;
  private ImageIcon iconMetaInfo;
  private ImageIcon iconOptions;
  private ImageIcon iconAbout;
  private ImageIcon iconHelp;
  private ImageIcon iconDonate;
  private ImageIcon iconGithib;
  private Localization locale;
  private JPanel pnlToolBar;
  private ToolBar toolBar;
  private ToolBarDatabase toolBarDatabase;
  private ToolBarRelation toolBarRelation;
  private ToolBarAttribute toolBarAttribute;
  private ToolBarFd toolBarFd;
  private GeneralRelationCheck checker;
  private GuiLogic guiLogic;
  private RelationView relationView;
  private RelationDetailsView relationDetailsView;
  private FeedbackbarPanel feedbackbarPanel;

  /**
   * Create the frame.
   */
  public MainWindow() {
    super();
    checker = new GeneralRelationCheck();
    GetIcons getIcon = GetIcons.getInstance();
    ImageIcon iconFrame = getIcon.getIconFrame();
    iconNew = getIcon.getMenuNew();
    iconOpen = getIcon.getMenuOpen();
    iconSave = getIcon.getMenuSave();
    iconExport = getIcon.getMenuExport();
    iconClose = getIcon.getMenuClose();
    ImageIcon iconUndo = getIcon.getMenuUndo();
    ImageIcon iconRedo = getIcon.getMenuRedo();
    iconMetaInfo = getIcon.getMenuEditInfos();
    iconOptions = getIcon.getMenuOptions();
    iconAbout = getIcon.getMenuAbout();
    iconHelp = getIcon.getMenuHelp();
    iconDonate = getIcon.getMenuDonate();
    iconGithib = getIcon.getMenuGithub();

    locale = Localization.getInstance();
    feedbackbarPanel = FeedbackbarPanel.getInstance();

    frame = new JFrame(locale.getString("GUI_FrameTitle") + " - " + locale.getString("GUI_FrameTitleNotSaved"));
    frame.setIconImage(iconFrame.getImage());
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        checkDirtyStateBeforeExiting();
      }
    });

    JPanel contentPane = new JPanel();
    // contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout());
    frame.setContentPane(contentPane);

    database = new Database();

    dbTreePanel = new DatabaseTreePanel(database);
    guiLogic = new GuiLogic(dbTreePanel);
    relationView = new RelationView(guiLogic);
    relationDetailsView = new RelationDetailsView(guiLogic);

    //guiLogic.showDbQuestion();
    WelcomeScreen wcs = new WelcomeScreen(guiLogic, database);
    FeedbackEnum retVal;
    do {
      wcs.showScreen();
      retVal = wcs.getRetVal();
    } while (retVal != FeedbackEnum.SUCCESSFUL);

    if (wcs.openClicked()) {
      updateDBAfterChange();
    }

    JTabbedPane displayTab = new JTabbedPane(SwingConstants.TOP);

    pnlToolBar = new JPanel(new BorderLayout());
    toolBar = new ToolBar(relationView, relationDetailsView, dbTreePanel);

    pnlToolBar.add(toolBar, BorderLayout.CENTER);
    contentPane.add(pnlToolBar, BorderLayout.PAGE_START);

    toolBarDatabase = new ToolBarDatabase(dbTreePanel, relationView, relationDetailsView);
    toolBarRelation = new ToolBarRelation(dbTreePanel, relationView, relationDetailsView);
    toolBarAttribute = new ToolBarAttribute(dbTreePanel, relationView, relationDetailsView);
    toolBarFd = new ToolBarFd(dbTreePanel, relationView, relationDetailsView);

    PropertyChangeListener changeListener = new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("TreeClick")) {
          if (((String) evt.getNewValue()).equalsIgnoreCase("Database")) {
            pnlToolBar.removeAll();
            if (CustomTree.getInstance().getDatabase().getDatabase().isEmpty()) {
              toolBarDatabase.setEnabledInspect(false);
            } else {
              toolBarDatabase.setEnabledInspect(true);
            }
            pnlToolBar.add(toolBarDatabase, BorderLayout.CENTER);
          } else if (((String) evt.getNewValue()).equalsIgnoreCase("Relation")) {
            pnlToolBar.removeAll();
            enableOptimizeButtons();
            pnlToolBar.add(toolBarRelation, BorderLayout.CENTER);
          } else if (((String) evt.getNewValue()).equalsIgnoreCase("Attribute")) {
            pnlToolBar.removeAll();
            toolBarAttribute.updateElements();
            pnlToolBar.add(toolBarAttribute, BorderLayout.CENTER);
          } else if (((String) evt.getNewValue()).equalsIgnoreCase("FD")) {
            pnlToolBar.removeAll();
            pnlToolBar.add(toolBarFd, BorderLayout.CENTER);
          } else {
            pnlToolBar.removeAll();
            pnlToolBar.add(toolBar, BorderLayout.CENTER);
          }
        }
      }
    };

    undoMenuItem = new JMenuItem(locale.getString("GUI_Undo"), iconUndo);
    undoMenuItem.setEnabled(false);
    redoMenuItem = new JMenuItem(locale.getString("GUI_Redo"), iconRedo);
    redoMenuItem.setEnabled(false);

    TimeLine.getInstance().initialize(database);

    TimeLine.getInstance().addChangeListener(new ChangeListener() {
      @Override
      public void Change(Change change) {
        if (change.getTime() == Time.AFTERCHANGE) {
          updateDBAfterChange();
          undoMenuItem.setEnabled(TimeLine.getInstance().getBackwardPossible());
          redoMenuItem.setEnabled(TimeLine.getInstance().getForwardPossible());
        }
      }
    });

    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setSize(1024, 600);
    frame.setMinimumSize(new Dimension(800, 480));
    frame.setLocationRelativeTo(null);

    Initialize init = new Initialize();
    init.init();
    Dimension minimumSizeSplitPane = new Dimension(100, 50);

    dbTreePanel.addPropertyChangeListener(changeListener);
    // relationView.addPropertyChangeListener(changeListener);

    JPanel pnlRight = new JPanel(new BorderLayout());
    pnlRight.setMinimumSize(minimumSizeSplitPane);
    pnlRight.add(relationView, BorderLayout.CENTER);
    pnlRight.add(feedbackbarPanel, BorderLayout.SOUTH);

    // RelationView
    displayTab.addTab(locale.getString("Relations"), relationView);

    // RelationDetails
    displayTab.addTab(locale.getString("GUI_RelationDetails"), relationDetailsView);

    pnlRight = new JPanel(new BorderLayout());
    pnlRight.setMinimumSize(minimumSizeSplitPane);
    pnlRight.add(displayTab, BorderLayout.CENTER);
    pnlRight.add(feedbackbarPanel, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dbTreePanel, pnlRight);
    splitPane.setDividerLocation(250);
    dbTreePanel.setMinimumSize(minimumSizeSplitPane);
    frame.add(splitPane, BorderLayout.CENTER);

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    createFileMenu(menuBar);
    createEditMenu(menuBar);
    createExtrasMenu(menuBar);
    createHelpMenu(menuBar);

    dbTreePanel.getTree().setSelectedItem(0);
  }

  /**
   * Getter for the MainFrame
   *
   * @return Mainframe
   */
  public JFrame getFrame() {
    return frame;
  }

  private void createFileMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu(locale.getString("GUI_File"));
    menuBar.add(fileMenu);

    JMenuItem newMenuItem = new JMenuItem(locale.getString("GUI_New"), iconNew);
    newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
    newMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        FeedbackEnum returnVal = guiLogic.newDatabase();
        if (returnVal == FeedbackEnum.SUCCESSFUL) {
          feedbackbarPanel.showFeedback(locale.getString("FB_NewDB"), FeedbackEnum.SUCCESSFUL);
        }
      }
    });
    fileMenu.add(newMenuItem);

    JMenuItem openMenuItem = new JMenuItem(locale.getString("GUI_Open"), iconOpen);
    openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
    openMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        FeedbackEnum returnVal = guiLogic.open();
        if (returnVal == FeedbackEnum.FAILED) {
          feedbackbarPanel.showFeedback(locale.getString("FB_OpenFailed"), FeedbackEnum.FAILED);
        }
      }
    });
    fileMenu.add(openMenuItem);

    fileMenu.add(new JSeparator());

    saveMenuItem = new JMenuItem(locale.getString("GUI_Save"), iconSave);
    saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    saveMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        FeedbackEnum returnVal = guiLogic.save();
        if (returnVal == FeedbackEnum.SUCCESSFUL) {
          feedbackbarPanel.showFeedback(locale.getString("FB_Save"), FeedbackEnum.SUCCESSFUL);
        } else if (returnVal == FeedbackEnum.FAILED) {
          feedbackbarPanel.showFeedback(locale.getString("FB_SaveFailed"), FeedbackEnum.FAILED);
        }
      }
    });
    fileMenu.add(saveMenuItem);

    JMenuItem saveAsMenuItem = new JMenuItem(locale.getString("GUI_SaveAs"), iconSave);
    saveAsMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        FeedbackEnum returnVal = guiLogic.saveAs();
        if (returnVal == FeedbackEnum.SUCCESSFUL) {
          feedbackbarPanel.showFeedback(locale.getString("FB_Save"), FeedbackEnum.SUCCESSFUL);
        } else if (returnVal == FeedbackEnum.FAILED) {
          feedbackbarPanel.showFeedback(locale.getString("FB_SaveFailed"), FeedbackEnum.FAILED);
        }
      }
    });
    fileMenu.add(saveAsMenuItem);

    fileMenu.add(new JSeparator());

    JMenuItem exportMenuItem = new JMenuItem(locale.getString("GUI_Export"), iconExport);
    exportMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        FeedbackEnum returnVal = guiLogic.export();
        if (returnVal == FeedbackEnum.SUCCESSFUL) {
          feedbackbarPanel.showFeedback(locale.getString("FB_Export"), FeedbackEnum.SUCCESSFUL);
        } else if (returnVal == FeedbackEnum.FAILED) {
          feedbackbarPanel.showFeedback(locale.getString("FB_ExportFailed"), FeedbackEnum.FAILED);
        }
      }
    });
    fileMenu.add(exportMenuItem);

    fileMenu.add(new JSeparator());

    JMenuItem exitMenuItem = new JMenuItem(locale.getString("GUI_Exit"), iconClose);
    exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
    exitMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        checkDirtyStateBeforeExiting();
      }
    });
    fileMenu.add(exitMenuItem);
  }

  private void createEditMenu(JMenuBar menuBar) {
    JMenu editMenu = new JMenu(locale.getString("GUI_Edit"));
    menuBar.add(editMenu);

    undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
    undoMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        guiLogic.undo();
      }
    });
    editMenu.add(undoMenuItem);

    redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
    redoMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        guiLogic.redo();
      }
    });
    editMenu.add(redoMenuItem);

    editMenu.add(new JSeparator());

    JMenuItem metaInfoMenuItem = new JMenuItem(locale.getString("MI_CustInfo"), iconMetaInfo);
    metaInfoMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        openInfoFrame();
      }
    });
    editMenu.add(metaInfoMenuItem);
  }

  private void openInfoFrame() {
    CustomerInfosFrame infoFrame = new CustomerInfosFrame(database);
    infoFrame.addObserver(this);
    infoFrame.setVisible(true);
  }

  private void createHelpMenu(JMenuBar menuBar) {
    JMenu helpMenu = new JMenu(locale.getString("GUI_Help"));
    menuBar.add(helpMenu);

    JMenuItem userGuideMenuItem = new JMenuItem(locale.getString("GUI_UserGuide"), iconHelp);
    userGuideMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OpenUrl.openURL("https://github.com/nexxx/Database-Analyzer/wiki/UserManual");
      }
    });
    userGuideMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    helpMenu.add(userGuideMenuItem);

    JMenuItem donateMenuItem = new JMenuItem(locale.getString("GUI_Donate"), iconDonate);
    donateMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OpenUrl.openURL("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=BJFK5BZUN89TG");
      }
    });
    helpMenu.add(donateMenuItem);

    JMenuItem gitHubMenuItem = new JMenuItem(locale.getString("GUI_Github"), iconGithib);
    gitHubMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OpenUrl.openURL("https://github.com/nexxx/Database-Analyzer");
      }
    });
    helpMenu.add(gitHubMenuItem);

    JMenuItem aboutMenuItem = new JMenuItem(locale.getString("GUI_About"), iconAbout);
    aboutMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        About about = new About();
        about.setVisible(true);
      }
    });
    helpMenu.add(aboutMenuItem);

  }


  private void createExtrasMenu(JMenuBar menuBar) {
    JMenu extrasMenu = new JMenu(locale.getString("GUI_Extras"));
    menuBar.add(extrasMenu);

    JMenuItem optionsMenuItem = new JMenuItem(locale.getString("GUI_Options"), iconOptions);
    optionsMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        optionsMenu optionsMenu = new optionsMenu();
        optionsMenu.setVisible(true);
      }
    });
    optionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
    extrasMenu.add(optionsMenuItem);
  }

  private void checkDirtyStateBeforeExiting() {
    int result = JOptionPane.NO_OPTION;
    if (TimeLine.getInstance().isDirty()) {
      Object[] options = {locale.getString("Yes"), locale.getString("No"), locale.getString("Cancel")};
      result = JOptionPane.showOptionDialog(frame, locale.getString("TREE_ExitMsg"), locale.getString("Confirm"),
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
    }
    switch (result) {
      case JOptionPane.YES_OPTION:
        saveMenuItem.doClick();
        System.exit(0);
        break;
      case JOptionPane.NO_OPTION:
        System.exit(0);
        break;
      case JOptionPane.CANCEL_OPTION:

      default:
        break;
    }
  }

  private void enableOptimizeButtons() {
    NormalForm currentNF = checker.getNF(dbTreePanel.getTree().getRelation(), new ArrayList<FunctionalDependency>());
    if (currentNF != NormalForm.SECOND && currentNF != NormalForm.THIRD && currentNF != NormalForm.BOYCECODD) {
      toolBarRelation.setEnabledOpti2NF(true);
    } else {
      toolBarRelation.setEnabledOpti2NF(false);
    }

    if (currentNF != NormalForm.THIRD && currentNF != NormalForm.BOYCECODD) {
      toolBarRelation.setEnabledOpti3NF(true);
    } else {
      toolBarRelation.setEnabledOpti3NF(false);
    }

    if (currentNF != NormalForm.BOYCECODD) {
      toolBarRelation.setEnabledOpti(true);
    } else {
      toolBarRelation.setEnabledOpti(false);
    }

    if (CustomTree.getInstance().getRelation().getAttributes().size() < 2) {
      toolBarRelation.setEnabledFD(false);
    } else {
      toolBarRelation.setEnabledFD(true);
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    if (arg instanceof Feedback) {
      Feedback feedback = (Feedback) arg;
      feedbackbarPanel.showFeedback(feedback.getText(), feedback.getFeedback());
    }
  }

  private void updateDBAfterChange() {
    database = (Database) TimeLine.getInstance().getCurrentElement();
    dbTreePanel.setDatabase(database);
    dbTreePanel.updateTree();

    relationView.display(database);
    relationDetailsView.display(database);

    if (guiLogic.getLastFileName() != null) {
      frame.setTitle(locale.getString("GUI_FrameTitle") + " - " +
        guiLogic.getLastFileName());
    } else {
      frame.setTitle(locale.getString("GUI_FrameTitle") + " - " +
        locale.getString("GUI_FrameTitleNotSaved"));
    }
  }
}
