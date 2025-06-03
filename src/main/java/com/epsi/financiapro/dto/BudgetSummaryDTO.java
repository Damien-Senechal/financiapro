package com.epsi.financiapro.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetSummaryDTO {
    private BigDecimal totalRevenus;
    private BigDecimal totalDepenses;
    private BigDecimal solde;
    private BigDecimal pretsRecus;
    private BigDecimal pretsAccordes;
    private BigDecimal soldeGlobal;
}