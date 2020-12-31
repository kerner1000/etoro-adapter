package com.github.kerner1000.etoro.stats.transactionsservice.persistence;

import com.github.kerner1000.etoro.stats.model.TransactionType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransactionsRepository extends CrudRepository<TransactionEntity, Integer> {

    @Transactional(readOnly = true)
    List<TransactionEntity> findByInstrument(String instrument);

    @Transactional(readOnly = true)
    List<TransactionEntity> findTransactionEntitiesByInstrumentIsStartingWith(String instrument);

    @Transactional(readOnly = true)
    List<TransactionEntity> findTransactionEntitiesByTypeEqualsAndInstrumentIsStartingWith(TransactionType type, String instrument);

}
