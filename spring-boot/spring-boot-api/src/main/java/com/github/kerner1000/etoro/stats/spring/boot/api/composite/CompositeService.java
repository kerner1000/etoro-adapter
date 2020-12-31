package com.github.kerner1000.etoro.stats.spring.boot.api.composite;

import com.github.kerner1000.etoro.stats.model.PositionGroups;
import com.github.kerner1000.etoro.stats.spring.boot.api.PositionService;
import com.github.kerner1000.etoro.stats.spring.boot.api.TaxonomyService;
import com.github.kerner1000.etoro.stats.spring.boot.api.TransactionService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;

@Api(description = "REST API of the eToro Stats App. Interact with Transactions, Trades, Positions and Taxonomy.")
public interface CompositeService extends TransactionService, PositionService, TaxonomyService {

    @GetMapping(value="/sector-breakdown", produces = "application/json")
    PositionGroups getSectorBreakdown();

}
