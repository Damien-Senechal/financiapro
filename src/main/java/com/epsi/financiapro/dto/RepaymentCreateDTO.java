package com.epsi.financiapro.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepaymentCreateDTO {
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "1.00", message = "Le montant minimum est de 1€")
    private BigDecimal montant;

    private String commentaire;
}