package com.xq.demo.readcdh.service;


import com.xq.demo.readcdh.model.CDHConfig;
import com.xq.demo.readcdh.model.CDHHosts;
import com.xq.demo.readcdh.model.CDHRoles;
import com.xq.demo.readcdh.model.CDHServices;
import com.xq.demo.readcdh.utils.CdhJdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * @author sk-qianxiao
 * @date 2019/12/11
 */

public class CDHDataBaseService {
    private static Logger logger = LoggerFactory.getLogger(CDHDataBaseService.class);

    private Connection conn = null;

    /**
     * @return server地址：如 192.168.20.148:2181,192.168.20.149:2181,192.168.20.150:2181
     */
    public String getZooKeeperServers() {
        String serviceName = "zookeeper";
        String rolesType = "SERVER";
        String portConfigAttr = "";
        String portConfigDefaultValue = "2181";

        String servers = getServiceUrl(serviceName, rolesType, portConfigAttr, portConfigDefaultValue);

        logger.warn("zookeeper servers=={}", servers);
        System.out.println(String.format("zookeeper servers==%s", servers));
        return servers;
    }


    /**
     * @return server地址：如 192.168.20.148:9092,192.168.20.149:9092,192.168.20.150:9092
     */
    public String getBootsTrapServers() {
        String serviceName = "kafka";
        String portConfigAttr = "bootstrap.servers";
        String portConfigDefaultValue = "9092";

        conn = CdhJdbcUtils.getConnection();
        //获取服务ID
        List<CDHServices> servicesList = getAllServices();
        Long zkServiceId = getServicesID(servicesList, serviceName);
        if (-1L == zkServiceId) {
            logger.error("no service which name {}", serviceName);
            return null;
        }

        //获取属性配置
        String bootstrapServers = getConfig(zkServiceId, portConfigAttr, portConfigDefaultValue);

        CdhJdbcUtils.closeConnection(conn);
        conn = null;


        logger.warn("从属性得到bootstrap.servers=={}", bootstrapServers);
        System.out.println(String.format("从属性得到bootstrap.servers==%s", bootstrapServers));
        return bootstrapServers;
    }

    /**
     * @return server地址：如 192.168.20.148:9092,192.168.20.149:9092,192.168.20.150:9092
     */
    public String getBootsTrapServers2() {
        String serviceName = "kafka";
        String rolesType = "KAFKA_BROKER";
        String portConfigAttr = "";
        String portConfigDefaultValue = "9092";

        String bootstrapServers = getServiceUrl(serviceName, rolesType, portConfigAttr, portConfigDefaultValue);

        logger.warn("bootstrap.servers=={}", bootstrapServers);
        System.out.println(String.format("bootstrap.servers==%s", bootstrapServers));
        return bootstrapServers;
    }

    public void getHiveServer2() {
        String serviceName = "hive";
        String rolesType = "HIVESERVER2";
        String portConfigAttr = "hs2_thrift_address_port";
        String portConfigDefaultValue = "10000";
        conn = CdhJdbcUtils.getConnection();
        List<String> serversList = getServers(serviceName, rolesType, portConfigAttr, portConfigDefaultValue);
        CdhJdbcUtils.closeConnection(conn);
        conn = null;

        for (String server : serversList) {
            logger.warn("hiverserver2=={}", server);
            System.out.println(String.format("hiverserver2==%s", server));
        }
    }

    private String getServiceUrl(String serviceName, String rolesType, String portConfigAttr, String portConfigDefaultValue) {
        conn = CdhJdbcUtils.getConnection();
        List<String> serversList = getServers(serviceName, rolesType, portConfigAttr, portConfigDefaultValue);
        CdhJdbcUtils.closeConnection(conn);
        conn = null;

        String serviceUrl = "";
        for (String server : serversList) {
            if (serviceUrl.isEmpty()) {
                serviceUrl = server;
            } else {
                serviceUrl += "," + server;
            }
        }

        return serviceUrl;
    }

