package com.epsi.financiapro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lender_id")
    private User lender;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interet; // Taux d'intérêt en pourcentage

    @Column(nullable = false)
    private Integer duree; // Durée en mois

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus statut = LoanStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateAcceptation;

    @OneToMany(mappedBy = "loanRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Repayment> repayments = new ArrayList<>();

    public enum LoanStatus {
        PENDING,
        ACCEPTED,
        REFUSED
    }

    // Calcul du montant total à rembourser
    public BigDecimal getMontantTotal() {
        return montant.add(montant.multiply(interet.divide(BigDecimal.valueOf(100))));
    }

    // Calcul du montant remboursé
    public BigDecimal getMontantRembourse() {
        return repayments.stream()
                .map(Repayment::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calcul du montant restant à rembourser
    public BigDecimal getMontantRestant() {
        return getMontantTotal().subtract(getMontantRembourse());
    }
}