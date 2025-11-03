package com.fluxmartApi.payments.mpesa.repository;

import com.fluxmartApi.payments.mpesa.entities.B2C_C2B_Entries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface B2CC2BEntriesRepository extends JpaRepository<B2C_C2B_Entries, String> {

    // Find Record By ConversationID or OriginatorConversationID ...
    Optional<B2C_C2B_Entries> findByConversationIdOrOriginatorConversationId(String conversationId, String originatorConversationId);

    // Find Transaction By TransactionId ....
    Optional<B2C_C2B_Entries> findByTransactionId(String transactionId);

    //List<B2C_C2B_Entries> findByBillRefNumber(String billRefNumber);
    List<B2C_C2B_Entries> findByBillRefNumber(String billRefNumber);
}
