package com.cine.complete.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payu")
public class PayuProperties {
    private String url;
    private String apiKey;
    private String apiLogin;
    private String accountId;
    private String merchantId;
    private String currency = "PEN";
    private String country = "PE";
    private boolean test = true;
    private boolean mock = false;
    private int timeoutSeconds = 30;
}