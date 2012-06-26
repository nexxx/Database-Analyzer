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
