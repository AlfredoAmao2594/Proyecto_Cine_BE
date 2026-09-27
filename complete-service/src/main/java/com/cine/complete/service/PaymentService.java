package com.cine.complete.service;

import com.cine.complete.dto.DatosDispositivo;
import com.cine.complete.dto.PaymentRequest;
import com.cine.complete.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse procesarPago(PaymentRequest request, DatosDispositivo dispositivo, String authorization);
}