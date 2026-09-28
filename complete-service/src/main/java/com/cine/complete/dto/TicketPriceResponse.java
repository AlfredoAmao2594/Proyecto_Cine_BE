package com.cine.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Precio de la entrada de cine (1 por compra). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPriceResponse {
    private BigDecimal price;
}
