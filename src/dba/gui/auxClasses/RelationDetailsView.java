package dba.gui.auxClasses;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import data.Database;
import data.RelationSchema;
import dba.gui.auxClasses.jGraph.JGraphView;
import dba.gui.auxClasses.jGraph.RelationDetailsGraphUpdater;
import dba.gui.auxClasses.jGraph.XGraph;

/**
 * Displays the relations and their functional dependencies
 */
public class RelationDetailsView extends JGraphView implements Observer {
  /**
	 * 
	 */
  private static final long serialVersionUID = -6169070587706507110L;
  final static String ATTRIBUTECOLOR = "#00FF00";
  final static String BACKGROUNDCOLOR = "#A7E2FF";

  private mxCell selectedCell;
  private mxGraph graph;
  private mxGraphComponent graphComponent;

  /**
   * Creates a new instance and registers this instance as a observer
   * of given GuiLogic
   * 
   * @param logic
   *          the GUiLogic to observe
   */
  public RelationDetailsView(GuiLogic logic) {
	super();

	logic.addObserver(this);
	initGraphics();
  }

  private void initGraphics() {
	setLayout(new BorderLayout());

	graph = new XGraph();
	super.setGraph(graph);

	graph.getModel().beginUpdate();
	try {
	  // change Style
	  initStyle();
	} finally {
	  graph.getModel().endUpdate();
	}

	// Configure Graph
	graph.setCellsEditable(false);
	graph.setDropEnabled(false);

	graphComponent = new mxGraphComponent(graph);
	graphComponent.getViewport().setBackground(Color.decode(BACKGROUNDCOLOR));

	// Disable the user's ability to draw own connections
	graphComponent.setConnectable(false);

	graphComponent.addMouseWheelListener(new MouseWheelListener() {
	  @Override
	  public void mouseWheelMoved(MouseWheelEvent event) {
		double scale = graph.getView().getScale();

		// Only scroll when Control is pressed
		if (!event.isControlDown()) {
		  return;
		}

		if (event.getWheelRotation() < 0) {
		  if (scale < 20) {
			graphComponent.zoomIn();
		  }
		} else {
		  if (scale > 0.1) {
			graphComponent.zoomOut();
		  }
		}

		notifyObservers();

	  }
	});

	graph.getSelectionModel().addListener(mxEvent.CHANGE,
	    new mxIEventListener() {

		  @Override
		  public void invoke(Object arg0, mxEventObject arg1) {
		    selectedCell = (mxCell) graph.getSelectionCell();

		    if (selectedCell != null) {
			  CustomTree.getInstance().setSelectedNode(selectedCell.getValue());
		    } else {
			  CustomTree.getInstance().setSelectedItem(0);
		    }
		  }
	    });

	add(graphComponent, BorderLayout.CENTER);

  }

  /**
   * Displays all relations of the given database
   * 
   * @param database
   *          the database to display
   */
  public void display(Database database) {
	display(database.getDatabase());
  }

  /**
   * Displays all given Relations
   */
  public void display(ArrayList<RelationSchema> relations) {

	RelationDetailsGraphUpdater updater = new RelationDetailsGraphUpdater(
	    graph, relations);
	updater.run();
  }

  /**
   * Zooms to the given percentage
   * 
   * @param factor
   *          the zoomFactor e.g. 100, 50%
   */
  public void zoom(String factor) {
	factor = factor.replace("%", "");

	Double newScale = Double.parseDouble(factor);
	if (newScale != null) {
	  newScale /= 100;
	  if (newScale != graph.getView().getScale()) {
		graphComponent.zoomTo(newScale, false);
		notifyObservers();
	  }

	}
  }

  /**
   * Exports the current graph to Png
   * 
   * @param path
   *          the path + the fileName
   */
  public void exportToPng(String path) {
	Dimension d = graphComponent.getGraphControl().getSize();
	BufferedImage image = new BufferedImage(d.width, d.height,
	    BufferedImage.TYPE_INT_ARGB);
	Graphics2D g = image.createGraphics();
	graphComponent.getGraphControl().paint(g);
	final File outputfile = new File(path.replace(".png", "_export_fd.png"));
	try {
	  ImageIO.write(image, "png", outputfile);
	} catch (IOException e) {
	  e.printStackTrace();
	}
  }

