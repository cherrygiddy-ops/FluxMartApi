package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReversalRequestDto{

    @JsonProperty("QueueTimeOutURL")
    private String queueTimeOutURL;

    @JsonProperty("Initiator")
    private String initiator;

    @JsonProperty("Remarks")
    private String remarks;

    @JsonProperty("Occasion")
    private String occasion;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("SecurityCredential")
    private String securityCredential;

    @JsonProperty("RecieverIdentifierType")
    private String recieverIdentifierType;

    @JsonProperty("CommandID")
    private String commandID;

    @JsonProperty("TransactionID")
    private String transactionID;

    @JsonProperty("ReceiverParty")
    private String receiverParty;

    @JsonProperty("ResultURL")
    private String resultURL;
}