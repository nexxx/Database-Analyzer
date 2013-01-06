package dba.gui.auxClasses.navBarPanels;

import com.mxgraph.swing.mxGraphComponent;
import dba.gui.auxClasses.jGraph.XGraphOutline;

import javax.swing.*;
import java.awt.*;

/**
 * Displays a Outline-View of a given mxGraphComponent
 *
 * @author theuers
 */
public class OutlinePanel extends JPanel {
  private XGraphOutline outline;

  public OutlinePanel(mxGraphComponent content) {
    super();
    this.setLayout(new BorderLayout());
    outline = new XGraphOutline(content);
    outline.setDrawLabels(true);
    outline.setTripleBuffered(false);
    this.add(outline);
  }

  /**
   * Sets the mxGraphComponent to display
   *
   * @param content the content which should be displayed
   */
  public void setContent(mxGraphComponent content) {
    outline.setGraphComponent(content);
    outline.updateFinder(true);
    outline.repaint();
  }
}
