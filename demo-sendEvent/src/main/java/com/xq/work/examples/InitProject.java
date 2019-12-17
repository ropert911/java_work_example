package com.xq.work.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author sk-qianxiao
 * @date 2019/11/6
 */
@Component
public class InitProject implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(InitProject.class);

    @Autowired
    private ServerSendEvent serverSendEvent;

    @Override
    public void run(ApplicationArguments args) {
        int server_id = 1;
        switch (server_id) {
            //发送iot事件的功能
            case 1: {
                serverSendEvent.startServer();
            }
            break;
            default:
                break;
        }

    }
}