package io.eddie.db.query;

import com.zaxxer.hikari.HikariDataSource;
import io.eddie.db.dao.SimpleCrudRepository;
import io.eddie.db.dao.SimpleJdbcCrudRepository;
import io.eddie.db.member.Member;
import io.eddie.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class SimpleJdbcCrudRepositoryTests {

    SimpleCrudRepository repository;

    @BeforeEach
    void init() {

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        dataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        dataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        repository = new SimpleJdbcCrudRepository(dataSource);

    }

    @Test
    @DisplayName("save Test")
    void save_test() throws Exception {

        String randomUsrStr = "USER_" + ((int) (Math.random() * 1_000_000));
        log.info("randomUsrStr = {}", randomUsrStr);

        Member saveRequest = new Member(0, randomUsrStr, randomUsrStr);
        Member savedMember = repository.save(saveRequest);

        log.info("savedMember = {}", savedMember);
        assertThat(savedMember.getMemberId()).isNotEqualTo(0);

    }

    @Test
    @DisplayName("read test ok")
    void read_test_ok() throws Exception {

        int availableIdx = 1;

        Optional<Member> memberOptional = repository.findById(availableIdx);

        boolean result = memberOptional.isPresent();
        assertThat(result).isTrue();

        Member findMember = memberOptional.get();

        assertThat(findMember).isNotNull();
        assertThat(findMember.getMemberId()).isEqualTo(availableIdx);

        log.info("findMember = {}", findMember);

    }

    @Test
    @DisplayName("read test ng")
    void read_test_ng() throws Exception {

        int unavailableIdx = 9999;

        Optional<Member> memberOptional = repository.findById(unavailableIdx);

        boolean result = memberOptional.isPresent();
        assertThat(result).isFalse();

        assertThatThrownBy(
                () -> {
                    memberOptional.get();
                }
        ).isInstanceOf(NoSuchElementException.class);

    }



}