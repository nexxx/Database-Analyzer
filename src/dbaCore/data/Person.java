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

package dbaCore.data;

/**
 * Class representing a person for the metainformation
 *
 * @author Andreas Freitag
 */
public class Person {
  private String name;
  private String job;
  private String mail;
  private String tel;
  private String fax;

  public Person() {
    super();
    name = "";
    job = "";
    mail = "";
    tel = "";
    fax = "";
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the job
   */
  public String getJob() {
    return job;
  }

  /**
   * @param job the job to set
   */
  public void setJob(String job) {
    this.job = job;
  }

  /**
   * @return the mail
   */
  public String getMail() {
    return mail;
  }

  /**
   * @param mail the mail to set
   */
  public void setMail(String mail) {
    this.mail = mail;
  }

  /**
   * @return the tel
   */
  public String getTel() {
    return tel;
  }

  /**
   * @param tel the tel to set
   */
  public void setTel(String tel) {
    this.tel = tel;
  }

  /**
   * @return the fax
   */
  public String getFax() {
    return fax;
  }

  /**
   * @param fax the fax to set
   */
  public void setFax(String fax) {
    this.fax = fax;
  }

  @Override
  public String toString() {
    return name;
  }
}
