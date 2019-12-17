package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/11/6
 */
@Getter
@Setter
@ToString
@Builder
//@AllArgsConstructor
public class GWSensorDataDTO implements Serializable {
    private Integer channel;
    private Integer sf;
    private String time;
    private String gwip;
    private String gwid;
    private String repeater;
    private Integer systype;
    private Float rssi;
    private Float snr;
    private Float snr_max;
    private Float snr_min;
    private String macAddr;
    private String data;
    private Integer frameCnt;
    private Integer fport;
    private List<UnwindData> unwindData;
    private String deviceTypeCode;
    private String sysTime;
    private Long itfTime;
    private AdditionalInfo additionalInfo;
}
