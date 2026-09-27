package com.cine.complete.client.payu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayuRequest {

    private String language;
    private String command;
    private Merchant merchant;
    private Transaction transaction;
    private boolean test;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Merchant {
        private String apiKey;
        private String apiLogin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Transaction {
        private Order order;
        private Persona payer;
        private CreditCard creditCard;
        private Map<String, Object> extraParameters;
        private String type;                        
        private String paymentMethod;               
        private String paymentCountry;              
        private String deviceSessionId;
        private String ipAddress;
        private String cookie;
        private String userAgent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Order {
        private String accountId;
        private String referenceCode;
        private String description;
        private String language;
        private String signature;
        private Map<String, Valor> additionalValues;
        private Persona buyer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Valor {
        private BigDecimal value;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Persona {
        private String fullName;
        private String emailAddress;
        private String contactPhone;
        private String dniNumber;
        private Direccion shippingAddress;
        private Direccion billingAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Direccion {
        private String street1;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditCard {
        private String number;
        private String securityCode;
        private String expirationDate;
        private String name;          
    }
}