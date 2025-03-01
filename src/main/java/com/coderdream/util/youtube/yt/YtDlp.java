package com.coderdream.util.youtube.yt;

import com.coderdream.util.youtube.yt.broker.LongProcessBroker;
import com.coderdream.util.youtube.yt.broker.ProcessBroker;
import com.coderdream.util.youtube.yt.builder.YtDlpOption;
import com.coderdream.util.youtube.yt.builder.YtDlpOptionBuilder;
import com.coderdream.util.youtube.yt.event.ProcessStreamChangeEventListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

//import com.litongjava.yt.broker.LongProcessBroker;
//import com.litongjava.yt.broker.ProcessBroker;
//import com.litongjava.yt.builder.YtDlpOption;
//import com.litongjava.yt.builder.YtDlpOptionBuilder;
//import com.litongjava.yt.event.ProcessStreamChangeEventListener;
//import com.litongjava.yt.utils.SnowflakeId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YtDlp {

  // Download URLs for different operating systems
  public static final String windows_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";
  public static final String linux_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux";
  public static final String mac_os_download_url = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp";

  /**
   * Downloads the yt-dlp executable if it does not already exist.
   */
  public static void downloadYtDlp() {
    File file = ytDlpFile();
    if (file.exists()) {
      // If the file already exists, no need to download it
      return;
    }

    String downloadUrl = ytDlpUrl();
    HttpURLConnection connection = null;

    try {
      @SuppressWarnings("deprecation")
      URL url = new URL(downloadUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(15000);
      connection.setReadTimeout(15000);

      // Start the connection
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (InputStream inputStream = connection.getInputStream(); OutputStream outputStream = Files.newOutputStream(file.toPath())) {
          byte[] buffer = new byte[4096];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
          System.out.println("Download completed: " + file.getAbsolutePath());
        }
      } else {
        System.err.println("Download failed, server returned response code: " + responseCode);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Returns the file where yt-dlp is or should be downloaded.
   *
   * @return File object representing the yt-dlp executable.
   */
  public static File ytDlpFile() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return new File("yt-dlp.exe");
    } else if (os.startsWith("linux")) {
      return new File("yt-dlp_linux");
    }
    return new File("yt-dlp");
  }

  /**
   * Returns the download URL for the current operating system.
   *
   * @return A string representing the download URL.
   */
  public static String ytDlpUrl() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      return windows_download_url;
    } else if (os.startsWith("linux")) {
      return linux_download_url;
    }
    return mac_os_download_url;
  }

  /**
   * Executes yt-dlp with the specified options in a synchronous manner.
   *
   * @param options The command-line options as a string.
   * @return The standard output of the executed command.
   * @throws IOException          If an I/O error occurs.
   * @throws InterruptedException If the execution is interrupted.
   */
  public static String run(String options) throws IOException, InterruptedException {
    ProcessBroker processBroker = new ProcessBroker(("./" + ytDlpFile().getPath() + "  " + options).split("  "));
    processBroker.execute();
    return processBroker.getStdout();
  }

  /**
   * Executes yt-dlp with the specified options in a long-running process.
   * Logs the command execution with a unique task ID.
   *
   * @param option   The command-line options.
   * @param listener Listener to capture process stream changes.
   * @return A LongProcessBroker instance managing the process.
   */
  public static LongProcessBroker runLong(String option, ProcessStreamChangeEventListener listener) {
    long id = SnowflakeId.id();
    String commandString = "./" + ytDlpFile().getPath() + "  " + option;
    log.info("Task id: " + id + " Executing command: " + commandString);
    String[] commandArray = commandString.split("  ");
    LongProcessBroker longProcessBroker = new LongProcessBroker(commandArray);
    longProcessBroker.addProcessStreamChangeEventListener(listener);
    longProcessBroker.execute();
    log.info("Finish task: " + id);
    return longProcessBroker;
  }

  /**
   * Downloads the audio for the specified URL.
   * Options must be built using the audio() method of YtDlpOptionBuilder.
   *
   * @param options The options for downloading and converting audio.
   * @return The output generated by yt-dlp.
   */
  public static String execute(YtDlpOption options) {
    String option = options.getStringBuilder() + options.getUrl();
    return execute(option);
  }

  /**
   * Internal execute method that downloads yt-dlp (if needed) and runs the command.
   *
   * @param option The command-line options.
   * @return The output from the command execution.
   */
  private static String execute(String option) {
    StringBuffer stringBuffer = new StringBuffer();
    YtDlp.downloadYtDlp();
    YtDlp.runLong(option, event -> stringBuffer.append(event.getChangedString()).append("\r\n"));
    return stringBuffer.toString();
  }

  /**
   * Downloads the audio for the specified URL using a provided listener for process stream changes.
   *
   * @param options  The options for downloading and converting audio.
   * @param listener The listener to handle process stream change events.
   */
  public static void execute(YtDlpOption options, ProcessStreamChangeEventListener listener) {
    YtDlp.downloadYtDlp();
    YtDlp.runLong(options.getStringBuilder() + options.getUrl(), listener);
  }

  /**
   * Retrieves the available formats for the specified URL.
   *
   * @param url The URL of the video.
   * @return The available formats as a String.
   */
  public static String getAvailableFormats(String url) {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    YtDlpOption options = ytDlpOptionBuilder.url(url).listFormats().build();
    try {
      return YtDlp.run(options.getStringBuilder() + options.getUrl());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
