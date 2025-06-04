# FinanciaPro - API REST

Plateforme collaborative de gestion budgétaire et de microcrédits entre particuliers.

## Structure du projet

```
financiapro/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/epsi/financiapro/
│       │       ├── FinanciaProApplication.java
│       │       ├── config/
│       │       │   ├── AppConfig.java
│       │       │   ├── SecurityConfig.java
│       │       │   └── SwaggerConfig.java
│       │       ├── controller/
│       │       │   ├── UserController.java
│       │       │   ├── BudgetController.java
│       │       │   ├── LoanController.java
│       │       │   └── RepaymentController.java
│       │       ├── dto/
│       │       │   ├── UserDTO.java
│       │       │   ├── UserRegistrationDTO.java
│       │       │   ├── UserRegistrationResponseDTO.java
│       │       │   ├── BudgetItemDTO.java
│       │       │   ├── BudgetItemCreateDTO.java
│       │       │   ├── BudgetSummaryDTO.java
│       │       │   ├── LoanRequestDTO.java
│       │       │   ├── LoanRequestCreateDTO.java
│       │       │   ├── RepaymentDTO.java
│       │       │   └── RepaymentCreateDTO.java
│       │       ├── entity/
│       │       │   ├── User.java
│       │       │   ├── BudgetItem.java
│       │       │   ├── LoanRequest.java
│       │       │   └── Repayment.java
│       │       ├── exception/
│       │       │   ├── GlobalExceptionHandler.java
│       │       │   └── ErrorResponse.java
│       │       ├── repository/
│       │       │   ├── UserRepository.java
│       │       │   ├── BudgetItemRepository.java
│       │       │   ├── LoanRequestRepository.java
│       │       │   └── RepaymentRepository.java
│       │       ├── security/
│       │       │   └── ApiKeyAuthenticationFilter.java
│       │       └── service/
│       │           ├── CurrentUserService.java
│       │           ├── UserService.java
│       │           ├── BudgetService.java
│       │           ├── LoanService.java
│       │           └── RepaymentService.java
│       └── resources/
│           ├── application.properties
│           └── data.sql
└── pom.xml
```

## Technologies utilisées

- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistance des données
- **H2 Database** - Base de données en mémoire
- **Spring Security** - Authentification par API Key
- **SpringDoc OpenAPI** - Documentation Swagger
- **ModelMapper** - Mapping entités/DTOs
- **Lombok** - Réduction du boilerplate code
- **Java 17** - Version Java

## Installation et démarrage

1. Cloner le projet
2. Installer les dépendances : `mvn clean install`
3. Lancer l'application : `mvn spring-boot:run`
4. L'API sera accessible sur : `http://localhost:8080`

## Documentation API

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/api-docs

## Base de données

- **Console H2** : http://localhost:8080/h2-console
- **JDBC URL** : `jdbc:h2:mem:financiapro`
- **Username** : `sa`
- **Password** : (vide)

## Authentification

L'API utilise une authentification par API Key. Pour toutes les requêtes (sauf `/users/register`), vous devez inclure l'en-tête :

```
Authorization: ApiKey {votre-clé-api}
```

## Utilisateurs de test

| Email | API Key |
|-------|---------|
| jean.dupont@example.com | 11111111-1111-1111-1111-111111111111 |
| marie.martin@example.com | 22222222-2222-2222-2222-222222222222 |
| pierre.bernard@example.com | 33333333-3333-3333-3333-333333333333 |
| sophie.dubois@example.com | 44444444-4444-4444-4444-444444444444 |

## Endpoints principaux

### Users
- `POST /users/register` - Créer un nouvel utilisateur
- `GET /users/me` - Obtenir ses informations
- `GET /users/summary` - Obtenir le résumé budgétaire

### Budget
- `GET /budget` - Lister les éléments du budget (avec filtres)
- `POST /budget/add` - Ajouter un élément
- `DELETE /budget/{id}` - Supprimer un élément
- `GET /budget/simulate/{months}` - Simuler le budget futur

### Loans
- `POST /loans/request` - Créer une demande de prêt
- `GET /loans/incoming` - Voir les demandes reçues
- `PUT /loans/{id}/accept` - Accepter une demande
- `PUT /loans/{id}/refuse` - Refuser une demande
- `GET /loans/history` - Historique des prêts

### Repayments
- `POST /repayments/loan/{id}/repay` - Effectuer un remboursement
- `GET /repayments/loan/{id}/repayments` - Lister les remboursements

## Logiques métier implémentées

1. **Calcul du solde global** : revenus - dépenses + prêts reçus - prêts accordés non remboursés
2. **Validation du prêteur** : doit avoir au moins 500€ de solde pour accepter un prêt
3. **Alertes** : notification console si crédit > revenus × 2
4. **Simulation budgétaire** : projection sur 3 mois basée sur l'historique

## Tests avec Postman

Pour tester l'API :

1. Importer la collection Postman fournie
2. Configurer la variable `{{apiKey}}` avec une clé valide
3. Exécuter les requêtes dans l'ordre :
    - Register (optionnel)
    - Get current user
    - Add budget items
    - Create loan request
    - Accept/Refuse loan
    - Make repayment
    - Check summary

## Sécurité

- Authentification par API Key obligatoire
- Validation des données entrantes
- Vérification des autorisations (un utilisateur ne peut modifier que ses propres données)
- Protection contre les remboursements excessifs
- Validation du solde minimum pour les prêteurs