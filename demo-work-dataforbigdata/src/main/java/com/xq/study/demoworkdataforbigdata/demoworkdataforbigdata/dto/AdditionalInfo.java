package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.dto;

import lombok.*;

/**
 * @author sk-litao
 * @date 2019/11/29
 */
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class AdditionalInfo {
    private DeviceInfo deviceInfo;
    private GatewayInfo gwInfo;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public GatewayInfo getGwInfo() {
        return gwInfo;
    }

    public void setGwInfo(GatewayInfo gwInfo) {
        this.gwInfo = gwInfo;
    }
}
