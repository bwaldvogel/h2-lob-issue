package de.bwaldvogel;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class H2LobIssueTest {

    private static final Logger log = LoggerFactory.getLogger(H2LobIssueTest.class);

    private static final int NUM_THREADS = 10;

    private static final int NUM_MODIFICATIONS_PER_THREAD = 10;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    private void doInTransaction(final Consumer<EntityManager> consumer) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                consumer.accept(entityManager);
            }
        });
    }

    @Test
    public void testConcurrentLobModification() throws Throwable {

        final LobEntity entity = new LobEntity();
        entity.setLob("");

        doInTransaction(em -> em.persist(entity));

        log.info("inserted entity with id {}", entity.getId());

        Runnable runnable = () -> {
            for (int i = 0; i < NUM_MODIFICATIONS_PER_THREAD; i++) {
                doInTransaction(em -> {
                    LobEntity lobEntity = em.find(LobEntity.class, entity.getId());
                    lobEntity.setLob(lobEntity.getLob() + "_");
                    em.merge(lobEntity);
                });
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            CompletionService<Void> completionService = new ExecutorCompletionService<Void>(service);

            for (int i = 0; i < NUM_THREADS; i++) {
                completionService.submit(runnable, null);
            }
            for (int i = 0; i < NUM_THREADS; i++) {
                completionService.take().get();
            }

        } finally {
            service.shutdown();
            service.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
