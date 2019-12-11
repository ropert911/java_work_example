package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.dto;

import lombok.*;

import java.util.List;

/**
 * @author sk-litao
 * @date 2019/11/29
 */
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class GatewayInfo {
    private List<Long> firstAreaId;
}
