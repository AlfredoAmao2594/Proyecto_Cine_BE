package com.cine.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRequest {

    @NotBlank(message = "es obligatorio")
    @Email(message = "no tiene un formato válido")
    private String email;

    @NotBlank(message = "es obligatorio")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "es obligatorio")
    @Size(max = 20)
    private String dni;

    @NotNull(message = "es obligatorio")
    private Long operationDate;

    @NotBlank(message = "es obligatorio")
    @Size(max = 36)
    private String transactionId;

    @Pattern(regexp = "DNI|CE|PASAPORTE", message = "debe ser DNI, CE o PASAPORTE")
    private String documentType; 

    private Long orderId;

    @Valid
    private List<ItemRequest> items;  
}