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

package dba.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Class (singelton) which provides all icons. All icons are loaded
 * only once and can be accessed via the getter
 *
 * @author Andreas Freitag
 */
public class GetIcons implements constants {
  private static GetIcons instance = null;
  private ImageIcon treeDatabase;
  private ImageIcon treeRelation;
  private ImageIcon treeAttribute;
  private ImageIcon treeFd;
  private ImageIcon menuNew;
  private ImageIcon menuOpen;
  private ImageIcon menuSave;
  private ImageIcon menuExport;
  private ImageIcon menuImport;
  private ImageIcon menuClose;
  private ImageIcon iconFrame;
  private ImageIcon iconInspectFrame;
  private ImageIcon iconWizardFrame;
  private ImageIcon iconFDWizardFrame;
  private ImageIcon iconOptimizeFrame;
  private ImageIcon iconEditInfosFrame;
  private ImageIcon menuOptions;
  private ImageIcon iconTipFrame;
  private ImageIcon iconAboutFrame;
  private ImageIcon iconOptionsFrame;
  private ImageIcon menuUndo;
  private ImageIcon menuRedo;
  private ImageIcon menuEditInfos;
  private ImageIcon menuAbout;
  private ImageIcon menuHelp;
  private ImageIcon menuDonate;
  private ImageIcon menuGithub;
  private ImageIcon popupDelete;
  private ImageIcon popupRename;
  private ImageIcon popupPK;
  private ImageIcon popupFK;
  private ImageIcon popupWizard;
  private ImageIcon popupRelation;
  private ImageIcon popupInspect;
  private ImageIcon popupOptimize2NF;
  private ImageIcon popupOptimize3NF;
  private ImageIcon popupOptimizeBCNF;
  private ImageIcon popupEdit;
  private ImageIcon popupAttribute;
  private ImageIcon fdArrow;
  private ImageIcon tipsIcon;
  private ImageIcon buttonFinish;
  private ImageIcon buttonCancel;
  private ImageIcon buttonAdd;
  private ImageIcon buttonEdit;
  private ImageIcon buttonDelete;
  private ImageIcon buttonAttribute;
  private ImageIcon buttonFd;
  private ImageIcon tbNew;
  private ImageIcon tbOpen;
  private ImageIcon tbSave;
  private ImageIcon tbUndo;
  private ImageIcon tbRedo;
  private ImageIcon tbDelete;
  private ImageIcon tbRename;
  private ImageIcon tbPK;
  private ImageIcon tbFK;
  private ImageIcon tbWizard;
  private ImageIcon tbRelation;
  private ImageIcon tbInspect;
  private ImageIcon tbEdit;
  private ImageIcon tbAttribute;
  private ImageIcon tbFd;
  private ImageIcon tb2NF;
  private ImageIcon tb3NF;
  private ImageIcon tbBCNF;
  private ImageIcon dbaLogo;
  private ImageIcon next;
  private ImageIcon prev;


