package dba.utils;

import java.util.Observer;

/**
 * Observable for Observer-Pattern
 * 
 * @author Sebastian Theuermann
 */
public interface Observable {
  public abstract void addObserver(Observer observer);

  public abstract void removeObserver(Observer observer);

  public abstract void notify(Object arg);
}
