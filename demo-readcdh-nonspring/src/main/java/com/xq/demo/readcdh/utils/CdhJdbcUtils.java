package com.xq.demo.readcdh.utils;

import com.xq.demo.readcdh.service.CDHDataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * @author sk-qianxiao
 * @date 2019/12/17
 */
public class CdhJdbcUtils {
    private static Logger logger = LoggerFactory.getLogger(CdhJdbcUtils.class);
    private static String cdhPgurl = "jdbc:postgresql://192.168.20.148:5432,192.168.20.149:5432,192.168.20.150:5432/scm";
    private static String cdhDbUserName = "";
    private static String cdhDbPasswd = "";
    private static boolean isInited = false;

    public static void init(String url, String userName, String passwd) {
        cdhPgurl = String.format("jdbc:postgresql://%s/scm", url);
        cdhDbUserName = userName;
        cdhDbPasswd = passwd;
        isInited = true;
    }


    public static Connection getConnection() {
        if (!isInited) {
            logger.warn("Not inited");
            return null;
        }
        Connection conn = null;

        try {
            Properties props = new Properties();
            props.setProperty("user", cdhDbUserName);
            props.setProperty("password", cdhDbPasswd);
            props.setProperty("ssl", "false");
            conn = DriverManager.getConnection(cdhPgurl, props);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }
    public static void closeConnection(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void test() {
        try {
            Connection conn = getConnection();

            //3.创建statement，跟数据库打交道一定需要这个对象
            Statement st = conn.createStatement();

            //4.执行查询
            String sql = "select host_id, name, ip_address from hosts";
            ResultSet rs = st.executeQuery(sql);

            //5.遍历查询每一条记录
            while (rs.next()) {
                int id = rs.getInt("host_id");
                String name = rs.getString("name");
                String address = rs.getString("ip_address");

                System.out.println("id = " + id + "; name = " + name + "; age = " + address);
            }

            //进行资源释放
            rs.close();
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
