package com.quick.jdbc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hosts")
public class CDHHosts {
    @Id
    @Column(name = "host_id")
    private long hostId;
    @Column(name = "name")
    private String name;
    @Column(name = "ip_address")
    private String ipAddr;

    public CDHHosts() {
    }

    public CDHHosts(long hostId, String name, String ipAddr) {
        this.hostId = hostId;
        this.name = name;
        this.ipAddr = ipAddr;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    @Override
    public String toString() {
        return "CDHHosts{" +
                "hostId=" + hostId +
                ", name='" + name + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                '}';
    }
}