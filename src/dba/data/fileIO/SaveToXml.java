/**
 * 
 */
package dba.data.fileIO;

import java.io.File;

import javax.xml.bind.JAXB;

import data.Database;
import dba.options.Options;

/**
 * Class to save the database to a xml file
 * 
 * @author Andreas Freitag
 * 
 */
public class SaveToXml {

  Options options;

  public SaveToXml() {
	options = Options.getInstance();
  }

  /**
   * Save the given database to a xml file. Used for CMD
   * 
   * @param saveMe
   *          Database which will be stored to a xml file
   * @param Name
   *          Name of the file which will be saved
   * @throws Exception
   *           File Exceptions
   */
  public void SaveDbNow(Database saveMe, String Name) throws Exception {
	File file = new File(options.getSaveFolder() + "/" + Name);
	SaveDbNow(saveMe, file);
  }

  /**
   * Save the given database to a xml file. Used for GUI
   * 
   * @param saveMe
   *          Database which will be stored to a xml file
   * @param file
   *          File where the XML will be stored
   * @exception javax.xml.bind.JAXBException
   */
  public void SaveDbNow(Database saveMe, File file) throws Exception {
	JAXB.marshal(saveMe, file);
  }
}
