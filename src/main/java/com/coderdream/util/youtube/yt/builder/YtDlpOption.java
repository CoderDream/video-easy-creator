package com.coderdream.util.youtube.yt.builder;

public class YtDlpOption {
  private final StringBuilder stringBuilder;
  private final String url;

  public YtDlpOption(StringBuilder stringBuilder, String url) {
    this.stringBuilder = stringBuilder;
    this.url = url;
  }

  public StringBuilder getStringBuilder() {
    return stringBuilder;
  }

  public String getUrl() {
    return url;
  }
}
