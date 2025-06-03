package com.epsi.financiapro.dto;

import com.epsi.financiapro.entity.LoanRequest.LoanStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanRequestDTO {
    private Long id;
    private UserDTO borrower;
    private UserDTO lender;
    private BigDecimal montant;
    private BigDecimal interet;
    private Integer duree;
    private LoanStatus statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateAcceptation;
    private BigDecimal montantTotal;
    private BigDecimal montantRembourse;
    private BigDecimal montantRestant;
}