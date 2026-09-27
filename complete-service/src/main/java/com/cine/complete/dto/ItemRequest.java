package com.cine.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotNull(message = "es obligatorio")
    private UUID productId;

    @NotNull(message = "es obligatorio")
    @Min(value = 1, message = "debe ser al menos 1")
    @Max(value = 20, message = "no puede ser mayor a 20")
    private Integer quantity;
}