package io.eddie.db.transaction;

import com.zaxxer.hikari.HikariDataSource;
import io.eddie.db.member.Member;
import io.eddie.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

@Slf4j
public class TransactionTests {

    Connection conn;
    PreparedStatement stmt;
    ResultSet rs;

    @AfterEach
    void close() {

        if ( rs != null ) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        if ( stmt != null ) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        if ( conn != null ) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

    }

    @Test
    @DisplayName("Database와 연결시 auto commit 파라미터를 false로 변경해서 auto commit 끄기")
    void auto_commit_off() throws Exception {

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        dataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        dataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

//        dataSource.setAutoCommit(false);

        try {

            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            Member saveReq = new Member(0, "_test", "_test");

            String sql = "insert into member (username, password) values (?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, saveReq.getUsername());
            stmt.setString(2, saveReq.getPassword());

            stmt.executeUpdate();

            conn.commit();

        } catch ( Exception e ) {
            conn.rollback();
        }

    }


}
