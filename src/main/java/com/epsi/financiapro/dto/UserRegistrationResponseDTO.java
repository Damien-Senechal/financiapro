package com.epsi.financiapro.dto;

import lombok.Data;

@Data
public class UserRegistrationResponseDTO {
    private Long id;
    private String prenom;
    private String nom;
    private String email;
    private String apiKey;
}