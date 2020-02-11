package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata;

import com.alibaba.fastjson.JSON;
import com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.dto.*;
import com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.kafka.KafkaOperations;
import com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.utils.TimeUtilForJavaEight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author sk-qianxiao
 * @date 2019/11/6
 */
@Component
public class InitProject implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(InitProject.class);

    @Value("${iot.kafka.producer.topic.origin.data}")
    private String originData;

    @Autowired
    private KafkaOperations kafkaOperations;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sendFireSaferData("3001", 0x11760, 105);
        sendFireSaferData("3002", 0x21780, 105);
        sendFireSaferData("3003", 0x31800, 105);
        sendFireSaferData("3004", 0x41800, 105);
    }

    private List<UnwindData> creatUnWindData(String devType, boolean isNormal) {
        List<UnwindData> unwindDataList = new ArrayList<>();
        unwindDataList.add(UnwindData.builder().key("sensorVoltage").value(5.5).build());
        Random random = new Random();
        switch (devType) {
            //TES 单路温度传感器
            case "3001":
                if (isNormal) {
                    unwindDataList.add(UnwindData.builder().key("temperature").value(30 + random.nextInt(20)).build());
                } else {
                    unwindDataList.add(UnwindData.builder().key("temperature").value(50 + random.nextInt(40)).build());
                }
                break;
            //THS 温湿度传感器
            case "3002":
                if (isNormal) {
                    unwindDataList.add(UnwindData.builder().key("temperature").value(30 + random.nextInt(20)).build());
                    unwindDataList.add(UnwindData.builder().key("humidityPresent").value(10 + random.nextInt(20)).build());
                } else {
                    unwindDataList.add(UnwindData.builder().key("temperature").value(50 + random.nextInt(40)).build());
                    unwindDataList.add(UnwindData.builder().key("humidityPresent").value(50 + random.nextInt(40)).build());
                }
                break;
            //WPS  水压传感器
            case "3003":
                if (isNormal) {
                    double value = 0.40 + random.nextInt(40) / 100d;
                    logger.info("3003 noarmal== {}", value);
                    unwindDataList.add(UnwindData.builder().key("hydraulic").value(value).build());
                } else {
                    double value = 0.60 + random.nextInt(60) / 100d;
                    logger.info("3003 abnoarmal== {}", value);
                    unwindDataList.add(UnwindData.builder().key("hydraulic").value(value).build());
                }
                break;
            //WDS 水位传感器
            case "3004":
                if (isNormal) {
                    double value = 1d + random.nextInt(100) / 100d;
                    logger.info("3004 noarmal== {}", value);
                    unwindDataList.add(UnwindData.builder().key("stage").value(value).build());
                } else {
                    double value = 2d + random.nextInt(200) / 100d;
                    logger.info("3004 abnoarmal== {}", value);
                    unwindDataList.add(UnwindData.builder().key("stage").value(value).build());
                }
                break;
            default:
                break;
        }

        return unwindDataList;
    }

    private void sendFireSaferData(String deviceTypeCode, int macBegin, int devCount) {
        logger.info("=====================Begin........{}", originData);

        for (int i = 0; i < devCount; ++i) {
            String devMacAddr = "90100005000" + Integer.toHexString(macBegin + i).toUpperCase();
            String gwid = "000080029c09e987";
            boolean isNormal = true;
            int step = (devCount - 3) / 5;
            if (i > step * 5) {
                isNormal = false;
            }
            List<UnwindData> unwindDataList = creatUnWindData(deviceTypeCode, isNormal);

            String formatDate = TimeUtilForJavaEight.getFormatDate(System.currentTimeMillis(), TimeUnit.MILLISECONDS, TimeUtilForJavaEight.PATTERN_YYYY_MM_DDTHH_MM_SS_XXX);
            String sysTime = TimeUtilForJavaEight.getFormatDate(System.currentTimeMillis(), TimeUnit.MILLISECONDS, TimeUtilForJavaEight.PATTERN_YYYY_MM_DDTHH_MM_SS_SSSXXX);

            DeviceInfo deviceInfo = DeviceInfo.builder().firstAreaId(1737L).secondAreaId(1738L).thirdAreaId(1739L).build();
            List<Long> fList = new ArrayList<>();
            fList.add(1737L);
            GatewayInfo gwInfo = GatewayInfo.builder().firstAreaId(fList).build();
            AdditionalInfo additionalInfo = AdditionalInfo.builder().deviceInfo(deviceInfo).gwInfo(gwInfo).build();
            GWSensorDataDTO gwSensorDataDTO = GWSensorDataDTO.builder()
                    .rssi(-27.0f).gwip("10.10.21.84").data("a037011464").channel(488500000).snr_max(37.0f)
                    .frameCnt(4).sf(12).systype(3).fport(5).snr(21.3f).snr_min(9.5f).repeater("00000000ffffffff")
                    .itfTime(System.currentTimeMillis() / 1_000).time(formatDate).sysTime(sysTime)
                    .gwid(gwid)
                    .deviceTypeCode(deviceTypeCode).macAddr(devMacAddr)
                    .unwindData(unwindDataList)
                    .additionalInfo(additionalInfo)
                    .build();


            kafkaOperations.sendData(originData, (deviceTypeCode + "_" + devMacAddr + "_" + gwid).getBytes(), JSON.toJSONString(gwSensorDataDTO).getBytes());
        }
        logger.info("=====================End........");
    }
}