package com.github.kerner1000.etoro.stats.transactionsservice;

import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionEntity;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PersistenceTests {

    @Autowired
    private TransactionsRepository repository;

    private TransactionEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();
    }

    @Test
    public void create01(){
        TransactionEntity entity = new TransactionEntity();
        entity.setPositionId(11);
        savedEntity = repository.save(entity);

        assertEqualsTransaction(entity, savedEntity);
    }

    @Test
    public void findById() {

        TransactionEntity newEntity = new TransactionEntity();
        newEntity.setPositionId(11);
        repository.save(newEntity);

        TransactionEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsTransaction(newEntity, foundEntity);

        assertEquals(1, repository.count());
    }

    @Transactional
    @Test
    public void getAllInstruments() {

        TransactionEntity newEntity = new TransactionEntity();
        newEntity.setPositionId(11);
        newEntity.setInstrument("test");
        repository.save(newEntity);

        newEntity = new TransactionEntity();
        newEntity.setPositionId(12);
        newEntity.setInstrument("test");
        repository.save(newEntity);

        List<String> instruments = repository.getAllInstruments();
            assertEquals(1, instruments.size());

       Iterable<TransactionEntity> transactions = repository.findAll();
        AtomicInteger cnt = new AtomicInteger();
       transactions.forEach(e -> cnt.incrementAndGet());
       assertEquals(2   , cnt.get());



    }

    @Transactional
    @Test
    public void getAllByPositionIdOrderByPositionId() {

        TransactionEntity newEntity3 = new TransactionEntity();
        newEntity3.setPositionId(22);
        newEntity3.setInstrument("test33");
        repository.save(newEntity3);

        TransactionEntity newEntity = new TransactionEntity();
        newEntity.setPositionId(11);
        newEntity.setInstrument("test11");
        repository.save(newEntity);

        TransactionEntity newEntity2 = new TransactionEntity();
        newEntity2.setPositionId(11);
        newEntity2.setInstrument("test11");
        repository.save(newEntity2);

       List<TransactionEntity> instrumentStream = repository.findAllByOrderByPositionId();
            assertEquals(3, instrumentStream.size());

    }

    private void assertEqualsTransaction(TransactionEntity newEntity, TransactionEntity foundEntity) {
        assertEquals(newEntity.getPositionId(), foundEntity.getPositionId());
        assertEquals(newEntity.getVersion(), foundEntity.getVersion());
        assertEquals(newEntity.getAmount(), foundEntity.getAmount());
        assertEquals(newEntity.getAccountBalanceAfter(), foundEntity.getAccountBalanceAfter());
        assertEquals(newEntity.getInstrument(), foundEntity.getInstrument());
        assertEquals(newEntity.getDateTime(), foundEntity.getDateTime());
        assertEquals(newEntity.getType(), foundEntity.getType());
    }
}
