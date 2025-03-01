package com.coderdream.util.youtube.yt.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessBroker {
  private final List<String> command = new ArrayList<>();
  private final StringBuilder stdout = new StringBuilder();
  private final StringBuilder stderr = new StringBuilder();

  public ProcessBroker(String... command) {
    this.command.addAll(Arrays.asList(command));
  }

  public int execute() throws IOException, InterruptedException {
    Process process = new ProcessBuilder(command).start();
    new StreamReaderThread(process.getInputStream(), stdout).start();
    new StreamReaderThread(process.getErrorStream(), stderr).start();
    return process.waitFor();
  }

  public String getStdout() {
    return stdout.toString();
  }

  public String getStderr() {
    return stderr.toString();
  }

  static class StreamReaderThread extends Thread {
    InputStream inputStream;
    StringBuilder output;

    StreamReaderThread(final InputStream inputStream, StringBuilder output) {
      this.inputStream = inputStream;
      this.output = output;
    }

    @Override
    public void start() {
      try {
        readStream(inputStream, output);
      } catch (IOException e) {
        output.append(e.getMessage());
      }
    }

    private void readStream(InputStream inputStream, StringBuilder sb) throws IOException {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "Shift-JIS"))) {
        String line;
        while ((line = reader.readLine()) != null)
          sb.append(line).append("\n");
      }
    }
  }
}
