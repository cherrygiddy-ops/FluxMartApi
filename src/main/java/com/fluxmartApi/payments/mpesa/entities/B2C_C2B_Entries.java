package com.fluxmartApi.payments.mpesa.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "b2c_c2b_entries")
@AllArgsConstructor
@NoArgsConstructor
public class B2C_C2B_Entries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Integer  internalId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "bill_ref_number")
    private String billRefNumber;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "originator_conversation_id")
    private String originatorConversationId;

    @Column(name = "entry_date")
    private Date entryDate;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "raw_callback_payload_response",columnDefinition = "TEXT")
    private Object rawCallbackPayloadResponse;
}