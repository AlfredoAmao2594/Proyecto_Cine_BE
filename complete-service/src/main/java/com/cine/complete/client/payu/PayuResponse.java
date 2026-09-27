package com.cine.complete.client.payu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayuResponse {

    public static final String CODE_SUCCESS = "SUCCESS";
    public static final String STATE_APPROVED = "APPROVED";

    private String code;
    private String error;
    private TransactionResponse transactionResponse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionResponse {
        private Long orderId;
        private String transactionId;
        private String state;
        private String responseCode;
        private String responseMessage;
        private String paymentNetworkResponseErrorMessage;
        private Long operationDate;   // milisegundos desde 1970
    }
}