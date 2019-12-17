package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoWorkDataforbigdataApplicationTests {

    @Test
    void contextLoads() {
        //定义一个十进制值
        int valueTen = 0x11761;
        //将其转换为十六进制并输出
        String strHex = "90100005000" + Integer.toHexString(0x11761);
        System.out.println(strHex);
    }

}
