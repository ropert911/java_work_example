package com.xq.work.examples.dto;


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
public class EventInfo implements Serializable {
    private Integer eventId;
    private String deviceMac;
    private Long eventLastTime;
    private Long eventFirstTime;
    private Integer eventFreq;
    private String sensorType;
    private String description;
    private String probReason;
    private String specReason;
}
