package com.quick.jdbc.service;

import com.quick.jdbc.model.CDHConfig;
import com.quick.jdbc.model.CDHHosts;
import com.quick.jdbc.model.CDHRoles;
import com.quick.jdbc.model.CDHServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author sk-qianxiao
 * @date 2019/12/11
 */
@Service
public class CDHDataBaseService {
    private static Logger logger = LoggerFactory.getLogger(CDHDataBaseService.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * @return server地址：如 192.168.20.148:2181,192.168.20.149:2181,192.168.20.150:2181
     */
    public String getZooKeeperServers() {
        String serviceName = "zookeeper";
        String rolesType = "SERVER";
        String portConfigAttr = "";
        String portConfigDefaultValue = "2181";


        List<String> serversList = getServers(serviceName, rolesType, portConfigAttr, portConfigDefaultValue);
        String servers = "";
        for (String server : serversList) {
            if (servers.isEmpty()) {
                servers = server;
            } else {
                servers += "," + server;
            }
        }

        logger.warn("zookeeper servers=={}", servers);
        return servers;
    }

    public void getHiveServer2() {
        String serviceName = "hive";
        String rolesType = "HIVESERVER2";
        String portConfigAttr = "hs2_thrift_address_port";
        String portConfigDefaultValue = "10000";
        List<String> serversList = getServers(serviceName, rolesType, portConfigAttr, portConfigDefaultValue);

        for (String server : serversList) {
            logger.warn("hiverserver2=={}", server);
        }
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
        Map<Long, CDHHosts> hostsMap = getAllHosts();
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

    private Map<Long, CDHHosts> getAllHosts() {
        Map<Long, CDHHosts> hostsMap = new HashMap<>(0);

        jdbcTemplate.query(
                "select host_id, name, ip_address from hosts", new Object[]{},
                (rs, rowNum) -> new CDHHosts(rs.getLong("host_id"), rs.getString("name"), rs.getString("ip_address"))
        ).forEach(host -> hostsMap.put(host.getHostId(), host));


        //遍历value
        for (CDHHosts host : hostsMap.values()) {
            logger.debug("host info ={}", host);
        }

        return hostsMap;
    }

    private List<CDHServices> getAllServices() {
        List<CDHServices> servicesList = new ArrayList<>(5);

        jdbcTemplate.query(
                "select service_id, name, service_type from services", new Object[]{},
                (rs, rowNum) -> new CDHServices(rs.getLong("service_id"), rs.getString("name"), rs.getString("service_type"))
        ).forEach(service -> servicesList.add(service));

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

        jdbcTemplate.query(
                "select host_id, role_type,service_id from roles where service_id=?", new Object[]{serviceId},
                (rs, rowNum) -> new CDHRoles(rs.getLong("host_id"), rs.getString("role_type"), rs.getLong("service_id"))
        ).forEach(roles -> rolesList.add(roles));

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
        jdbcTemplate.query(
                "select config_id,attr,value from configs where service_id=? and attr=? limit 1", new Object[]{serviceId, attrName},
                (rs, rowNum) -> new CDHConfig(rs.getLong("config_id"), rs.getString("attr"), rs.getString("value"))
        ).forEach(config -> configList.add(config));

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
