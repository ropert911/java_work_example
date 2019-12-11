package com.quick.jdbc.model;

/**
 * @author sk-qianxiao
 * @date 2019/12/11
 */
public class CDHRoles {
    private Long hostId;
    private String roleType;
    private Long serviceId;

    public CDHRoles(Long hostId, String roleType, Long serviceId) {
        this.hostId = hostId;
        this.roleType = roleType;
        this.serviceId = serviceId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "CDHRoles{" +
                "hostId=" + hostId +
                ", roleType='" + roleType + '\'' +
                ", serviceId=" + serviceId +
                '}';
    }
}
