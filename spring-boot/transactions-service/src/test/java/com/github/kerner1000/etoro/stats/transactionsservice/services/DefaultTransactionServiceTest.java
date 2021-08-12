package com.github.kerner1000.etoro.stats.transactionsservice.services;

import com.github.kerner1000.etoro.stats.model.Transaction;
import com.github.kerner1000.etoro.stats.model.TransactionType;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionEntity;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionMapper;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DefaultTransactionServiceTest {

    @Autowired
    DefaultTransactionService transactionService;

    @Autowired
    TransactionRepository repository;

    @Autowired
    TransactionMapper mapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @AfterEach
    void tearDown() {
    }

    @Transactional
    @Test
    public void testGetOpenTransactions01() {
        TransactionEntity entity = new TransactionEntity();
        entity.setPositionId(11);
        entity.setInstrument("instrument");
        entity.setType(TransactionType.OPEN_POSITION);
        repository.save(entity);

        transactionService.getOpenTransactions("instrument");


    }

    @Transactional
    @Test
    public void testGetOpenTransactions02() {
        TransactionEntity entity = new TransactionEntity();
        entity.setPositionId(11);
        entity.setInstrument("instrument");
        entity.setType(TransactionType.OPEN_POSITION);
        TransactionEntity savedEntity = repository.save(entity);


        List<Transaction> list = transactionService.getOpenTransactions("instrument");

        assertEquals(1, list.size());

        assertEquals(mapper.entityToApi(entity), list.get(0));
    }

    @Transactional
    @Test
    public void testGetOpenTransactions03() {
        TransactionEntity entity = new TransactionEntity();
        entity.setPositionId(11);
        entity.setInstrument("instrument");
        entity.setType(TransactionType.OPEN_POSITION);
        repository.save(entity);

        entity = new TransactionEntity();
        entity.setPositionId(11);
        entity.setInstrument("instrument");
        entity.setType(TransactionType.CLOSE_POSITION);
        repository.save(entity);


        List<Transaction> list = transactionService.getOpenTransactions("instrument");

        assertEquals(0, list.size());
    }

    @Transactional
    @Test
    public void testGetTransactions01() {
        TransactionEntity entity = new TransactionEntity();
        entity.setPositionId(11);
        entity.setInstrument("instrument");
        repository.save(entity);


        List<Transaction> list = transactionService.getTransactions("instrument");

        assertEquals(1, list.size());

        assertEquals(mapper.entityToApi(entity), list.get(0));
    }
}
