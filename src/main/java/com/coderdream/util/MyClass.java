package com.coderdream.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {

    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

    /**
     * 以下是级别关系（从高到低）：
     * OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
     */
    public void myMethod() {
        logger.trace("This is a trace message");
        logger.debug("This is a debug message.");
        logger.info("This is an info message.");
        logger.warn("This is a warning message.");
        logger.error("This is an error message.");
    }

    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        myClass.myMethod();
    }
}
