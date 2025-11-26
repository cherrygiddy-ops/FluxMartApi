package com.fluxmartApi.payments.mpesa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxmartApi.order.OrderNotFoundException;
import com.fluxmartApi.payments.CheckOutService;
import com.fluxmartApi.payments.OrderAlreadyUpdatedException;
import com.fluxmartApi.payments.PaymentException;
import com.fluxmartApi.payments.mpesa.dtos.*;
import com.fluxmartApi.payments.mpesa.repository.StkPushEntriesRepository;
import com.fluxmartApi.payments.mpesa.services.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/m-pesa")
@Slf4j
@RequiredArgsConstructor
public class MpesaController {

    private final CommonServices commonServices;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;
    private final StkPushEntriesRepository stkPushEntriesRepository;
    private final StkPushImpl stkPush;
    private final CheckOutService checkOutService;
    private final C2bImpl c2bimpl;
   private final B2CImpl b2CImpl;
    @GetMapping(path = "/token", produces = "application/json")
    public ResponseEntity<AccessTokenResponse> getAccessToken() {
        return ResponseEntity.ok(commonServices.getAccessToken());
    }

    @GetMapping(path = "/register-url", produces = "application/json")
    public ResponseEntity<RegisterUrlResponse> registerUrl() {
        var response = commonServices.registerUrl();
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/simulate-c2b", produces = "application/json")
    public ResponseEntity<SimulateTransactionResponse> simulateC2BTransaction(@RequestBody SimulateTransactionRequest simulateTransactionRequest) {
        SimulateTransactionResponse simulateTransactionResponse = c2bimpl.simulateC2BAndPersist(simulateTransactionRequest);

        if (simulateTransactionResponse == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(simulateTransactionResponse);
    }

    @PostMapping(path = "/confirmation", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> mpesaValidation(@RequestBody MpesaValidationResponse mpesaValidationResponse) {
        AcknowledgeResponse response = c2bimpl.completeMpesaTransanction(mpesaValidationResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateC2BTransaction")
    public ResponseEntity<?>   updateC2BPayments(@RequestBody InternalTransactionStatusRequest request){
        checkOutService.confirmC2BTransactionUpdateOrder(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Order Updated");
    }


    @GetMapping(path = "/check-account-balance", produces = "application/json")
    public ResponseEntity<CommonSyncResponse> checkAccountBalance() {
        return ResponseEntity.ok(commonServices.checkAccountBalance());
    }

    @PostMapping(path = "/stk-transaction-request", produces = "application/json")
    public ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest) {
        return ResponseEntity.ok(stkPush.initiateStkPushTransaction(internalStkPushRequest));
    }

    @SneakyThrows
    @PostMapping(path = "/stk-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> acknowledgeStkPushResponse(@RequestBody StkPushAsyncResponse callbackRequest) {
        stkPush.saveStkTransactionsUpdateOrderAndPostTransactions(callbackRequest);
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/query-lnm-request", produces = "application/json")
    public ResponseEntity<LNMQueryResponse> getTransactionStatus(@RequestBody InternalLNMRequest internalLNMRequest) {
        return ResponseEntity.ok(stkPush.getTransactionStatus(internalLNMRequest));
    }

    @PutMapping("/updateStkPayments")
    public ResponseEntity<?>   updateStkPayments(){
        checkOutService.confirmStkPushAndUpdateOrder();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Order Updated");
    }

    @PostMapping(path = "/transaction-reversal", produces = "application/json")
    public ResponseEntity<CommonSyncResponse> performTransactionReversal(@RequestBody ReversalRequestDto reversalRequestDto) {
        CommonSyncResponse commonSyncResponse = commonServices.performTransactionReversal(reversalRequestDto);

        if (commonSyncResponse == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(commonSyncResponse);
    }

    @PostMapping(path = "/b2c-transaction", produces = "application/json")
    public ResponseEntity<CommonSyncResponse> performB2CTransaction(@RequestBody InternalB2CTransactionRequest internalB2CTransactionRequest) {
        CommonSyncResponse commonSyncResponse = b2CImpl.performB2CTransaction(internalB2CTransactionRequest);

        if (commonSyncResponse == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(commonSyncResponse);
    }

    @PostMapping(path = "/simulate-transaction-result", produces = "application/json")
    public ResponseEntity<TransactionStatusSyncResponse> getTransactionStatusResult(@RequestBody InternalTransactionStatusRequest internalTransactionStatusRequest) {
        return ResponseEntity.ok(b2CImpl.getB2CTransactionResult(internalTransactionStatusRequest));
    }

    @PostMapping(path = "/transaction-result", produces = "application/json")
public ResponseEntity<AcknowledgeResponse> b2cTransactionAsyncResults(@RequestBody B2CTransactionAsyncResponse b2CTransactionAsyncResponse) {
    AcknowledgeResponse response = b2CImpl.handleB2CTransactionAsyncResults(b2CTransactionAsyncResponse);
    return ResponseEntity.ok(response);
}
    @PostMapping(path = "/b2c-queue-timeout", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> queueTimeout(@RequestBody Object object) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @ExceptionHandler(TransactionNotFound.class)
    public ResponseEntity<?> handleTransactionNotFound (){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("could not find the said transaction try again");
    }
    @ExceptionHandler(OrderAlreadyUpdatedException.class)
    public ResponseEntity<?> handleOrderPaidException (){
        return ResponseEntity.status(HttpStatus.FOUND).body("Order  Already Updated");
    }
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException (){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount is less than Owed");
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFoundException (){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order  Not Found");
    }

}