package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author sk-shifanwen
 * @date 2019/6/5
 */
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class UnwindData implements Serializable {
    private String key;
    private Object value;
}