  private GetIcons() {
    super();
    treeDatabase = getIcon("database.png", constants.treeIconSize);
    treeRelation = getIcon("relation.png", constants.treeIconSize);
    treeAttribute = getIcon("attribute.png", constants.treeIconSize);
    treeFd = getIcon("fd.png", constants.treeIconSize);
    menuNew = getIcon("new.png", constants.popupIconSize);
    menuOpen = getIcon("open.png", constants.popupIconSize);
    menuSave = getIcon("save.png", constants.popupIconSize);
    menuExport = getIcon("export.png", constants.popupIconSize);
    menuImport = getIcon("import.png", constants.popupIconSize);
    menuClose = getIcon("close.png", constants.popupIconSize);
    iconFrame = getIcon("dba.png", 32);
    iconInspectFrame = getIcon("inspect.png", 32);
    iconWizardFrame = getIcon("newWizard.png", 32);
    iconFDWizardFrame = getIcon("fd.png", 32);
    iconOptimizeFrame = getIcon("optimize.png", 32);
    menuOptions = getIcon("options.png", popupIconSize);
    iconTipFrame = getIcon("tip.png", 32);
    iconAboutFrame = getIcon("about.png", 32);
    iconEditInfosFrame = getIcon("editInfos.png", 32);
    menuUndo = getIcon("undo.png", constants.popupIconSize);
    menuRedo = getIcon("redo.png", constants.popupIconSize);
    menuEditInfos = getIcon("editInfos.png", popupIconSize);
    menuAbout = getIcon("about.png", constants.popupIconSize);
    menuHelp = getIcon("help.png", constants.popupIconSize);
    menuDonate = getIcon("donate.png", constants.popupIconSize);
    menuGithub = getIcon("github.png", constants.popupIconSize);
    popupDelete = getIcon("delete.png", constants.popupIconSize);
    popupRename = getIcon("rename.png", constants.popupIconSize);
    popupPK = getIcon("pk.png", constants.popupIconSize);
    popupFK = getIcon("fk.png", constants.popupIconSize);
    popupWizard = getIcon("newWizard.png", constants.popupIconSize);
    popupRelation = getIcon("relation.png", constants.popupIconSize);
    popupInspect = getIcon("inspect.png", constants.popupIconSize);
    popupOptimize2NF = getIcon("2nf.png", popupIconSize);
    popupOptimize3NF = getIcon("3nf.png", popupIconSize);
    popupOptimizeBCNF = getIcon("bcnf.png", popupIconSize);
    popupEdit = getIcon("edit.png", constants.popupIconSize);
    popupAttribute = getIcon("attribute.png", constants.popupIconSize);
    fdArrow = getIcon("fdArrow.png", constants.fdArrowSize);
    tipsIcon = getIcon("tip.png", 32);
    buttonFinish = getIcon("finish.png", constants.buttonIconSize);
    buttonCancel = getIcon("cancel.png", constants.buttonIconSize);
    buttonAdd = getIcon("add.png", constants.buttonIconSize);
    buttonEdit = getIcon("edit.png", constants.buttonIconSize);
    buttonDelete = getIcon("delete.png", constants.buttonIconSize);
    buttonAttribute = getIcon("attribute.png", constants.buttonIconSize);
    buttonFd = getIcon("fd.png", constants.buttonIconSize);
    tbNew = getIcon("new.png", constants.toolBarIconSize);
    tbOpen = getIcon("open.png", constants.toolBarIconSize);
    tbSave = getIcon("save.png", constants.toolBarIconSize);
    tbUndo = getIcon("undo.png", constants.toolBarIconSize);
    tbRedo = getIcon("redo.png", constants.toolBarIconSize);
    tbDelete = getIcon("delete.png", constants.toolBarIconSize);
    tbRename = getIcon("rename.png", constants.toolBarIconSize);
    tbPK = getIcon("pk.png", constants.toolBarIconSize);
    tbFK = getIcon("fk.png", constants.toolBarIconSize);
    tbWizard = getIcon("newWizard.png", constants.toolBarIconSize);
    tbRelation = getIcon("relation.png", constants.toolBarIconSize);
    tbInspect = getIcon("inspect.png", constants.toolBarIconSize);
    tbEdit = getIcon("edit.png", constants.toolBarIconSize);
    tbAttribute = getIcon("attribute.png", constants.toolBarIconSize);
    tbFd = getIcon("fd.png", constants.toolBarIconSize);
    tb2NF = getIcon("2nf.png", toolBarIconSize);
    tb3NF = getIcon("3nf.png", toolBarIconSize);
    tbBCNF = getIcon("bcnf.png", toolBarIconSize);
    iconOptionsFrame = getIcon("options.png", 32);
    dbaLogo = getIcon("dba.png", 300);
    next = getIcon("next.png", 16);
    prev = getIcon("previous.png", 16);
  }

  /**
   * Getter for the icons
   *
   * @param iconName Iconname
   * @param size     Size of the icon - eg. 24 for 24x24 pixels
   * @return IconImage or null if icon does not exist
   */
  private ImageIcon getIcon(String iconName, int size) {
    URL imgURL = getClass().getResource("/res/icons/" + iconName);
    if (imgURL != null) {
      ImageIcon icon = new ImageIcon(imgURL);
      Image image = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
      return new ImageIcon(image);
    } else {
      System.err.println("Couldn't find icon: " + iconName);
      return null;
    }
  }

  /**
   * Getter for the singelton geticons (thread-safe)
   */
  public synchronized static GetIcons getInstance() {
    if (instance == null) {
      synchronized (GetIcons.class) {
        instance = new GetIcons();
      }
    }
    return instance;
  }