    private List<String> getServers(String serviceName, String rolesType, String portConfigAttr, String portConfigDefaultValue) {
        //获取服务ID
        List<CDHServices> servicesList = getAllServices();
        Long serviceId = getServicesID(servicesList, serviceName);
        if (-1L == serviceId) {
            logger.error("no service which name {}", serviceName);
            return null;
        }

        //获取服务下指定角色对应的主机号
        Set<Long> hostIdSet = new HashSet<>(3);
        List<CDHRoles> rolesList = getServiceRoles(serviceId);
        for (CDHRoles roles : rolesList) {
            if (rolesType.equals(roles.getRoleType())) {
                hostIdSet.add(roles.getHostId());
            }
        }

        //得到主机ip
        Set<String> hostIpSet = new HashSet<>(3);
        Map<Long, CDHHosts> hostsMap = getAllHosts(conn);
        for (Long hostId : hostIdSet) {
            CDHHosts host = hostsMap.get(hostId);
            if (null != host) {
                hostIpSet.add(host.getIpAddr());
            }
        }


        //获取端口
        String configValue = getConfig(serviceId, portConfigAttr, portConfigDefaultValue);

        //拼接成servier:port格式
        List<String> serverList = new ArrayList<>(3);
        for (String hostIp : hostIpSet) {
            String server = hostIp + ":" + configValue;
            serverList.add(server);
        }
        return serverList;
    }

    private Map<Long, CDHHosts> getAllHosts(Connection conn) {
        Map<Long, CDHHosts> hostsMap = new HashMap<>(0);

        try {
            Statement st = conn.createStatement();
            String sql = "select host_id, name, ip_address from hosts";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                long id = rs.getLong("host_id");
                String name = rs.getString("name");
                String address = rs.getString("ip_address");

                CDHHosts cdhHosts = new CDHHosts(id, name, address);
                hostsMap.put(cdhHosts.getHostId(), cdhHosts);

            }

            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //遍历value
        for (CDHHosts host : hostsMap.values()) {
            logger.debug("host info ={}", host);
        }

        return hostsMap;
    }

    private List<CDHServices> getAllServices() {
        List<CDHServices> servicesList = new ArrayList<>(5);

        try {
            Statement st = conn.createStatement();
            String sql = "select service_id, name, service_type from services";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                long serviceId = rs.getLong("service_id");
                String name = rs.getString("name");
                String serviceType = rs.getString("service_type");

                servicesList.add(new CDHServices(serviceId, name, serviceType));
            }

            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //遍历value
        for (CDHServices service : servicesList) {
            logger.debug("service info ={}", service);
        }

        return servicesList;
    }

    private Long getServicesID(List<CDHServices> servicesList, String serviceName) {
        Long serviceId = -1L;
        for (CDHServices service : servicesList) {
            if (serviceName.equals(service.getName())) {
                serviceId = service.getServiceId();
                break;
            }
        }

        return serviceId;
    }

    private List<CDHRoles> getServiceRoles(Long serviceId) {
        List<CDHRoles> rolesList = new ArrayList<>(10);

        try {

            String sql = "select host_id, role_type,service_id from roles where service_id=?";
            PreparedStatement pset_f = conn.prepareStatement(sql);
            pset_f.setLong(1, serviceId);
            ResultSet rs = pset_f.executeQuery();

            while (rs.next()) {
                long hostId = rs.getLong("host_id");
                String roleType = rs.getString("role_type");
                long service_id = rs.getLong("service_id");

                rolesList.add(new CDHRoles(hostId, roleType, service_id));
            }

            rs.close();
            pset_f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //遍历value
        for (CDHRoles roles : rolesList) {
            logger.debug("roles info ={}", roles);
        }

        return rolesList;
    }

    private String getConfig(Long serviceId, String attrName, String defaultValue) {
        if (null == serviceId || null == attrName || attrName.length() <= 0) {
            return defaultValue;
        }

        List<CDHConfig> configList = new ArrayList<>(1);

        try {
            String sql = "select config_id,attr,value from configs where service_id=? and attr=? limit 1";
            PreparedStatement pset_f = conn.prepareStatement(sql);
            pset_f.setLong(1, serviceId);
            pset_f.setString(2, attrName);

            logger.debug("getConfig sql={}", sql);
            ResultSet rs = pset_f.executeQuery();

            while (rs.next()) {
                long config_id = rs.getLong("config_id");
                String attr = rs.getString("attr");
                String value = rs.getString("value");

                configList.add(new CDHConfig(config_id, attr, value));
            }

            rs.close();
            pset_f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //遍历value
        for (CDHConfig config : configList) {
            logger.debug("config info ={}", config);
        }

        if (configList.isEmpty()) {
            return defaultValue;
        } else {
            return configList.get(0).getValue();
        }
    }
}
