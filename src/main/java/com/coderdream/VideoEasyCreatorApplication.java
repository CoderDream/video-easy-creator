package com.coderdream;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.coderdream.mapper")
public class VideoEasyCreatorApplication {

    public static void main(String[] args) {
//        System.setProperty("https://api.chatanywhere.tech","127.0.0.1");
//        System.setProperty("http.proxyPort","7890");
//        System.setProperty("https://api.chatanywhere.tech","127.0.0.1");
//        System.setProperty("https.proxyPort","7890");
//        Document document = new Document(PageSize.A4);
        org.apache.hc.client5.http.ssl.TlsSocketStrategy tlsSocketStrategy = null;
//        freemarker.ext.jakarta.jsp.TaglibFactory taglibFactory = null;
        SpringApplication.run(VideoEasyCreatorApplication.class, args);
    }

}
