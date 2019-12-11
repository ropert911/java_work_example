package com.quick.jdbc;

import com.quick.jdbc.entity.CDHHosts;
import com.quick.jdbc.repository.CDHHostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


/**
 * @Author: wangxc
 * @GitHub: https://github.com/vector4wang
 * @CSDN: http://blog.csdn.net/qqhjqs?viewmode=contents
 * @BLOG: http://vector4wang.tk
 * @wxid: BMHJQS
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CDHHostRepository cdhHostRepository;

    @Value("${spring.datasource.url}")
    private String pgsqlUrl;
    @Value("${spring.datasource.username}")
    private String pgsqlUserName;
    @Value("${spring.datasource.password}")
    private String pgsqlPass;
    @Value("${spring.datasource.driver-class-name}")
    private String pgsqlDriver;

    @Override
    public void run(String... strings) throws Exception {
        log.info("Query Data");
        int method = 2;
        //下面几种方法都可以
        switch (method) {
            case 1: {
                String url = pgsqlUrl;
                Properties props = new Properties();
                props.setProperty("user", pgsqlUserName);
                props.setProperty("password", pgsqlPass);
                props.setProperty("ssl", "false");
                Connection conn = DriverManager.getConnection(url, props);

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
                conn.close();
                st.close();
                rs.close();

//                String url = "jdbc:postgresql://192.168.20.149:5432,192.168.20.150:5432/scm";
//                Connection conn = DriverManager.getConnection(url);
            }
            break;
            case 2:
                //查询,本方法的问题是没有找到如何支持 url中有多个host的方法
                jdbcTemplate.query(
                        "select host_id, name, ip_address from hosts", new Object[]{},
                        (rs, rowNum) -> new CDHHosts(rs.getLong("host_id"), rs.getString("name"), rs.getString("ip_address"))
                ).forEach(customer -> log.info(customer.toString()));
                break;
            case 3:
                List<CDHHosts> list = cdhHostRepository.getAll();
                list.forEach(item -> log.info(item.toString()));
                break;
            default:
                break;
        }
    }
}
