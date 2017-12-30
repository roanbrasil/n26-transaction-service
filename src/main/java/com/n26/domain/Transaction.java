package com.n26.domain;

import lombok.*;

import java.math.BigDecimal;

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