  /**
   * @return the treeDatabase
   */
  public ImageIcon getTreeDatabase() {
    return treeDatabase;
  }

  /**
   * @return the treeRelation
   */
  public ImageIcon getTreeRelation() {
    return treeRelation;
  }

  /**
   * @return the treeAttribute
   */
  public ImageIcon getTreeAttribute() {
    return treeAttribute;
  }

  /**
   * @return the treeFd
   */
  public ImageIcon getTreeFd() {
    return treeFd;
  }

  /**
   * @return the menuNew
   */
  public ImageIcon getMenuNew() {
    return menuNew;
  }

  /**
   * @return the menuOpen
   */
  public ImageIcon getMenuOpen() {
    return menuOpen;
  }

  /**
   * @return the menuSave
   */
  public ImageIcon getMenuSave() {
    return menuSave;
  }

  /**
   * @return the menuClose
   */
  public ImageIcon getMenuClose() {
    return menuClose;
  }

  /**
   * @return the iconFrame
   */
  public ImageIcon getIconFrame() {
    return iconFrame;
  }

  /**
   * @return the menuUndo
   */
  public ImageIcon getMenuUndo() {
    return menuUndo;
  }

  /**
   * @return the menuRedo
   */
  public ImageIcon getMenuRedo() {
    return menuRedo;
  }

  /**
   * @return the menuAbout
   */
  public ImageIcon getMenuAbout() {
    return menuAbout;
  }

  /**
   * @return the menuHelp
   */
  public ImageIcon getMenuHelp() {
    return menuHelp;
  }

  /**
   * @return the popupDelete
   */
  public ImageIcon getPopupDelete() {
    return popupDelete;
  }

  /**
   * @return the popupRename
   */
  public ImageIcon getPopupRename() {
    return popupRename;
  }

  /**
   * @return the popupPK
   */
  public ImageIcon getPopupPK() {
    return popupPK;
  }

  /**
   * @return the popupFK
   */
  public ImageIcon getPopupFK() {
    return popupFK;
  }

  /**
   * @return the popupWizard
   */
  public ImageIcon getPopupWizard() {
    return popupWizard;
  }

  /**
   * @return the popupRelation
   */
  public ImageIcon getPopupRelation() {
    return popupRelation;
  }

  /**
   * @return the popupInspect
   */
  public ImageIcon getPopupInspect() {
    return popupInspect;
  }

  /**
   * @return the popupEdit
   */
  public ImageIcon getPopupEdit() {
    return popupEdit;
  }

  /**
   * @return the popupAttribute
   */
  public ImageIcon getPopupAttribute() {
    return popupAttribute;
  }

  /**
   * @return the buttonFdArrow
   */
  public ImageIcon getFdArrow() {
    return fdArrow;
  }

  /**
   * @return the buttonFinish
   */
  public ImageIcon getButtonFinish() {
    return buttonFinish;
  }

  /**
   * @return the buttonCancel
   */
  public ImageIcon getButtonCancel() {
    return buttonCancel;
  }

  /**
   * @return the buttonAdd
   */
  public ImageIcon getButtonAdd() {
    return buttonAdd;
  }

  /**
   * @return the buttonEdit
   */
  public ImageIcon getButtonEdit() {
    return buttonEdit;
  }

  /**
   * @return the buttonDelete
   */
  public ImageIcon getButtonDelete() {
    return buttonDelete;
  }

  /**
   * @return the buttonAttribute
   */
  public ImageIcon getButtonAttribute() {
    return buttonAttribute;
  }

  /**
   * @return the buttonFd
   */
  public ImageIcon getButtonFd() {
    return buttonFd;
  }

  /**
   * @return the iconInspectFrame
   */
  public ImageIcon getIconInspectFrame() {
    return iconInspectFrame;
  }

  /**
   * @return the iconWizardFrame
   */
  public ImageIcon getIconWizardFrame() {
    return iconWizardFrame;
  }

  /**
   * @return the iconFDWizardFrame
   */
  public ImageIcon getIconFDWizardFrame() {
    return iconFDWizardFrame;
  }

  /**
   * @return the iconOptimizeFrame
   */
  public ImageIcon getIconOptimizeFrame() {
    return iconOptimizeFrame;
  }

