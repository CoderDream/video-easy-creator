package com.coderdream.util.gemini.demo01;

import com.coderdream.util.cd.CdConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvPhoneticFiller {

    private static final String INPUT_CSV_PATH = "C:\\Users\\CoderDream\\Desktop\\cefr_total_words.csv";
    private static final String OUTPUT_CSV_PATH = "C:\\Users\\CoderDream\\Desktop\\cefr_total_words_new.csv";
    private static final String GEMINI_API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 API 密钥
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 1080;

    public static void main(String[] args) {
        try {
            processCsv(INPUT_CSV_PATH, OUTPUT_CSV_PATH);
            log.info("CSV processing complete. Output file: " + OUTPUT_CSV_PATH);
        } catch (IOException e) {
            log.error("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    private static void processCsv(String inputPath, String outputPath) throws IOException {
        List<CSVRecord> records = new ArrayList<>();

        // 修改 CSV 读取方式以处理 BOM 字符和大小写问题
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
        try (Reader reader = new InputStreamReader(new FileInputStream(inputPath), StandardCharsets.UTF_8)) {
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            // 读取所有记录到内存
            for (CSVRecord record : csvParser) {
                records.add(record);
            }
            csvParser.close();
        } catch (Exception e) {
            log.error("Error reading CSV file: " + e.getMessage(), e);
            throw e; // Re-throw the exception to be caught in main
        }


        // 处理记录并写入新文件
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputPath), csvFormat.withHeader(
                "id", "headword", "english_phonetic ", "american_phonetic "))) {

            for (int i = 0; i < records.size(); i += 200) {
                List<CSVRecord> batch = records.subList(i, Math.min(i + 200, records.size()));
                processBatch(batch, csvPrinter);
            }
        } catch (Exception e) {
            log.error("Error writing CSV file: " + e.getMessage(), e);
            throw e; // Re-throw the exception to be caught in main
        }
    }


    private static void processBatch(List<CSVRecord> batch, CSVPrinter csvPrinter) throws IOException {
        for (CSVRecord record : batch) {
            String id = record.get(0);
            String headword = record.get(1);

            // 确保精确匹配 Header 名称 (包含尾部空格)
            String englishPhoneticHeader = "english_phonetic ";
            String americanPhoneticHeader = "american_phonetic ";

            // 检查是否已经有音标，使用包含尾部空格的 Header 名称
            if (record.isSet(englishPhoneticHeader) && !record.get(englishPhoneticHeader).isEmpty()
                    && record.isSet(americanPhoneticHeader) && !record.get(americanPhoneticHeader).isEmpty()) {
                csvPrinter.printRecord(id, headword, record.get(englishPhoneticHeader), record.get(americanPhoneticHeader));
                continue;
            }

            try {
                // 获取音标
                PhoneticData phoneticData = getPhoneticData(headword);

                // 写入记录
                csvPrinter.printRecord(id, headword, phoneticData.englishPhonetic, phoneticData.americanPhonetic);
                log.info("Processed: " + headword);

            } catch (Exception e) {
                log.error("Error processing " + headword + ": " + e.getMessage(), e);
                csvPrinter.printRecord(id, headword, "", ""); // 写入空音标
            }

            csvPrinter.flush(); // 及时刷新
        }
    }


    private static PhoneticData getPhoneticData(String headword) throws Exception {
        // 使用 API 获取音标数据
        String prompt = "Provide the English (UK) and American phonetic transcriptions (IPA) for the word: " + headword +
                ". Separate the UK and US transcriptions with a semicolon. If not available, leave it blank";

        String jsonRequest = String.format("""
                {
                  "contents": [{
                    "parts":[{
                      "text": "%s"
                    }]
                  }],
                  "safetySettings": [
                        {
                            "category": "HARM_CATEGORY_HARASSMENT",
                            "threshold": "BLOCK_NONE"
                        },
                        {
                            "category": "HARM_CATEGORY_HATE_SPEECH",
                            "threshold": "BLOCK_NONE"
                        },
                        {
                            "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                            "threshold": "BLOCK_NONE"
                        },
                        {
                            "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                            "threshold": "BLOCK_NONE"
                        }
                   ]
                }
                """, prompt);

        HttpClient client = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress(PROXY_HOST, PROXY_PORT)))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status code: " + response.statusCode() + ", body: " + response.body());
        }

        // 解析 API 响应
        String responseBody = response.body();
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray candidates = jsonObject.getAsJsonArray("candidates");
        JsonObject content = candidates.get(0).getAsJsonObject();
        JsonArray parts = content.getAsJsonObject().getAsJsonArray("parts");
        JsonObject part = parts.get(0).getAsJsonObject();
        String text = part.get("parts").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();


        // 拆分音标数据
        String[] phonetics = text.split(";");
        String englishPhonetic = (phonetics.length > 0) ? phonetics[0].trim() : "";
        String americanPhonetic = (phonetics.length > 1) ? phonetics[1].trim() : "";

        return new PhoneticData(englishPhonetic, americanPhonetic);
    }

    // 存储音标数据的内部类
    private static class PhoneticData {
        String englishPhonetic;
        String americanPhonetic;

        public PhoneticData(String englishPhonetic, String americanPhonetic) {
            this.englishPhonetic = englishPhonetic;
            this.americanPhonetic = americanPhonetic;
        }
    }

}
