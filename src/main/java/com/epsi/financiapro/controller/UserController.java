package com.epsi.financiapro.controller;

import com.epsi.financiapro.dto.*;
import com.epsi.financiapro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Enregistrer un nouvel utilisateur",
            description = "Crée un nouvel utilisateur et retourne ses informations avec sa clé API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<UserRegistrationResponseDTO> register(@Valid @RequestBody UserRegistrationDTO dto) {
        UserRegistrationResponseDTO response = userService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtenir les informations de l'utilisateur connecté",
            description = "Retourne les informations de l'utilisateur authentifié par API Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUserInfo();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/summary")
    @Operation(summary = "Obtenir le résumé budgétaire",
            description = "Retourne le résumé complet du budget incluant revenus, dépenses, prêts et solde global")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résumé récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<BudgetSummaryDTO> getBudgetSummary() {
        BudgetSummaryDTO summary = userService.getBudgetSummary();
        return ResponseEntity.ok(summary);
    }
}