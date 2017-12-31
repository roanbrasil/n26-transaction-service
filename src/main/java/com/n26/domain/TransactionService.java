package com.n26.domain;

import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;
import org.springframework.http.ResponseEntity;

import java.util.DoubleSummaryStatistics;

/**
 * TransactionService.java
 * Interface class responsible about Service Interface.
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12-30-2017
 */
public interface TransactionService {

    /**
     * Insert transaction in memory no older than 60 seconds
     *
     * @param transaction
     * @throws NoContentTimestampException
     */
    void add(Transaction transaction) throws NoContentTimestampException;

    /**
     * Collect transaction statistics from memory and return just transaction information no older than 60 seconds
     *
     * @return
     * @throws EntityNotFoundException
     */
    Statistics get() throws EntityNotFoundException;
}
