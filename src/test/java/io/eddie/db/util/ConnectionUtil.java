package io.eddie.db.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionUtil {

    public static class MysqlDbConnectionConstant {
        public static final String URL = "jdbc:mysql://localhost:3306/grepp_jdbc";
        public static final String USERNAME = "happy";
        public static final String PASSWORD = "day";
    }

    public static class H2DbConnectionConstant {
        public static final String URL = "jdbc:h2:./grepp";
        public static final String USERNAME = "sa";
        public static final String PASSWORD = "";
    }

    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection(
                    MysqlDbConnectionConstant.URL
                    , MysqlDbConnectionConstant.USERNAME
                    , MysqlDbConnectionConstant.PASSWORD
            );
/*

            Connection connection = DriverManager.getConnection(
                    H2DbConnectionConstant.URL
                    , H2DbConnectionConstant.USERNAME
                    , H2DbConnectionConstant.PASSWORD
            );
*/

            log.info("Connection = {} ", connection);

            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
