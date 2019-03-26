package com.n26.infrastructure;

import com.n26.domain.Statistics;
import com.n26.domain.Transaction;
import com.n26.domain.TransactionService;
import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TransactionController.java
 * Controller responsible to receive http request to transaction events.
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12-30-2017
 */
@Slf4j
@CrossOrigin
@RestController
public class TransactionController {

    private final TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }


    @PostMapping(value = "/transactions", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody Transaction transaction) throws NoContentTimestampException {
        long startTime = System.currentTimeMillis();
        this.service.add(transaction);
        log.info("Added Transactions by transaction param [transaction={}, elapsedTime={}]",
                transaction.toString(), elapsedTimeSince(startTime));

    }

    @GetMapping(value = "/statistics", produces = "application/json")
    public ResponseEntity<Statistics> get() throws EntityNotFoundException {
        long startTime = System.currentTimeMillis();
        Statistics stats = this.service.get();
        log.info("Collect Transactions Statistics [statistics={}, elapsedTime={}]",
                stats.toString(), elapsedTimeSince(startTime));
        return ResponseEntity.ok().body(stats);
    }

    /**
     * Calculate de range of time between the begin and end of requisition to set up in log
     *
     * @param startTime
     * @return long
     */
    private long elapsedTimeSince(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
