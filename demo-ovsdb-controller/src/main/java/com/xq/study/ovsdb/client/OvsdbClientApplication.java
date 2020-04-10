package com.xq.study.ovsdb.client;


import com.vmware.ovsdb.exception.OvsdbClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

/**
 * @author sk-qianxiao
 */
@SpringBootApplication
public class OvsdbClientApplication implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(OvsdbClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OvsdbClientApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws OvsdbClientException, IOException {
        OvsdbClientPassiveConnectionTest test = new OvsdbClientPassiveConnectionTest();
        test.setUp(false);
        test.testTcpConnection();
//        test.tearDown();
    }
}
