package com.twdt.test;


import com.twdt.sender.ServerConnectorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
   private static final Logger logger = LoggerFactory.getLogger(LogTest.class);
    public static void main(String[] args) {
//        logger.info("aaaaaaaaaaaaaa");
//        logger.debug("aaaaaaaaaaaaaaa");

        ServerConnectorUtil.write2Log("aaaaaaaaaaaaaaaaaaa");

    }
}
