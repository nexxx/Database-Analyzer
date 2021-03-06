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


import dba.gui.auxClasses.GuiLogic;
import dba.gui.auxClasses.RelationDetailsView;
import dba.gui.auxClasses.RelationView;
import dba.gui.auxClasses.SearchPanel;
import dba.gui.auxClasses.feedback.FeedbackbarPanel;
import dba.gui.auxClasses.jGraph.JGraphView;
import dba.gui.auxClasses.navBarPanels.*;
import dba.gui.auxClasses.toolBars.*;
import dba.gui.metaInfoFrame.CustomerInfosFrame;
import dba.init.Initialize;
import dba.options.Feedback;
import dba.options.FeedbackEnum;
import dba.options.Options;
import dba.utils.GetIcons;
import dba.utils.Localization;
import dba.utils.OpenUrl;
import dba.utils.constants;
import dbaCore.data.*;
import dbaCore.data.events.Change;
import dbaCore.data.events.ChangeListener;
import dbaCore.data.events.Time;
import dbaCore.logic.Analysis.GeneralRelationCheck;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
  private ImageIcon iconImport;
  private ImageIcon iconSave;
  private ImageIcon iconExport;
  private ImageIcon iconClose;
  private ImageIcon iconMetaInfo;
  private ImageIcon iconOptions;
  private ImageIcon iconAbout;
  private ImageIcon iconHelp;
  private ImageIcon iconDonate;
  private ImageIcon iconGithib;
  private ImageIcon iconSearch;
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
  private JSplitPane splitPane;
  private Dimension minimumSizeSplitPane;
  private int lastSplitPaneLocation;
  private int splitPaneDividerSize;
  private JTabbedPane tabbedPaneOutline;
  private JCheckBoxMenuItem inspectMenuItem;
  private JCheckBoxMenuItem outlineMenuItem;
  private Options options;
  private OutlinePanel pnlOutline;
  private InspectPanel pnlInspect;
  private JPanel pnlSearch;
  private JPanel pnlLeft;
  private static Logger logger =  Logger.getLogger(MainWindow.class.getName());



  /**
   * Create the frame.
   */
  public MainWindow() {
    super();

    logger.log(Level.INFO, "Starting DBA");

    options = Options.getInstance();
    checker = new GeneralRelationCheck();
    GetIcons getIcon = GetIcons.getInstance();
    ImageIcon iconFrame = getIcon.getIconFrame();
    iconNew = getIcon.getMenuNew();
    iconOpen = getIcon.getMenuOpen();
    iconImport = getIcon.getMenuImport();
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
    iconSearch = getIcon.getMenuSearch();

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
    guiLogic.addObserver(this);
    relationView = new RelationView(guiLogic);
    relationDetailsView = new RelationDetailsView(guiLogic);

    showWelcomeScreen();

    JTabbedPane displayTab = new JTabbedPane(SwingConstants.TOP);

    pnlToolBar = new JPanel(new BorderLayout());
    toolBar = new ToolBar(relationView, relationDetailsView, guiLogic);

    pnlToolBar.add(toolBar, BorderLayout.CENTER);
    contentPane.add(pnlToolBar, BorderLayout.PAGE_START);

    toolBarDatabase = new ToolBarDatabase(dbTreePanel, relationView, relationDetailsView, guiLogic);
    toolBarRelation = new ToolBarRelation(dbTreePanel, relationView, relationDetailsView, guiLogic);
    toolBarAttribute = new ToolBarAttribute(relationView, relationDetailsView, guiLogic);
    toolBarFd = new ToolBarFd(dbTreePanel, relationView, relationDetailsView, guiLogic);

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

    TimeLine.getInstance().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("isDirty")) {
          updateFrameTitle();
        }
      }
    });

    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setSize(1024, 600);
    frame.setMinimumSize(new Dimension(800, 480));
    frame.setLocationRelativeTo(null);

    Initialize init = Initialize.getInstance();
    init.init();
    logger.log(Level.INFO, "Initialization finished");

    minimumSizeSplitPane = new Dimension(100, 50);

    dbTreePanel.addPropertyChangeListener(new ToolbarChangeListener());
    // relationView.addPropertyChangeListener(changeListener);

    JPanel pnlRight = new JPanel(new BorderLayout());
    pnlRight.setMinimumSize(minimumSizeSplitPane);
    pnlRight.add(relationView, BorderLayout.CENTER);
    pnlRight.add(feedbackbarPanel, BorderLayout.SOUTH);

    // RelationView
    displayTab.addTab(locale.getString("Relations"), relationView);

    // RelationDetails
    displayTab.addTab(locale.getString("GUI_RelationDetails"), relationDetailsView);

    displayTab.addChangeListener(new javax.swing.event.ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        JTabbedPane tabPane = (JTabbedPane) changeEvent.getSource();

        //Disable zooming for all tabs
        for (int i = 0; i < tabPane.getTabCount(); i++) {
          if (tabPane.getComponentAt(i) instanceof JGraphView) {
            ((JGraphView) tabPane.getComponentAt(i)).setZoomEnabled(false);
          }
        }

        //Enable zooming for the selected tab
        Object obj = tabPane.getSelectedComponent();
        if (obj instanceof JGraphView) {
          JGraphView view = (JGraphView) obj;
          view.setZoomEnabled(true);
          updateZoomFactors(view);
          pnlOutline.setContent(view.getGraphComponent());
        }
      }
    });

    //initial disabling of relationView that is not visible
    relationDetailsView.setZoomEnabled(false);

    pnlRight = new JPanel(new BorderLayout());
    pnlRight.setMinimumSize(minimumSizeSplitPane);
    pnlRight.add(displayTab, BorderLayout.CENTER);
    pnlRight.add(feedbackbarPanel, BorderLayout.SOUTH);

    pnlLeft = new JPanel(new BorderLayout());

    pnlSearch = new SearchPanel();
    ((SearchPanel)pnlSearch).addObserver(this);


    tabbedPaneOutline = new JTabbedPane();
    pnlLeft.add(tabbedPaneOutline, BorderLayout.CENTER);

    pnlInspect = new InspectPanel();
    pnlOutline = new OutlinePanel(relationView.getGraphComponent());

    updateNavTabs();

    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, pnlRight);
    splitPane.setOneTouchExpandable(true);
    splitPaneDividerSize = 5;
    splitPane.setDividerSize(splitPaneDividerSize);
    splitPane.setDividerLocation(250);

    dbTreePanel.setMinimumSize(minimumSizeSplitPane);
    frame.add(splitPane, BorderLayout.CENTER);

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    frame.setDropTarget(new DropTarget() {
      @Override
      public synchronized void drop(DropTargetDropEvent evt) {
        try {
          evt.acceptDrop(DnDConstants.ACTION_COPY);
          List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

          String path = droppedFiles.get(0).getCanonicalPath();
          if(path.endsWith(".xml")) {
            FeedbackEnum returnVal = guiLogic.openFromPath(path);
            if (returnVal == FeedbackEnum.FAILED) {
              feedbackbarPanel.showFeedback(locale.getString("FB_OpenFailed"), FeedbackEnum.FAILED);
            }
          } else {
            feedbackbarPanel.showFeedback(locale.getString("FB_OpenFailed"), FeedbackEnum.FAILED);
          }

        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    createFileMenu(menuBar);
    createEditMenu(menuBar);
    createViewMenu(menuBar);
    createExtrasMenu(menuBar);
    createHelpMenu(menuBar);

    dbTreePanel.getTree().setSelectedItem(0);
    logger.log(Level.INFO, "Started GUI");

  }


  private void updateZoomFactors(JGraphView view) {
    toolBarAttribute.updateZoom(view);
    toolBarFd.updateZoom(view);
    toolBarRelation.updateZoom(view);
    toolBarDatabase.updateZoom(view);
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
    logger.log(Level.INFO, "Creating File Menu bar NOW");
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

    JMenuItem importMenuItem = new JMenuItem(locale.getString("Import"), iconImport);
    importMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        guiLogic.importDb();
      }
    });
    fileMenu.add(importMenuItem);

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

    JMenuItem findMenuItem = new JMenuItem(locale.getString("GUI_Search"), iconSearch);
    findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
    findMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (splitPane.getDividerLocation() < minimumSizeSplitPane.width) {
          BasicSplitPaneUI ui = (BasicSplitPaneUI)splitPane.getUI();
          JButton oneClick = (JButton)ui.getDivider().getComponent(1);
          oneClick.doClick();
        }
        pnlLeft.add(pnlSearch, BorderLayout.SOUTH);
        pnlLeft.revalidate();
      }
    });
    editMenu.add(findMenuItem);

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

  private void createViewMenu(JMenuBar menuBar) {
    JMenu viewMenu = new JMenu(locale.getString("GUI_View"));
    menuBar.add(viewMenu);

    JMenu navTabMenu = new JMenu(locale.getString("GUI_NavTabs"));
    viewMenu.add(navTabMenu);


    inspectMenuItem = new JCheckBoxMenuItem(locale.getString("GUI_Inspect"), options.getShowTabInspect());
    inspectMenuItem.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent itemEvent) {
        options.setShowTabInspect(inspectMenuItem.isSelected());
        options.writeOptions();
        updateNavTabs();
      }
    });
    navTabMenu.add(inspectMenuItem);

    outlineMenuItem = new JCheckBoxMenuItem(locale.getString("GUI_Outline"), options.getShowTabOutline());
    outlineMenuItem.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent itemEvent) {
        options.setShowTabOutline(outlineMenuItem.isSelected());
        options.writeOptions();
        updateNavTabs();
      }
    });
    navTabMenu.add(outlineMenuItem);
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

        optionsMenu optionsMenu = new optionsMenu(frame);
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


  private void updateFrameTitle() {
    if (guiLogic.getLastFileName() != null) {
      frame.setTitle(locale.getString("GUI_FrameTitle") + " - " +
        guiLogic.getLastFileName());
    } else {
      frame.setTitle(locale.getString("GUI_FrameTitle") + " - " +
        locale.getString("GUI_FrameTitleNotSaved"));
    }
    if (TimeLine.getInstance().isDirty()) {
      frame.setTitle(frame.getTitle() + "*");
    }
  }

  private void updateDBAfterChange() {
    database = (Database) TimeLine.getInstance().getCurrentElement();
    dbTreePanel.setDatabase(database);
    dbTreePanel.updateTree();

    updateFrameTitle();

    relationView.display(database);
    relationDetailsView.display(database);

    if (pnlInspect != null) {
      pnlInspect.updateScrollpane();
    }
  }

  @Override
  public void update(Observable observable, Object o) {
    if (observable instanceof GuiLogic) {
      updateFrameTitle();
    } else if (o instanceof Feedback) {
      Feedback feedback = (Feedback) o;
      feedbackbarPanel.showFeedback(feedback.getText(), feedback.getFeedback());
    } else if(o instanceof SearchPanel) {
      pnlLeft.remove(pnlSearch);              //Close Search Panel
      pnlLeft.revalidate();
    }
  }

  /**
   * Remove all tabs and add those tabs which are set to true in options
   * Nav Tree is always shown
   */
  private void updateNavTabs() {
    tabbedPaneOutline.removeAll();

    tabbedPaneOutline.addTab(locale.getString("GUI_Tree"), dbTreePanel);

    if (options.getShowTabInspect()) {
      tabbedPaneOutline.addTab(locale.getString("GUI_Inspect"), pnlInspect);
    }
    if (options.getShowTabOutline()) {
      tabbedPaneOutline.addTab(locale.getString("GUI_Outline"), pnlOutline);
    }
  }

  private void showWelcomeScreen() {
    WelcomeScreen wcs = new WelcomeScreen(guiLogic, database);
    FeedbackEnum retVal;
    do {
      wcs.showScreen();
      retVal = wcs.getRetVal();
    } while (retVal != FeedbackEnum.SUCCESSFUL);

    if (wcs.openClicked()) {
      updateDBAfterChange();
    }
  }

  class ToolbarChangeListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {


        }
      });

      if (evt.getPropertyName().equalsIgnoreCase("TreeClick")) {
        if (((String) evt.getNewValue()).equalsIgnoreCase("Database")) {
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              pnlToolBar.removeAll();
              toolBarDatabase.setEnabledInspect(!CustomTree.getInstance().getDatabase().getDatabase().isEmpty());
              toolBarDatabase.updateDatatype();
              pnlToolBar.add(toolBarDatabase, BorderLayout.CENTER);
            }
          });
        } else if (((String) evt.getNewValue()).equalsIgnoreCase("Relation")) {
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              pnlToolBar.removeAll();
              enableOptimizeButtons();
              pnlToolBar.add(toolBarRelation, BorderLayout.CENTER);
            }
          });
        } else if (((String) evt.getNewValue()).equalsIgnoreCase("Attribute")) {
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              pnlToolBar.removeAll();
              toolBarAttribute.updateElements();
              pnlToolBar.add(toolBarAttribute, BorderLayout.CENTER);
            }
          });
        } else if (((String) evt.getNewValue()).equalsIgnoreCase("FD")) {
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              pnlToolBar.removeAll();
              pnlToolBar.add(toolBarFd, BorderLayout.CENTER);
            }
          });
        } else {
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              pnlToolBar.removeAll();
              pnlToolBar.add(toolBar, BorderLayout.CENTER);
            }
          });
        }
      }
      SwingUtilities.updateComponentTreeUI(pnlToolBar);

    }
  }
}
