package com.xq.study.demo.demo;

import com.vmware.ovsdb.exception.OvsdbClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DemoApplication implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

    }

    @Override
    public void run(ApplicationArguments args) throws OvsdbClientException, IOException {
        OvsdbClientPassiveConnectionTest test = new OvsdbClientPassiveConnectionTest();
        test.setUp(false);
        test.testTcpConnection();
//        test.tearDown();
    }
}
