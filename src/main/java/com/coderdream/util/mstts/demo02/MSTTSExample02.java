package com.coderdream.util.mstts.demo02;

import com.coderdream.util.cd.CdConstants;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MSTTSExample02 {

    private static final String AZURE_SPEECH_KEY = CdConstants.SPEECH_KEY_EASTASIA; // 替换成你的Azure Speech Key
    private static final String AZURE_SPEECH_REGION = CdConstants.SPEECH_REGION_EASTASIA; // 替换成你的Azure Speech Region

    public static String generateSSML(String sentence, String word1, String pronunciation1, String word2, String pronunciation2) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // 创建根元素
        Document doc = docBuilder.newDocument();
        Element speak = doc.createElement("speak");
        doc.appendChild(speak);
        speak.setAttribute("version", "1.0");
        speak.setAttribute("xmlns", "http://www.w3.org/2001/10/synthesis");
        speak.setAttribute("xml:lang", "zh-CN"); // 设置语言

        // 创建 voice 元素
        Element voice = doc.createElement("voice");
        voice.setAttribute("name", "zh-CN-XiaoxiaoNeural"); // 选择中文语音
        speak.appendChild(voice);

        String[] parts = sentence.split(word1, 2);  // 分割句子
        voice.appendChild(doc.createTextNode(parts[0]));

        // 创建 phoneme 元素 1
        Element phoneme1 = doc.createElement("phoneme");
        phoneme1.setAttribute("alphabet", CdConstants.SAPI); // 使用国际音标
        phoneme1.setAttribute("ph", pronunciation1); // 设置发音
        phoneme1.setTextContent(word1); // 设置文本
        voice.appendChild(phoneme1);

        if (parts.length > 1) { // 句子中存在第二个词
            String[] parts2 = parts[1].split(word2, 2);
            voice.appendChild(doc.createTextNode(parts2[0]));

            // 创建 phoneme 元素 2
            Element phoneme2 = doc.createElement("phoneme");
            phoneme2.setAttribute("alphabet", CdConstants.SAPI); // 使用国际音标
            phoneme2.setAttribute("ph", pronunciation2); // 设置发音
            phoneme2.setTextContent(word2); // 设置文本
            voice.appendChild(phoneme2);

            if (parts2.length > 1) {
                voice.appendChild(doc.createTextNode(parts2[1]));
            }
        }

        // 将 XML 文档转换为 String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        return writer.toString();
    }

    public static byte[] textToSpeech(String ssml) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String subscriptionKey = AZURE_SPEECH_KEY;
        String region = AZURE_SPEECH_REGION;
        String endpoint = "https://" + region + ".tts.speech.microsoft.com/cognitiveservices/v1";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .header("Ocp-Apim-Subscription-Key", subscriptionKey)
            .header("Content-Type", "application/ssml+xml")
            .header("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3") // 指定输出格式
            .header("User-Agent", "MSTTS Java Example")
            .header("X-Microsoft-Language", "zh-CN")
            .POST(HttpRequest.BodyPublishers.ofString(ssml))
            .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.err.println("Error during text-to-speech: " + response.statusCode() + " - " + response.body());
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // 示例：
            String sentence = "这个物体的重量是五公斤，请重复一遍。";
            String word1 = "重量";
            String pronunciation1 = "ʈ͡ʂʰʊŋ⁵¹ li̯ɑŋ²¹⁴"; // "zhòng liàng" 的 IPA 表示
            String word2 = "重复";
            String pronunciation2 = "ʈ͡ʂʰʊŋ²¹⁴ fu⁵¹"; // "chóng fù" 的 IPA 表示

            String ssml = generateSSML(sentence, word1, pronunciation1, word2, pronunciation2);

            byte[] audioData = textToSpeech(ssml);

            if (audioData != null) {
                // 将音频数据保存到文件 (可选)
                java.nio.file.Files.write(java.nio.file.Paths.get("output.mp3"), audioData);
                System.out.println("Audio saved to output.mp3");
                // 或者你可以直接播放音频数据，具体实现取决于你使用的音频库。
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
