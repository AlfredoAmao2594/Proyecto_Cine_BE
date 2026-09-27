package com.cine.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "es obligatorio")
    @Pattern(regexp = "\\d{16}", message = "debe tener 16 dígitos")
    private String cardNumber;

    @NotBlank(message = "es obligatorio")
    @Pattern(regexp = "\\d{4}/(0[1-9]|1[0-2])", message = "debe tener el formato YYYY/MM")
    private String expirationDate;

    @NotBlank(message = "es obligatorio")
    @Pattern(regexp = "\\d{3,4}", message = "debe tener 3 o 4 dígitos")
    private String cvv;

    @NotBlank(message = "es obligatorio")
    @Size(max = 100)
    private String cardHolderName;

    @NotBlank(message = "es obligatorio")
    @Email(message = "no tiene un formato válido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "es obligatorio")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "es obligatorio")
    @Pattern(regexp = "DNI|CE|PASAPORTE", message = "debe ser DNI, CE o PASAPORTE")
    private String documentType;

    @NotBlank(message = "es obligatorio")
    @Size(max = 20)
    private String documentNumber;

    @NotEmpty(message = "el carrito está vacío")
    @Valid
    private List<ItemRequest> items;

    @Override
    public String toString() {
        return "PaymentRequest(email=" + email + ", documentType=" + documentType + ", items=" + items + ")";
    }
}