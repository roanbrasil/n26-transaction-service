package com.n26.domain;

import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;
import org.springframework.http.ResponseEntity;

import java.util.DoubleSummaryStatistics;

public interface TransactionService {

    void add(Transaction transaction) throws NoContentTimestampException;

    DoubleSummaryStatistics get() throws EntityNotFoundException;
}
