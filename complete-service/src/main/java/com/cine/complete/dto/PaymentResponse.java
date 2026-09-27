package com.cine.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String state;      
    private String transactionId;
    private Long orderId;
    private Long operationDate;
    private String message;
    private BigDecimal amount; 
    private String referenceCode;
}