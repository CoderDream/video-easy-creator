package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-structure#single-voice-example#add-a-break">添加中断</a>
 *  Add a break  添加中断
 * </pre>
 */
@Slf4j
public class SpeechSynthesis1003 {

  // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
  private static String speechKey = "50vWNO4RVL41CEjbkm4aT5c8VPBjO1XNQOMWEYgX3IrrKQn37XTTJQQJ99BCACYeBjFXJ3w3AAAYACOGjmML";//  CdConstants.SPEECH_KEY_EAST_US;// System.getenv("SPEECH_KEY");
  private static String speechRegion = "eastus";// "eastasia";// System.getenv("SPEECH_REGION");

  public static void main(String[] args)
    throws InterruptedException, ExecutionException {
    long startTime = System.currentTimeMillis();
    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
      speechRegion);
    SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,
      null);

    String index = "1003";
    String ssml = xmlToString(
      CdFileUtil.getResourceRealPath() + File.separator + "ssml"
        + File.separator + "ssml" + index + ".xml");
    SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
    AudioDataStream stream = AudioDataStream.fromResult(result);
    stream.saveToWavFile(
      OperatingSystem.getBaseFolder() + File.separator + "mstts"
        + File.separator + "file" + index + ".wav");
    long elapsedTime = System.currentTimeMillis() - startTime;
    String duration = CdTimeUtil.formatDuration(elapsedTime);
    log.info("WAV 文件合并完成，耗时：{}", duration);
  }

  private static String xmlToString(String filePath) {
    File file = new File(filePath);
    StringBuilder fileContents = new StringBuilder((int) file.length());

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        fileContents.append(scanner.nextLine())
          .append(System.lineSeparator());
      }
      return fileContents.toString().trim();
    } catch (FileNotFoundException ex) {
      return "File not found.";
    }
  }
}
