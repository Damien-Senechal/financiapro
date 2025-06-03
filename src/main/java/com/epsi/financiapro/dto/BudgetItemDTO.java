package com.epsi.financiapro.dto;

import com.epsi.financiapro.entity.BudgetItem.BudgetType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetItemDTO {
    private Long id;
    private BudgetType type;
    private BigDecimal montant;
    private String description;
    private LocalDate date;
}