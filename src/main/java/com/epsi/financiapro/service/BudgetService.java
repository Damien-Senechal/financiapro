package com.epsi.financiapro.service;

import com.epsi.financiapro.dto.BudgetItemCreateDTO;
import com.epsi.financiapro.dto.BudgetItemDTO;
import com.epsi.financiapro.dto.BudgetSummaryDTO;
import com.epsi.financiapro.entity.BudgetItem;
import com.epsi.financiapro.entity.User;
import com.epsi.financiapro.repository.BudgetItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    private final BudgetItemRepository budgetItemRepository;
    private final CurrentUserService currentUserService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public BudgetService(BudgetItemRepository budgetItemRepository,
                         CurrentUserService currentUserService, UserService userService,
                         ModelMapper modelMapper) {
        this.budgetItemRepository = budgetItemRepository;
        this.currentUserService = currentUserService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public List<BudgetItemDTO> getBudgetItems(BudgetItem.BudgetType type,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              BigDecimal minAmount,
                                              BigDecimal maxAmount) {
        User user = currentUserService.getCurrentUser();
        List<BudgetItem> items;

        // Filtrage selon les paramètres
        if (type != null && startDate != null && endDate != null) {
            items = budgetItemRepository.findByUserAndTypeAndDateBetween(user, type, startDate, endDate);
        } else if (type != null) {
            items = budgetItemRepository.findByUserAndType(user, type);
        } else if (startDate != null && endDate != null) {
            items = budgetItemRepository.findByUserAndDateBetween(user, startDate, endDate);
        } else {
            items = budgetItemRepository.findByUser(user);
        }

        // Filtrage supplémentaire par montant
        if (minAmount != null || maxAmount != null) {
            items = items.stream()
                    .filter(item -> {
                        if (minAmount != null && item.getMontant().compareTo(minAmount) < 0) {
                            return false;
                        }
                        if (maxAmount != null && item.getMontant().compareTo(maxAmount) > 0) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        return items.stream()
                .map(item -> modelMapper.map(item, BudgetItemDTO.class))
                .collect(Collectors.toList());
    }

    public BudgetItemDTO addBudgetItem(BudgetItemCreateDTO dto) {
        User user = currentUserService.getCurrentUser();

        BudgetItem item = modelMapper.map(dto, BudgetItem.class);
        item.setUser(user);

        item = budgetItemRepository.save(item);

        return modelMapper.map(item, BudgetItemDTO.class);
    }

    public void deleteBudgetItem(Long id) {
        User user = currentUserService.getCurrentUser();

        BudgetItem item = budgetItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Élément de budget non trouvé"));

        // Vérifier que l'élément appartient à l'utilisateur courant
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n'avez pas le droit de supprimer cet élément");
        }

        budgetItemRepository.delete(item);
    }

    public Map<String, Object> simulateBudget(int months) {
        User user = currentUserService.getCurrentUser();
        LocalDate today = LocalDate.now();

        // Récupérer le résumé actuel
        BudgetSummaryDTO currentSummary = userService.getBudgetSummary();

        // Calculer les moyennes des 3 derniers mois
        LocalDate threeMonthsAgo = today.minusMonths(3);
        List<BudgetItem> recentItems = budgetItemRepository
                .findByUserAndDateBetween(user, threeMonthsAgo, today);

        BigDecimal avgIncome = calculateAverage(recentItems, BudgetItem.BudgetType.INCOME);
        BigDecimal avgExpense = calculateAverage(recentItems, BudgetItem.BudgetType.EXPENSE);

        // Créer les projections mensuelles
        List<Map<String, Object>> projections = new ArrayList<>();
        BigDecimal soldeCumule = currentSummary.getSoldeGlobal();

        for (int i = 1; i <= months; i++) {
            Map<String, Object> monthProjection = new HashMap<>();
            LocalDate targetDate = today.plusMonths(i);

            BigDecimal soldeMensuel = avgIncome.subtract(avgExpense);
            soldeCumule = soldeCumule.add(soldeMensuel);

            monthProjection.put("mois", targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            monthProjection.put("revenus", avgIncome);
            monthProjection.put("depenses", avgExpense);
            monthProjection.put("soldeMensuel", soldeMensuel);
            monthProjection.put("soldeCumule", soldeCumule);

            projections.add(monthProjection);
        }

        // Générer des insights et alertes
        List<String> alertes = new ArrayList<>();
        BigDecimal variationTotale = soldeCumule.subtract(currentSummary.getSoldeGlobal());

        if (soldeCumule.compareTo(currentSummary.getSoldeGlobal()) > 0) {
            alertes.add("Tendance positive : +" + variationTotale + "€ prévus dans " + months + " mois");
        } else {
            alertes.add("Attention : " + variationTotale + "€ prévus dans " + months + " mois");
        }

        if (avgIncome.subtract(avgExpense).compareTo(BigDecimal.ZERO) > 0) {
            alertes.add("Épargne mensuelle moyenne : " + avgIncome.subtract(avgExpense) + "€");
        }

        // Construire la réponse complète
        Map<String, Object> response = new HashMap<>();
        response.put("soldeActuel", currentSummary.getSoldeGlobal());
        response.put("soldeFinal", soldeCumule);
        response.put("variationTotale", variationTotale);
        response.put("moyennesMensuelles", Map.of(
                "revenus", avgIncome,
                "depenses", avgExpense,
                "epargne", avgIncome.subtract(avgExpense)
        ));
        response.put("projections", projections);
        response.put("alertes", alertes);
        response.put("resume", Map.of(
                "epargneTotale", variationTotale,
                "tauxEpargne", avgIncome.compareTo(BigDecimal.ZERO) > 0 ?
                        avgIncome.subtract(avgExpense).divide(avgIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                        BigDecimal.ZERO,
                "tendance", variationTotale.compareTo(BigDecimal.ZERO) > 0 ? "positive" : "negative"
        ));

        return response;
    }

    private BigDecimal calculateAverage(List<BudgetItem> items, BudgetItem.BudgetType type) {
        BigDecimal total = items.stream()
                .filter(item -> item.getType() == type)
                .map(BudgetItem::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Moyenne sur 3 mois, éviter division par zéro
        return total.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }
}