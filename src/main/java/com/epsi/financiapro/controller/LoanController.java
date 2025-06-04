package com.epsi.financiapro.controller;

import com.epsi.financiapro.dto.LoanRequestCreateDTO;
import com.epsi.financiapro.dto.LoanRequestDTO;
import com.epsi.financiapro.service.LoanService;
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
@RequestMapping("/loans")
@Tag(name = "Loans", description = "Gestion des prêts")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/request")
    @Operation(summary = "Créer une demande de prêt",
            description = "Crée une nouvelle demande de prêt envers un autre utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Demande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Prêteur non trouvé")
    })
    public ResponseEntity<LoanRequestDTO> createLoanRequest(@Valid @RequestBody LoanRequestCreateDTO dto) {
        LoanRequestDTO request = loanService.createLoanRequest(dto);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @GetMapping("/incoming")
    @Operation(summary = "Lister les demandes de prêt reçues",
            description = "Retourne la liste des demandes de prêt en attente pour l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<LoanRequestDTO>> getIncomingRequests() {
        List<LoanRequestDTO> requests = loanService.getIncomingLoanRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accepter une demande de prêt",
            description = "Accepte une demande de prêt en attente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande acceptée avec succès"),
            @ApiResponse(responseCode = "400", description = "Solde insuffisant (moins de 500€)"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<LoanRequestDTO> acceptLoanRequest(
            @Parameter(description = "ID de la demande de prêt") @PathVariable Long id) {
        LoanRequestDTO request = loanService.acceptLoanRequest(id);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/refuse")
    @Operation(summary = "Refuser une demande de prêt",
            description = "Refuse une demande de prêt en attente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande refusée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<LoanRequestDTO> refuseLoanRequest(
            @Parameter(description = "ID de la demande de prêt") @PathVariable Long id) {
        LoanRequestDTO request = loanService.refuseLoanRequest(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/history")
    @Operation(summary = "Historique des prêts",
            description = "Retourne l'historique complet des prêts (empruntés et accordés)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<LoanRequestDTO>> getLoanHistory() {
        List<LoanRequestDTO> history = loanService.getLoanHistory();
        return ResponseEntity.ok(history);
    }
}