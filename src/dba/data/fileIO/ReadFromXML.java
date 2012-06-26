package dba.data.fileIO;

import java.io.File;

import javax.xml.bind.JAXB;

import data.Database;
import dba.options.Options;

/**
 * Class to read the db from a xml file
 * 
 * @author Andreas Freitag
 * 
 */
public class ReadFromXML {
  Options options;

  public ReadFromXML() {
	options = Options.getInstance();
  }

  /**
   * Unmarshall a xml file and return the Database
   * 
   * @param name
   *          of the file which will be opened
   * @return Database read from xml
   * @throws Exception
   *           JAXB & File Exceptions
   */
  public Database ReadDbNow(String name) throws Exception {
	return ReadDbNow(new File(options.getSaveFolder() + "/" + name));

  }

  /**
   * Unmarshall a xml file and return the Database
   * 
   * @param file
   *          which will be opened
   * @return Database read from xml
   * @throws Exception
   *           JAXB & File Exceptions
   */
  public Database ReadDbNow(File file) throws Exception {
	return JAXB.unmarshal(file, Database.class);

  }
}
