package com.github.kerner1000.etoro.stats.transactionsservice.services;

import com.github.kerner1000.etoro.stats.model.DataIntegrityException;
import com.github.kerner1000.etoro.stats.model.Transaction;
import com.github.kerner1000.etoro.stats.model.TransactionType;
import com.github.kerner1000.etoro.stats.spring.boot.api.TransactionService;
import com.github.kerner1000.etoro.stats.spring.boot.util.exceptions.InvalidInputException;
import com.github.kerner1000.etoro.stats.spring.boot.util.http.ServiceUtil;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionEntity;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionMapper;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class DefaultTransactionService implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTransactionService.class);

    private final ServiceUtil serviceUtil;

    private final TransactionsRepository repository;

    private final TransactionMapper mapper;

    @Autowired
    public DefaultTransactionService(TransactionsRepository repository,
                                     TransactionMapper mapper, ServiceUtil serviceUtil) {
        this.repository = Objects.requireNonNull(repository);
        this.mapper = Objects.requireNonNull(mapper);
        this.serviceUtil = Objects.requireNonNull(serviceUtil);
    }

    @Transactional
    @Override
    public List<Transaction> getTransactions(String instrument) {
        List<TransactionEntity> entities = repository.findByInstrumentIsStartingWithOrderByPositionId(instrument);
        return entities.stream().map(e -> mapper.entityToApi(e)).collect(Collectors.toList());
    }

    public List<Transaction> getOpenTransactions(String instrument) {
        return getOpenTransactions(repository.findByInstrumentIsStartingWithOrderByPositionId(instrument));
    }

    public List<Transaction> getOpenTransactions() {
        return getOpenTransactions(repository.findAllByOrderByPositionId());
    }

    List<Transaction> getOpenTransactions(List<TransactionEntity> supplier) {
        List<Transaction> result = new ArrayList<>();

            final List<TransactionEntity> samePositionEntities = new ArrayList<>();
            final AtomicLong lastPositionId = new AtomicLong(-1);
        supplier.forEach(e -> {
                        //same position
                        if (e.getPositionId() == lastPositionId.get()) {
                            samePositionEntities.add(e);
                        } else {
                            if (samePositionEntities.size() == 1) {
                                if (TransactionType.OPEN_POSITION.equals(samePositionEntities.get(0).getType())) {
                                    // exactly one transaction, -> open transaction
                                    result.add(mapper.entityToApi(samePositionEntities.get(0)));
                                } else {
                                    // closed position
                                }
                            }
                            // new position
                            samePositionEntities.clear();
                            samePositionEntities.add(e);
                            lastPositionId.set(e.getPositionId());
                        }
                    }
            );
            // don't forget last element
            if (samePositionEntities.size() == 1) {
                // exactly one transaction, -> open transaction
                if (TransactionType.OPEN_POSITION.equals(samePositionEntities.get(0).getType())) {
                    result.add(mapper.entityToApi(samePositionEntities.get(0)));
                } else {
                    throw new DataIntegrityException("One position left, but type is not open [" + samePositionEntities.get(0) + "]");
                }
            }

        return result;
    }



    @Override
    public Transaction createTransaction(Transaction body) {
        try {
            TransactionEntity entity = mapper.apiToEntity(body);
            TransactionEntity newEntity = repository.save(entity);
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, ID: " + body.getPositionId());
        }
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
        logger.info("All transactions deleted");
    }
}
