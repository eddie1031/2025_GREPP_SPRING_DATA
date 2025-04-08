package io.eddie.db.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ConnectionUtilTests {

    @Test
    @DisplayName("Database Connection 테스트")
    void connection_test() throws Exception {

        Connection conn = ConnectionUtil.getConnection();

        log.info("conn = {}", conn);
        conn.close();

    }

    @Test
    @DisplayName("Abstraction")
    void test_1() throws Exception {

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                ConnectionUtil.MysqlDbConnectionConstant.URL,
                ConnectionUtil.MysqlDbConnectionConstant.USERNAME,
                ConnectionUtil.MysqlDbConnectionConstant.PASSWORD
        );

        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        log.info("conn1 = {}", conn1);
        log.info("conn2 = {}", conn2);

        conn1.close();
        conn2.close();

    }

    @Test
    @DisplayName("hikari")
    void hikari_test() throws Exception {

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        hikariDataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        hikariDataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        hikariDataSource.setMaximumPoolSize(5);

        Connection conn1 = hikariDataSource.getConnection();
        Connection conn2 = hikariDataSource.getConnection();
        Connection conn3 = hikariDataSource.getConnection();

        Thread.sleep(10000);

    }



}