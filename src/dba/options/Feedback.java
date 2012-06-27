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

package dba.options;

/**
 * Represents a Feedback (Text,Result)
 *
 * @author Sebastian Theuermann
 */
public class Feedback {

  private String text;
  private FeedbackEnum feedback;

  public Feedback(String text, FeedbackEnum feedback) {
    super();
    this.text = text;
    this.feedback = feedback;
  }

  public String getText() {
    return text;
  }

  public FeedbackEnum getFeedback() {
    return feedback;
  }

}
