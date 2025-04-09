package io.eddie.db.transaction.inner;

import io.eddie.db.dao.SimpleCrudRepository;
import io.eddie.db.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SimpleJdbcService {

    private final SimpleCrudRepository repository;
    private final PlatformTransactionManager transactionManager;

    public void logic1(Member saveReq, boolean isRollback) throws Exception {

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition()
        );

        try {

            Member saved = repository.save(saveReq);

            Optional<Member> memberOptional = repository.findById(saved.getMemberId());
            Member findMember = memberOptional.orElseThrow();

            log.info("findMember.getUsername() = {}", findMember.getUsername());

            if ( isRollback )  {
                transactionManager.rollback(transaction);
                return;
            }

            transactionManager.commit(transaction);

        } catch ( Exception e ) {
            transactionManager.rollback(transaction);
        }

    }


}
