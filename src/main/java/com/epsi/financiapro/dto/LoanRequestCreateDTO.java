package com.epsi.financiapro.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestCreateDTO {
    @NotNull(message = "L'ID du prêteur est obligatoire")
    private Long lenderId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "10.00", message = "Le montant minimum est de 10€")
    @DecimalMax(value = "10000.00", message = "Le montant maximum est de 10000€")
    private BigDecimal montant;

    @NotNull(message = "Le taux d'intérêt est obligatoire")
    @DecimalMin(value = "0.00", message = "Le taux d'intérêt ne peut pas être négatif")
    @DecimalMax(value = "20.00", message = "Le taux d'intérêt maximum est de 20%")
    private BigDecimal interet;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée minimum est de 1 mois")
    @Max(value = 60, message = "La durée maximum est de 60 mois")
    private Integer duree;
}