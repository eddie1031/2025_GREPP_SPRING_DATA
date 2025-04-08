package io.eddie.db.query;

import io.eddie.db.member.Member;
import io.eddie.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class StatementTests {

    Connection conn;
    Statement stmt;

    @BeforeEach
    void init() {
        conn = ConnectionUtil.getConnection();
    }

    @AfterEach
    void close() {

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
    @DisplayName("JDBC, 회원가입")
    void insert_test() throws Exception {

        Member admin = genMember("admin", "admin");
        Member member = genMember("member", "member");

        String sql1 = genInsertQuery(admin);
        String sql2 = genInsertQuery(member);

        stmt = conn.createStatement();

        int rows = stmt.executeUpdate(sql1);
        log.info("적용된 row = {}", rows);

        rows = stmt.executeUpdate(sql2);
        log.info("적용된 row = {}", rows);

    }

    @Test
    @DisplayName("JDBC, 로그인")
    void selectTest() throws Exception {



    }

    private static String genInsertQuery(Member member) {
        return "INSERT INTO member (username, password) values ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    private static Member genMember(String username, String password) {
        return new Member(0, username, password);
    }

}
