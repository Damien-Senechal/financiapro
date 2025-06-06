package com.epsi.financiapro.controller;

import com.epsi.financiapro.dto.BudgetItemCreateDTO;
import com.epsi.financiapro.dto.BudgetItemDTO;
import com.epsi.financiapro.entity.BudgetItem;
import com.epsi.financiapro.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/budget")
@Tag(name = "Budget", description = "Gestion du budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    @Operation(summary = "Lister les éléments du budget",
            description = "Retourne la liste des éléments du budget avec filtrage optionnel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<BudgetItemDTO>> getBudgetItems(
            @Parameter(description = "Type d'élément (INCOME ou EXPENSE)")
            @RequestParam(required = false) BudgetItem.BudgetType type,

            @Parameter(description = "Date de début (format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Date de fin (format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Montant minimum")
            @RequestParam(required = false) BigDecimal minAmount,

            @Parameter(description = "Montant maximum")
            @RequestParam(required = false) BigDecimal maxAmount) {

        List<BudgetItemDTO> items = budgetService.getBudgetItems(type, startDate, endDate, minAmount, maxAmount);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    @Operation(summary = "Ajouter un élément au budget",
            description = "Crée un nouvel élément de budget (revenu ou dépense)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Élément créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<BudgetItemDTO> addBudgetItem(@Valid @RequestBody BudgetItemCreateDTO dto) {
        BudgetItemDTO item = budgetService.addBudgetItem(dto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un élément du budget",
            description = "Supprime un élément du budget par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Élément supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Élément non trouvé")
    })
    public ResponseEntity<Void> deleteBudgetItem(
            @Parameter(description = "ID de l'élément à supprimer") @PathVariable Long id) {
        budgetService.deleteBudgetItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/simulate/{months}")
    @Operation(summary = "Simuler l'évolution du budget",
            description = "Simule l'évolution mensuelle du budget avec calcul des soldes cumulés et alertes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Simulation réussie"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<Map<String, Object>> simulateBudget(
            @Parameter(description = "Nombre de mois à simuler") @PathVariable int months) {

        Map<String, Object> simulation = budgetService.simulateBudget(months);
        return ResponseEntity.ok(simulation);
    }
}