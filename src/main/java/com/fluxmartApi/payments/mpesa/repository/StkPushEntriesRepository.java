package com.fluxmartApi.payments.mpesa.repository;


import com.fluxmartApi.payments.mpesa.entities.StkPush_Entries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StkPushEntriesRepository extends JpaRepository<StkPush_Entries, String> {

    //  Find Record By MerchantRequestID or CheckoutRequestID ...
    StkPush_Entries findByMerchantRequestIDOrCheckoutRequestID(String merchantRequestID, String checkoutRequestID);

    // Find Transaction By TransactionId ...
    StkPush_Entries findByTransactionId(String transactionId);

    Optional<StkPush_Entries> findByResultDesc(String resultDesc);
}
