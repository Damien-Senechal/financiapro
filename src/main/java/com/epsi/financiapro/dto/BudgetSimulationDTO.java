package com.epsi.financiapro.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BudgetSimulationDTO {
    private List<MonthlyProjectionDTO> projections;
    private BigDecimal soldeInitial;
    private BigDecimal soldeFinal;
    private BigDecimal variationTotale;
    private List<String> alertes;
}
