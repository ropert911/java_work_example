package com.quick.jdbc.model;

/**
 * @author sk-qianxiao
 * @date 2019/12/11
 */
public class CDHConfig {
    private Long configId;
    private String attr;
    private String value;

    public CDHConfig(Long configId, String attr, String value) {
        this.configId = configId;
        this.attr = attr;
        this.value = value;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CDHConfig{" +
                "configId=" + configId +
                ", attr='" + attr + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
