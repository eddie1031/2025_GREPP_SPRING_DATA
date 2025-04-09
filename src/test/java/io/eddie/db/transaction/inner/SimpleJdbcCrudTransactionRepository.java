package io.eddie.db.transaction.inner;

import io.eddie.db.dao.SimpleCrudRepository;
import io.eddie.db.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@RequiredArgsConstructor
public class SimpleJdbcCrudTransactionRepository implements SimpleCrudRepository {

    private final DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    @Override
    public Member save(Member member) throws SQLException {

        String sql = "insert into member (username, password) values (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();

            if ( rs.next() ) {
                int idx = rs.getInt(1);
                member.setMemberId(idx);
            }

            return member;

        } catch ( SQLException e ) {
            throw e;
        } finally {
            closeConnection(conn, pstmt, rs);
        }

    }

    @Override
    public Optional<Member> findById(Integer id) throws SQLException {

        String sql = "select * from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                Member findMember = new Member(
                        resultSet.getInt("member_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                return Optional.of(findMember);
            } else {
                return Optional.empty();
            }

        } catch ( SQLException e ) {
            throw e;
        } finally {
            closeConnection(connection, preparedStatement, resultSet);
        }

    }

    @Override
    public void update(Member member) {

        String sql = "update member set password = ? where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, member.getPassword());
            preparedStatement.setInt(2, member.getMemberId());

            preparedStatement.executeUpdate();

        } catch ( SQLException e ) {

        } finally {
            closeConnection(connection, preparedStatement, null);
        }

    }

    @Override
    public void remove(Integer id) throws SQLException {

        String sql = "delete from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

        } catch ( SQLException e ) {
            throw e;
        } finally {
            closeConnection(connection, preparedStatement, null);
        }

    }

}
