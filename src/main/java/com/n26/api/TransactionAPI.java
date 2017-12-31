package com.n26.api;

import com.n26.domain.Statistics;
import com.n26.domain.Transaction;
import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;

import java.util.DoubleSummaryStatistics;

/**
 * TransactionAPI.java
 * Interface class responsible about Controller Interface.
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12-30-2017
 */
public interface TransactionAPI {

    /**
     * Receive the request with Transaction Body to insert transaction in memory delegating to service
     *
     * @param transaction
     * @throws NoContentTimestampException
     */
    void add(Transaction transaction) throws NoContentTimestampException;

    /**
     * Collect transaction statistics
     *
     * @return
     * @throws EntityNotFoundException
     */
    Statistics get() throws EntityNotFoundException;
}
