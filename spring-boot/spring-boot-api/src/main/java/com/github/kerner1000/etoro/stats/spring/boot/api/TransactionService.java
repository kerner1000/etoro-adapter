package com.github.kerner1000.etoro.stats.spring.boot.api;

import com.github.kerner1000.etoro.stats.model.Transaction;
import com.github.kerner1000.etoro.stats.model.TransactionGroup;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

public interface TransactionService {

    @GetMapping(value="/transactions/{instrument}", produces = "application/json")
    TransactionGroup getTransactions(@PathVariable String instrument);

    @GetMapping(value="/open-transactions/{instrument}", produces = "application/json")
    TransactionGroup getOpenTransactions(@PathVariable String instrument);

    @GetMapping(value="/open-transactions", produces = "application/json")
    TransactionGroup getOpenTransactions();

    @ApiOperation(
            value = "${api.transaction.create.description}",
            notes = "${api.transaction.create.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value    = "/transaction",
            consumes = "application/json",
            produces = "application/json")
    Transaction createTransaction(@RequestBody Transaction body);

    @DeleteMapping(value = "/delete-all-transactions")
    void deleteAll();

}