  /**
   * Adds the Styles for Attributes and Relations
   */
  @SuppressWarnings("unchecked")
  private void initStyle() {
	mxStylesheet stylesheet = graph.getStylesheet();

	// Define Style for Attribute with no Key
	Hashtable<String, Object> style = new Hashtable<>();
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_LABEL);
	style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
	style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	style.put(mxConstants.STYLE_AUTOSIZE, true);
	style.put(mxConstants.STYLE_FILLCOLOR, ATTRIBUTECOLOR);
	style.put(mxConstants.STYLE_FONTCOLOR, "black");
	style.put(mxConstants.STYLE_FONTSIZE, 15);
	style.put(mxConstants.STYLE_MOVABLE, false);
	style.put(mxConstants.STYLE_EDITABLE, false);
	style.put(mxConstants.STYLE_RESIZABLE, false);
	style.put(mxConstants.STYLE_IMAGE_HEIGHT, 32);
	style.put(mxConstants.STYLE_IMAGE_WIDTH, 32);
	style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_nokey.png");
	style.put(mxConstants.STYLE_STROKEWIDTH, 1);

	stylesheet.putCellStyle("ATTRIBUTE_NOKEY", style);

	// Define Style for Primary-Key Attribute
	style = (Hashtable<String, Object>) style.clone();
	style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pk.png");
	stylesheet.putCellStyle("ATTRIBUTE_PK", style);

	// Define Style for Foreign-Key Attribute
	style = (Hashtable<String, Object>) style.clone();
	style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_fk.png");
	stylesheet.putCellStyle("ATTRIBUTE_FK", style);

	// Define Style for Primary-Key/Foreign-Key Attribute
	style = (Hashtable<String, Object>) style.clone();
	style.put(mxConstants.STYLE_IMAGE, "/res/icons/graph_pkfk.png");
	stylesheet.putCellStyle("ATTRIBUTE_PKFK", style);

	// Define Style for Relation-Header
	style = new Hashtable<>();
	style.put(mxConstants.STYLE_OPACITY, 0);
	style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	style.put(mxConstants.STYLE_FONTCOLOR, "black");
	style.put(mxConstants.STYLE_MOVABLE, false);
	style.put(mxConstants.STYLE_EDITABLE, false);
	style.put(mxConstants.STYLE_RESIZABLE, false);
	style.put(mxConstants.STYLE_FONTSIZE, 20);
	style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
	stylesheet.putCellStyle("RELATION_HEADER", style);

	// Define Style for FD-Nodes
	style = new Hashtable<>();
	style.put(mxConstants.STYLE_MOVABLE, false);
	style.put(mxConstants.STYLE_EDITABLE, false);
	style.put(mxConstants.STYLE_RESIZABLE, false);
	style.put(mxConstants.STYLE_OPACITY, 0);
	stylesheet.putCellStyle("NODE", style);

	// Define Invisibility-Style for Edges
	style = new Hashtable<>();
	style.put(mxConstants.STYLE_MOVABLE, false);
	style.put(mxConstants.STYLE_EDITABLE, false);
	style.put(mxConstants.STYLE_RESIZABLE, false);
	style.put(mxConstants.STYLE_STROKEWIDTH, 2);
	style.put(mxConstants.STYLE_STROKECOLOR, "black");
	stylesheet.putCellStyle("EDGE_ARROW", style);

	// Define Style for plain Edges
	style = (Hashtable<String, Object>) style.clone();
	style.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
	stylesheet.putCellStyle("EDGE_PLAIN", style);
  }

  // Observer-Method
  @Override
  public void update(Observable o, Object arg) {
	if (arg instanceof String) {
	  exportToPng((String) arg);
	}
  }
}
