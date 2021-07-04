package com.github.kerner1000.etoro.stats.transactionsservice.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransactionsRepository extends CrudRepository<TransactionEntity, Long> {

    @Transactional(readOnly = true)
    List<TransactionEntity> findByInstrumentOrderByPositionId(String instrument);

    @Transactional(readOnly = true)
    List<TransactionEntity> findByInstrumentIsStartingWithOrderByPositionId(String instrument);

    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT t.instrument FROM TransactionEntity t")
    List<String> getAllInstruments();

    @Transactional(readOnly = true)
    List<TransactionEntity> findAllByOrderByPositionId();


}
