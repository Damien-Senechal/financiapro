package com.epsi.financiapro.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MonthlyProjectionDTO {
    private String mois; // "2025-07"
    private BigDecimal revenus;
    private BigDecimal depenses;
    private BigDecimal soldeMensuel;
    private BigDecimal soldeCumule;
    private BigDecimal pretsARecevoir;
    private BigDecimal remboursementsAPayer;
    private BigDecimal soldeAvecCredits;
}
