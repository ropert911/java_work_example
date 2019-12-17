package com.xq.demo.readcdh.model;

/**
 * @author sk-qianxiao
 * @date 2019/12/11
 */
public class CDHServices {
    private Long serviceId;
    private String name;
    private String service_type;

    public CDHServices(Long serviceId, String name, String service_type) {
        this.serviceId = serviceId;
        this.name = name;
        this.service_type = service_type;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    @Override
    public String toString() {
        return "CDHServices{" +
                "serviceId=" + serviceId +
                ", name='" + name + '\'' +
                ", service_type='" + service_type + '\'' +
                '}';
    }
}
