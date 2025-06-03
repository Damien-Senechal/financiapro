package com.epsi.financiapro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RepaymentDTO {
    private Long id;
    private Long loanRequestId;
    private BigDecimal montant;
    private LocalDate date;
    private String commentaire;
}