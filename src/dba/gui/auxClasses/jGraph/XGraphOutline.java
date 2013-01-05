package dba.gui.auxClasses.jGraph;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.view.mxGraphView;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Wrapper for mxGraphOutline, enforces maximum and minimum ZoomFactors
 */
public class XGraphOutline extends mxGraphOutline {

  public XGraphOutline(mxGraphComponent component){
    super(component);
    super.removeMouseListener(tracker);
    super.removeMouseMotionListener(tracker);
    super.tracker=new XMouseTracker();
    super.addMouseMotionListener(tracker);
    super.addMouseListener(tracker);
  }

  /**
   * Wrapper for MouseTracker, constrains the reachable ZoomLevels
   */
  public class XMouseTracker extends MouseTracker {

    /*
	 *Calculates new ZoomFactor and Visible Area after the mouse is released
    */
    @Override
    public void mouseReleased(MouseEvent e)
    {
      if (start != null)
      {
        if (zoomGesture)
        {
          double dx = e.getX() - start.getX();
          double w = finderBounds.getWidth();

          final JScrollBar hs = graphComponent
            .getHorizontalScrollBar();
          final double sx;

          if (hs != null)
          {
            sx = (double) hs.getValue() / hs.getMaximum();
          }
          else
          {
            sx = 0;
          }

          final JScrollBar vs = graphComponent.getVerticalScrollBar();
          final double sy;

          if (vs != null)
          {
            sy = (double) vs.getValue() / vs.getMaximum();
          }
          else
          {
            sy = 0;
          }

          mxGraphView view = graphComponent.getGraph().getView();
          double scale = view.getScale();
          double newScale = scale - (dx * scale) / w;

          //Enforce minimum and maximum ZoomLevels
          if(newScale > 20){
            newScale = 20;
          }else if(newScale < 0.15){
            newScale = 0.15;
          }

          double factor = newScale / scale;
          view.setScale(newScale);

          if (hs != null)
          {
            hs.setValue((int) (sx * hs.getMaximum() * factor));
          }

          if (vs != null)
          {
            vs.setValue((int) (sy * vs.getMaximum() * factor));
          }
        }

        zoomGesture = false;
        start = null;
      }
    }
  }

}
