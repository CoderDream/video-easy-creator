package com.coderdream.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ClassPathResourceExample {
    public void getClassPathResource(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            System.out.println("Resource URI: " + classPathResource.getURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClassPathResourceExample example = new ClassPathResourceExample();
        example.getClassPathResource("application.properties");
    }
}
