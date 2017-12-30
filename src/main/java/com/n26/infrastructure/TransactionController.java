package com.n26.infrastructure;

import com.n26.api.TransactionAPI;
import com.n26.domain.Transaction;
import com.n26.domain.TransactionService;
import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.DoubleSummaryStatistics;

@Slf4j
@CrossOrigin
@RestController
public class TransactionController implements TransactionAPI {

    private final TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @Override
    @PostMapping(value = "/transactions", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody Transaction transaction) throws NoContentTimestampException {
        long startTime = System.currentTimeMillis();
        this.service.add(transaction);
        log.info("Added Transactions by transaction param [transaction={}, elapsedTime={}]",
                transaction.toString(), elapsedTimeSince(startTime));

    }

    @Override
    @GetMapping(value = "/statistics", produces = "application/json")
    public DoubleSummaryStatistics get() throws EntityNotFoundException {
        long startTime = System.currentTimeMillis();
        DoubleSummaryStatistics stats = this.service.get();
        log.info("Collect Transactions Statistics [statistics={}, elapsedTime={}]",
                stats.toString(), elapsedTimeSince(startTime));
        return stats;
    }

    private long elapsedTimeSince(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
