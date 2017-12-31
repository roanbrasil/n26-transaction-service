package com.n26.domain;

import lombok.*;

import java.math.BigDecimal;

/**
 * Transaction.java
 * Pojo responsible to get Transaction
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12/30/2017
 */
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Transaction {

    private Double amount;

    private long timestamp;
}
