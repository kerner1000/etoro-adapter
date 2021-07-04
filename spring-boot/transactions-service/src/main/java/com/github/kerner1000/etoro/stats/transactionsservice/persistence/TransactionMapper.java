package com.github.kerner1000.etoro.stats.transactionsservice.persistence;

import com.github.kerner1000.etoro.stats.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction entityToApi(TransactionEntity entity);

    @Mappings({
            @Mapping(target = "version", ignore = true)
    })
    TransactionEntity apiToEntity(Transaction api);
}