package com.coderdream.util.txt.demo01;

import org.mozilla.universalchardet.UniversalDetector;
import java.io.FileInputStream;
import java.io.IOException;

public class EncodingDetector {
    public static String detectEncoding(String filePath) {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(filePath)) {
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            return detector.getDetectedCharset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