  /**
   * @return the iconTipFrame
   */
  public ImageIcon getIconTipFrame() {
    return iconTipFrame;
  }

  /**
   * @return the infoIcon
   */
  public ImageIcon getInfoIcon() {
    return tipsIcon;
  }

  /**
   * @return the iconAboutFrame
   */
  public ImageIcon getIconAboutFrame() {
    return iconAboutFrame;
  }

  /**
   * @return the tbNew
   */
  public ImageIcon getTbNew() {
    return tbNew;
  }

  /**
   * @return the tbOpen
   */
  public ImageIcon getTbOpen() {
    return tbOpen;
  }

  /**
   * @return the tbSave
   */
  public ImageIcon getTbSave() {
    return tbSave;
  }

  /**
   * @return the tbUndo
   */
  public ImageIcon getTbUndo() {
    return tbUndo;
  }

  /**
   * @return the tbRedo
   */
  public ImageIcon getTbRedo() {
    return tbRedo;
  }

  /**
   * @return the tbDelete
   */
  public ImageIcon getTbDelete() {
    return tbDelete;
  }

  /**
   * @return the tbRename
   */
  public ImageIcon getTbRename() {
    return tbRename;
  }

  /**
   * @return the tbPK
   */
  public ImageIcon getTbPK() {
    return tbPK;
  }

  /**
   * @return the tbFK
   */
  public ImageIcon getTbFK() {
    return tbFK;
  }

  /**
   * @return the tbWizard
   */
  public ImageIcon getTbWizard() {
    return tbWizard;
  }

  /**
   * @return the tbRelation
   */
  public ImageIcon getTbRelation() {
    return tbRelation;
  }

  /**
   * @return the tbInspect
   */
  public ImageIcon getTbInspect() {
    return tbInspect;
  }

  /**
   * @return the tbEdit
   */
  public ImageIcon getTbEdit() {
    return tbEdit;
  }

  /**
   * @return the tbAttribute
   */
  public ImageIcon getTbAttribute() {
    return tbAttribute;
  }

  /**
   * @return the tbFd
   */
  public ImageIcon getTbFd() {
    return tbFd;
  }

  /**
   * @return the tb2NF
   */
  public ImageIcon getTb2NF() {
    return tb2NF;
  }

  /**
   * @return the tb3NF
   */
  public ImageIcon getTb3NF() {
    return tb3NF;
  }

  /**
   * @return the tbBCNF
   */
  public ImageIcon getTbBCNF() {
    return tbBCNF;
  }

  /**
   * @return the popupOptimize2NF
   */
  public ImageIcon getPopupOptimize2NF() {
    return popupOptimize2NF;
  }

  /**
   * @return the popupOptimize3NF
   */
  public ImageIcon getPopupOptimize3NF() {
    return popupOptimize3NF;
  }

  /**
   * @return the popupOptimizeBCNF
   */
  public ImageIcon getPopupOptimizeBCNF() {
    return popupOptimizeBCNF;
  }

  /**
   * @return the menuOptions
   */
  public ImageIcon getMenuOptions() {
    return menuOptions;
  }

  /**
   * @return the iconOptionsFrame
   */
  public ImageIcon getIconOptionsFrame() {
    return iconOptionsFrame;
  }

  /**
   * @return the menuExport
   */
  public ImageIcon getMenuExport() {
    return menuExport;
  }

  /**
   * @return the iconEditInfosFrame
   */
  public ImageIcon getIconEditInfosFrame() {
    return iconEditInfosFrame;
  }

  /**
   * @return the menuEditInfos
   */
  public ImageIcon getMenuEditInfos() {
    return menuEditInfos;
  }

  /**
   * @return the dbaLogo
   */
  public ImageIcon getDbaLogo() {
    return dbaLogo;
  }

  /**
   * @return the menuDonate
   */
  public ImageIcon getMenuDonate() {
    return menuDonate;
  }

  /**
   * @return the gitHub
   */
  public ImageIcon getMenuGithub() {
    return menuGithub;
  }

  /**
   * @return the MenuImport
   */
  public ImageIcon getMenuImport() {
    return menuImport;
  }

  /**
   * @return the next icon
   */
  public ImageIcon getNext() {
    return next;
  }

  /**
   * @return get previous icon
   */
  public ImageIcon getPrev() {
    return prev;
  }
}
