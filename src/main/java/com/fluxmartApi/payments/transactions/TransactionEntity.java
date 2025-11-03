package com.fluxmartApi.payments.transactions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

}
