package com.coderdream.util.youtube.yt.builder;

/**
 * YtDlpOptionBuilder is a builder class for constructing command-line options
 * for yt-dlp. It uses a StringBuilder to accumulate options and parameters.
 */
public class YtDlpOptionBuilder {

  // StringBuilder to accumulate command-line options separated by two spaces.
  private final StringBuilder stringBuilder = new StringBuilder();

  // URL of the video to process.
  private String url = "";

  /**
   * Appends a command-line token and two spaces as a separator.
   *
   * @param str The token to append.
   */
  private void append(String str) {
    stringBuilder.append(str).append("  ");
  }

  /**
   * Sets the video URL.
   *
   * @param urla The URL of the video.
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder url(String urla) {
    this.url = urla;
    return this;
  }

  /**
   * Sets the output filename template.
   * <p>
   * This method adds the "--output" option along with the specified template.
   * If the template contains spaces, it will be wrapped in quotes.
   *
   * @param template The output filename template (e.g., "%(title)s.%(ext)s").
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder output(String template) {
    append("--output");
    append("\"" + template + "\""); // Wrap the template in quotes if it contains spaces.
    return this;
  }

  /**
   * Adds the option to list available formats for the given video.
   * <p>
   * This corresponds to the "-F" flag in yt-dlp.
   *
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder listFormats() {
    append("-F");
    return this;
  }

  /**
   * Adds the option to extract audio from the video.
   * <p>
   * This corresponds to the "-x" flag in yt-dlp.
   *
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder audio() {
    append("-x");
    return this;
  }

  /**
   * Specifies the audio format to convert the extracted audio.
   * <p>
   * This adds the "--audio-format" option followed by the desired format.
   *
   * @param format The desired audio format (e.g., "mp3", "m4a", etc.).
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder audioFormat(String format) {
    append("--audio-format");
    append(format);
    return this;
  }

  /**
   * Specifies the audio quality for the conversion.
   * <p>
   * This adds the "--audio-quality" option followed by the desired quality.
   *
   * @param quality The desired audio quality (e.g., "0", "128K", etc.).
   * @return The current instance of YtDlpOptionBuilder.
   */
  public YtDlpOptionBuilder audioQuality(String quality) {
    append("--audio-quality");
    append(quality);
    return this;
  }

  /**
   * Builds the final YtDlpOption object which contains the constructed command-line options
   * and the URL.
   *
   * @return A new YtDlpOption instance.
   */
  public YtDlpOption build() {
    return new YtDlpOption(stringBuilder, url);
  }
}
