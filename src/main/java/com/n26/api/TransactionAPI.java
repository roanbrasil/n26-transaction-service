package com.n26.api;

import com.n26.domain.Transaction;
import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;

import java.util.DoubleSummaryStatistics;

public interface TransactionAPI {

    void add(Transaction transaction) throws NoContentTimestampException;

    DoubleSummaryStatistics get() throws EntityNotFoundException;
}
