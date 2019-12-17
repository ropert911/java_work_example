package com.xq.demo.readcdh;


import com.xq.demo.readcdh.service.CDHDataBaseService;
import com.xq.demo.readcdh.utils.CdhJdbcUtils;

public class ReadcdhNonspringApplication {

    public static void main(String[] args) {
        CdhJdbcUtils.init("192.168.20.148:5432,192.168.20.149:5432,192.168.20.150:5432", "postgres", "sks123.com");
//        CdhJdbcUtils.test();

        CDHDataBaseService cdhDataBaseService = new CDHDataBaseService();
        cdhDataBaseService.getZooKeeperServers();
        cdhDataBaseService.getHiveServer2();
        cdhDataBaseService.getBootsTrapServers();
        cdhDataBaseService.getBootsTrapServers2();

    }

}
