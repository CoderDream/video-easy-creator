package com.coderdream.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {

    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

    public void myMethod() {
        logger.debug("This is a debug message.");
        logger.info("This is an info message.");
        logger.warn("This is a warning message.");
        logger.error("This is an error message.");
        logger.trace("This is a trace message");
    }

    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        myClass.myMethod();
    }
}