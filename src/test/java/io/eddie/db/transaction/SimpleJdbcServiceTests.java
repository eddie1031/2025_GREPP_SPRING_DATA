package io.eddie.db.transaction;

import io.eddie.db.dao.SimpleCrudRepository;
import io.eddie.db.member.Member;
import io.eddie.db.transaction.inner.SimpleJdbcCrudTransactionRepository;
import io.eddie.db.transaction.inner.SimpleJdbcService;
import io.eddie.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SimpleJdbcServiceTests {

    SimpleCrudRepository repository;
    SimpleJdbcService simpleJdbcService;

    @BeforeEach
    void init() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                ConnectionUtil.MysqlDbConnectionConstant.URL,
                ConnectionUtil.MysqlDbConnectionConstant.USERNAME,
                ConnectionUtil.MysqlDbConnectionConstant.PASSWORD
        );
        repository = new SimpleJdbcCrudTransactionRepository(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        simpleJdbcService = new SimpleJdbcService(repository, transactionManager);

    }

    @Test
    @DisplayName("정상 커밋")
    void commit() throws Exception {

        Member saveReq1 = new Member(0, "member100", "member100");
        Member saveReq2 = new Member(0, "member200", "member200");

        simpleJdbcService.logic1(saveReq1, false);
        simpleJdbcService.logic1(saveReq2, false);

    }

    @Test
    @DisplayName("롤백 테스트")
    void rollback() throws Exception {

        Member saveReq1 = new Member(0, "member500", "member500");
        Member saveReq2 = new Member(0, "member600", "member600");

        simpleJdbcService.logic1(saveReq1, true);
        simpleJdbcService.logic1(saveReq2, true);

    }


}