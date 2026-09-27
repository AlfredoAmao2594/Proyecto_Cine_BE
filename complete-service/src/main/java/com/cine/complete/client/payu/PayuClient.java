package com.cine.complete.client.payu;

public interface PayuClient {

    PayuResponse enviarPago(PayuRequest request);
}
