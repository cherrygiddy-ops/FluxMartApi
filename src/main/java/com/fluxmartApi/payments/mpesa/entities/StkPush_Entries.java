package com.fluxmartApi.payments.mpesa.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity()
@Table(name = "stk_push_entries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StkPush_Entries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Integer internalId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "merchant_request_id")
    private String merchantRequestID;

    @Column(name = "checkout_request_id")
    private String checkoutRequestID;

    @Column(name = "entry_date")
    private Date entryDate;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "result_desc")
    private String resultDesc;

    @Column(name = "mpesa_receipt_number")
    private String mpesaReceiptNumber;

    @Column(name = "raw_callback_payload_response")
    private String rawCallbackPayloadResponse;
}