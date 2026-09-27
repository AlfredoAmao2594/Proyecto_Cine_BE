package com.cine.complete.service;

import com.cine.complete.dto.CompleteRequest;
import com.cine.complete.repository.ResultadoRegistroCompra;

public interface CompleteService {

    ResultadoRegistroCompra registrarCompra(CompleteRequest request, String authorization);
}