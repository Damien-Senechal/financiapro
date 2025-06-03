package com.epsi.financiapro.dto;

import com.epsi.financiapro.entity.BudgetItem.BudgetType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetItemCreateDTO {
    @NotNull(message = "Le type est obligatoire")
    private BudgetType type;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    private String description;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;
}