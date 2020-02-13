package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author sk-qianxiao
 */
@SpringBootApplication
public class DemoWorkDataforbigdataApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(DemoWorkDataforbigdataApplication.class, args);
        //kafka ip
        String property = configurableApplicationContext.getEnvironment().getProperty("kip");
        System.out.println("111111111");
        System.out.println(property);
    }

}
