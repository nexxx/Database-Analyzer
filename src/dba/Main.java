package dba;
import dba.gui.MainWindow;
import dba.gui.tippOfTheDayFrame;
import dba.init.Initialize;
import dba.options.Options;


public class Main {
  Options options;

  /**
   * Main Class
   * 
   * @param args
   *          Arguments ( -cmd to start as commandline application)
   */
  public static void main(String[] args) {

	// Initialize - Load options ETC
	Initialize initialize = new Initialize();
	initialize.init();

	MainWindow mainWindow = new MainWindow();
	mainWindow.getFrame().setVisible(true);
	@SuppressWarnings("unused")
	tippOfTheDayFrame tippFrame = new tippOfTheDayFrame();
  }

}
