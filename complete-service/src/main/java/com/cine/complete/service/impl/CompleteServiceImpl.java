package com.cine.complete.service.impl;

import com.cine.complete.dto.CompleteRequest;
import com.cine.complete.entity.Compra;
import com.cine.complete.entity.CompraDetalle;
import com.cine.complete.entity.LogPago;
import com.cine.complete.exception.BusinessException;
import com.cine.complete.repository.CompraRepository;
import com.cine.complete.repository.LogPagoRepository;
import com.cine.complete.repository.ResultadoRegistroCompra;
import com.cine.complete.service.CalculadoraPedido;
import com.cine.complete.service.CompleteService;
import com.cine.complete.service.PedidoCalculado;
import com.cine.complete.util.DocumentoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteServiceImpl implements CompleteService {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final String APROBADO = "APPROVED";

    private final CompraRepository compraRepository;
    private final LogPagoRepository logPagoRepository;
    private final CalculadoraPedido calculadoraPedido;

    @Override
    @Transactional
    public ResultadoRegistroCompra registrarCompra(CompleteRequest request, String authorization) {
        log.info("Registrando compra de la transacción {}", request.getTransactionId());

        // 1. Seguridad: solo se registran pagos que PayU realmente aprobó (quedaron en log_pago)
        LogPago pago = logPagoRepository.buscarPorTransaccion(request.getTransactionId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "La transacción no existe"));
        if (!APROBADO.equals(pago.getEstado())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La transacción no fue aprobada por PayU");
        }

        String tipoDocumento = request.getDocumentType() != null ? request.getDocumentType() : "DNI";
        if (!DocumentoUtils.esValido(tipoDocumento, request.getDni())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El número de documento no es válido para " + tipoDocumento);
        }

        // 2. Si llegan productos, deben sumar exactamente lo que se cobró
        PedidoCalculado pedido = null;
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            pedido = calculadoraPedido.calcular(request.getItems(), authorization);
            if (pedido.getTotal().compareTo(pago.getMonto()) != 0) {
                log.warn("Monto no coincide: productos S/ {} vs pagado S/ {}", pedido.getTotal(), pago.getMonto());
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Los productos no coinciden con el monto pagado");
            }
        }

        // 3. Cabecera: el SP devuelve el código "0" que pide el reto
        Compra compra = Compra.builder()
                .correo(request.getEmail())
                .nombreCompleto(request.getName())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(request.getDni())
                .idTransaccion(request.getTransactionId())
                .idOrdenPayu(request.getOrderId())
                .fechaOperacion(LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getOperationDate()), ZONA_LIMA))
                .montoTotal(pago.getMonto())   // el monto real cobrado, no uno enviado por el front
                .build();

        ResultadoRegistroCompra resultado = compraRepository.registrarCompra(compra);

        switch (resultado.getCodigo()) {
            case ResultadoRegistroCompra.REGISTRADA:
                if (pedido != null) {
                    guardarDetalle(resultado, pedido);
                }
                log.info("Compra {} registrada", resultado.getIdCompra());
                return resultado;
            case ResultadoRegistroCompra.DUPLICADA:
                log.warn("La transacción {} ya estaba registrada (compra {})",
                        request.getTransactionId(), resultado.getIdCompra());
                return resultado;
            default:
                throw new BusinessException(HttpStatus.BAD_REQUEST, resultado.getMensaje());
        }
    }

    private void guardarDetalle(ResultadoRegistroCompra resultado, PedidoCalculado pedido) {
        pedido.getLineas().forEach(linea -> compraRepository.registrarDetalle(CompraDetalle.builder()
                .idCompra(resultado.getIdCompra())
                .idProducto(linea.getProductId())
                .nombreProducto(linea.getNombre())
                .cantidad(linea.getCantidad())
                .precioUnitario(linea.getPrecioUnitario())
                .build()));
    }
}