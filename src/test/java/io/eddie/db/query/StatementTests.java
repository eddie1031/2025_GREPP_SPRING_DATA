package io.eddie.db.query;

import io.eddie.db.member.Member;
import io.eddie.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class StatementTests {

    Connection conn;
    PreparedStatement pstmt;
    Statement stmt;
    ResultSet rs;

    @BeforeEach
    void init() {
        conn = ConnectionUtil.getConnection();
    }

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

        if ( pstmt != null ) {
            try {
                pstmt.close();
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

        Member user1 = genMember("member", "member");
        Member user1_ng = genMember("member", "1234");

        // 로그인이 되었다면 -> row가 조회 되어야 함
        String sql1 = genSelectQuery(user1);
        String sql2 = genSelectQuery(user1_ng);

        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql1);

        Member findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getMemberId()).isEqualTo(2);
        assertThat(findMember.getUsername()).isEqualTo("member");
        assertThat(findMember.getPassword()).isEqualTo("member");

        rs.close();

        rs = stmt.executeQuery(sql2);

        findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isNull();
        assertThat(findMember.getPassword()).isNull();

    }

    @Test
    @DisplayName("Statement Test, SQL Injection")
    void statement_test() throws Exception {

        // SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = 'admin' AND m.password = '' or '' = ''
        Member admin = genMember("admin", "' or '' = '");
        String sql = genSelectQuery(admin);

        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        Member findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isEqualTo("admin");
        assertThat(findMember.getPassword()).isEqualTo("admin");

    }

    @Test
    @DisplayName("test")
    void _test() throws Exception {

        // SELECT m.member_id, m.username, m.password FROM member as m
        // WHERE m.username = ? AND m.password = ?
        Member unsafeAttempt = genMember("admin", "' or '' = '");

        String sql = "SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = ? AND m.password = ?";

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, unsafeAttempt.getUsername());
        pstmt.setString(2, unsafeAttempt.getPassword());

        rs = pstmt.executeQuery();

        Member findMember = new Member();

        if ( rs.next() ) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isNull();
        assertThat(findMember.getPassword()).isNull();

    }


    private static String genSelectQuery(Member member) {
        return "SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = '%s' AND m.password = '%s'".formatted(member.getUsername(), member.getPassword());
    }

    private static String genInsertQuery(Member member) {
        return "INSERT INTO member (username, password) values ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    private static Member genMember(String username, String password) {
        return new Member(0, username, password);
    }

}
