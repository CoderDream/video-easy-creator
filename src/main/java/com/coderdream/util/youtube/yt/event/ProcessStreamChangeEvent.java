package com.coderdream.util.youtube.yt.event;

import java.util.EventObject;
import java.util.List;

public class ProcessStreamChangeEvent extends EventObject {

  private static final long serialVersionUID = -4422303639442631331L;
  private final List<String> strings;
  private final String changedString;

  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   * @throws IllegalArgumentException if source is null.
   */
  public ProcessStreamChangeEvent(Object source, List<String> strings, String changedString) {
    super(source);
    this.strings = strings;
    this.changedString = changedString;
  }

  public String getChangedString() {
    return changedString;
  }

  public List<String> getStrings() {
    return strings;
  }
}
