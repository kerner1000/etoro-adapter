package com.github.kerner1000.etoro.stats.transactionsservice.services;

import com.github.kerner1000.etoro.stats.model.Transaction;
import com.github.kerner1000.etoro.stats.model.TransactionGroup;
import com.github.kerner1000.etoro.stats.model.TransactionIdMapFactory;
import com.github.kerner1000.etoro.stats.model.TransactionType;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionEntity;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionMapper;
import com.github.kerner1000.etoro.stats.transactionsservice.persistence.TransactionsRepository;
import com.github.kerner1000.etoro.stats.spring.boot.api.TransactionService;
import com.github.kerner1000.etoro.stats.spring.boot.util.exceptions.InvalidInputException;
import com.github.kerner1000.etoro.stats.spring.boot.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DefaultTransactionService implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTransactionService.class);

    private final ServiceUtil serviceUtil;

    private final TransactionsRepository repository;

    private final TransactionMapper mapper;

    @Autowired
    public DefaultTransactionService(TransactionsRepository repository, TransactionMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }


    @Override
    public TransactionGroup getTransactions(String instrument) {
        List<Transaction> result = new ArrayList<>();
        List<TransactionEntity> entities = repository.findTransactionEntitiesByInstrumentIsStartingWith(instrument);
        entities.forEach(e -> result.add(mapper.entityToApi(e)));
        TransactionGroup result2 = new TransactionGroup(result);
        logger.debug("Returning for instrument {}: {}", instrument, result2);
       return result2;
    }

    @Override
    public TransactionGroup getOpenTransactions(String instrument) {
        List<Transaction> result = new ArrayList<>();
        List<TransactionEntity> entities = repository.findTransactionEntitiesByInstrumentIsStartingWith(instrument);

        Map<Number, List<TransactionEntity>> id2EntityMap = new TransactionIdMapFactory<>(entities).getTransactionIdMap();
        for(Map.Entry<Number, List<TransactionEntity>> e : id2EntityMap.entrySet()){
            List<TransactionEntity> value = e.getValue();
            if(value.size() == 1){
                if(TransactionType.OPEN_POSITION.equals(e.getValue().get(0).getType())){
                    result.add(mapper.entityToApi(e.getValue().get(0)));
                } else {
                    // closed position
                }
            } else if(value.size() == 2){
                // open and close, dismiss
            } else {
                logger.warn("Unexpected transaction count for ID: {}, {} elements", e.getKey(), e.getValue().size());
            }
        }

        TransactionGroup result2 = new TransactionGroup(result);
        logger.debug("Returning for instrument {}: {}", instrument, result2);
        return result2;
    }

    @Override
    public TransactionGroup getOpenTransactions() {
        List<Transaction> result = new ArrayList<>();
        Iterable<TransactionEntity> entities = repository.findAll();

        Map<Number, List<TransactionEntity>> id2EntityMap = new LinkedHashMap<>();
        for(TransactionEntity e : entities){
            List<TransactionEntity> value = id2EntityMap.get(e.getTransactionId());
            if(value == null){
                value = new ArrayList<>();
                id2EntityMap.put(e.getTransactionId(), value);
            }
            value.add(e);
        }
        for(Map.Entry<Number, List<TransactionEntity>> e : id2EntityMap.entrySet()){
            List<TransactionEntity> value = e.getValue();
            if(value.size() == 1){
                if(TransactionType.OPEN_POSITION.equals(value.get(0).getType())){
                    result.add(mapper.entityToApi(e.getValue().get(0)));
                } else {
                    // closed position
                }
            } else if(value.size() == 2){
                // open and close, dismiss
            } else {
                logger.warn("Unexpected transaction count for ID: {}, {} elements", e.getKey(), e.getValue().size());
            }
        }

        TransactionGroup result2 = new TransactionGroup(result);
        if(!result2.isEmpty()) {
            result2.setGroupIdentifier(result.stream().findFirst().get().getInstrument());
        }
        logger.debug("Returning all: {}", result2);
        return result2;
    }

    @Override
    public Transaction createTransaction(Transaction body) {
        try {
            TransactionEntity entity = mapper.apiToEntity(body);
            TransactionEntity newEntity = repository.save(entity);

//            logger.debug("Entity created for ID: {}", body.getTransactionId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, ID: " + body.getTransactionId());
        }
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
        logger.debug("All transactions deleted");
    }
}
