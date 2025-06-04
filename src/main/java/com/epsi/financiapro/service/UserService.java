package com.epsi.financiapro.service;

import com.epsi.financiapro.dto.*;
import com.epsi.financiapro.entity.BudgetItem;
import com.epsi.financiapro.entity.LoanRequest;
import com.epsi.financiapro.entity.User;
import com.epsi.financiapro.repository.BudgetItemRepository;
import com.epsi.financiapro.repository.LoanRequestRepository;
import com.epsi.financiapro.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final LoanRequestRepository loanRequestRepository;
    private final ModelMapper modelMapper;
    private final CurrentUserService currentUserService;

    public UserService(UserRepository userRepository,
                       BudgetItemRepository budgetItemRepository,
                       LoanRequestRepository loanRequestRepository,
                       ModelMapper modelMapper,
                       CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.loanRequestRepository = loanRequestRepository;
        this.modelMapper = modelMapper;
        this.currentUserService = currentUserService;
    }

    public UserRegistrationResponseDTO register(UserRegistrationDTO dto) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer le nouvel utilisateur
        User user = modelMapper.map(dto, User.class);
        user = userRepository.save(user);

        return modelMapper.map(user, UserRegistrationResponseDTO.class);
    }

    public UserDTO getCurrentUserInfo() {
        User user = currentUserService.getCurrentUser();
        return modelMapper.map(user, UserDTO.class);
    }

    public BudgetSummaryDTO getBudgetSummary() {
        User user = currentUserService.getCurrentUser();
        BudgetSummaryDTO summary = new BudgetSummaryDTO();

        // Calculer les totaux des revenus et dépenses
        BigDecimal totalRevenus = Optional.ofNullable(
                budgetItemRepository.sumByUserAndType(user, BudgetItem.BudgetType.INCOME)
        ).orElse(BigDecimal.ZERO);

        BigDecimal totalDepenses = Optional.ofNullable(
                budgetItemRepository.sumByUserAndType(user, BudgetItem.BudgetType.EXPENSE)
        ).orElse(BigDecimal.ZERO);

        // Calculer le solde de base
        BigDecimal solde = totalRevenus.subtract(totalDepenses);

        // Calculer les prêts
        BigDecimal pretsRecus = BigDecimal.ZERO;
        BigDecimal pretsAccordes = BigDecimal.ZERO;
        BigDecimal remboursementsRecus = BigDecimal.ZERO;
        BigDecimal remboursementsEffectues = BigDecimal.ZERO;

        // Prêts où l'utilisateur est emprunteur (prêts reçus)
        for (LoanRequest loan : loanRequestRepository.findByBorrower(user)) {
            if (loan.getStatut() == LoanRequest.LoanStatus.ACCEPTED) {
                pretsRecus = pretsRecus.add(loan.getMontant());
                remboursementsEffectues = remboursementsEffectues.add(loan.getMontantRembourse());
            }
        }

        // Prêts où l'utilisateur est prêteur (prêts accordés)
        for (LoanRequest loan : loanRequestRepository.findByLender(user)) {
            if (loan.getStatut() == LoanRequest.LoanStatus.ACCEPTED) {
                pretsAccordes = pretsAccordes.add(loan.getMontant());
                remboursementsRecus = remboursementsRecus.add(loan.getMontantRembourse());
            }
        }

        // Calculer le solde global selon la formule
        // solde global = revenus - dépenses + prêts reçus - prêts accordés non remboursés
        BigDecimal pretsAccordesNonRembourses = pretsAccordes.subtract(remboursementsRecus);
        BigDecimal soldeGlobal = solde.add(pretsRecus).subtract(pretsAccordesNonRembourses);

        summary.setTotalRevenus(totalRevenus);
        summary.setTotalDepenses(totalDepenses);
        summary.setSolde(solde);
        summary.setPretsRecus(pretsRecus);
        summary.setPretsAccordes(pretsAccordes);
        summary.setSoldeGlobal(soldeGlobal);

        // Vérifier si l'utilisateur est en situation de crédit > revenus x 2
        BigDecimal totalCredits = pretsRecus.subtract(remboursementsEffectues);
        if (totalCredits.compareTo(totalRevenus.multiply(BigDecimal.valueOf(2))) > 0) {
            System.out.println("ALERTE: L'utilisateur " + user.getEmail() +
                    " a un crédit total supérieur à 2 fois ses revenus!");
        }

        return summary;
    }
}