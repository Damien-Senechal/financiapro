package com.epsi.financiapro.controller;

import com.epsi.financiapro.dto.RepaymentCreateDTO;
import com.epsi.financiapro.dto.RepaymentDTO;
import com.epsi.financiapro.service.RepaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repayments")
@Tag(name = "Repayments", description = "Gestion des remboursements")
public class RepaymentController {

    private final RepaymentService repaymentService;

    public RepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @PostMapping("/loan/{id}/repay")
    @Operation(summary = "Effectuer un remboursement",
            description = "Crée un nouveau remboursement pour un prêt accepté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Remboursement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Montant invalide ou supérieur au restant dû"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (pas l'emprunteur)"),
            @ApiResponse(responseCode = "404", description = "Prêt non trouvé")
    })
    public ResponseEntity<RepaymentDTO> createRepayment(
            @Parameter(description = "ID du prêt") @PathVariable Long id,
            @Valid @RequestBody RepaymentCreateDTO dto) {
        RepaymentDTO repayment = repaymentService.createRepayment(id, dto);
        return new ResponseEntity<>(repayment, HttpStatus.CREATED);
    }

    @GetMapping("/loan/{id}/repayments")
    @Operation(summary = "Lister les remboursements d'un prêt",
            description = "Retourne la liste des remboursements effectués pour un prêt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (pas impliqué dans le prêt)"),
            @ApiResponse(responseCode = "404", description = "Prêt non trouvé")
    })
    public ResponseEntity<List<RepaymentDTO>> getRepaymentsByLoan(
            @Parameter(description = "ID du prêt") @PathVariable Long id) {
        List<RepaymentDTO> repayments = repaymentService.getRepaymentsByLoan(id);
        return ResponseEntity.ok(repayments);
    }
}