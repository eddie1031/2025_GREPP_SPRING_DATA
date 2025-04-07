package io.eddie.db.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ConnectionUtilTests {

    @Test
    @DisplayName("Database Connection 테스트")
    void connection_test() throws Exception {
        ConnectionUtil.getConnection();
    }


}