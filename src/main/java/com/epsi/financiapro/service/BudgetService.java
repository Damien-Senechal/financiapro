package com.epsi.financiapro.service;

import com.epsi.financiapro.dto.BudgetItemCreateDTO;
import com.epsi.financiapro.dto.BudgetItemDTO;
import com.epsi.financiapro.entity.BudgetItem;
import com.epsi.financiapro.entity.User;
import com.epsi.financiapro.repository.BudgetItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    private final BudgetItemRepository budgetItemRepository;
    private final CurrentUserService currentUserService;
    private final ModelMapper modelMapper;

    public BudgetService(BudgetItemRepository budgetItemRepository,
                         CurrentUserService currentUserService,
                         ModelMapper modelMapper) {
        this.budgetItemRepository = budgetItemRepository;
        this.currentUserService = currentUserService;
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

    public List<BudgetItemDTO> simulateBudget(int months) {
        User user = currentUserService.getCurrentUser();
        LocalDate today = LocalDate.now();

        // Récupérer les éléments récurrents du dernier mois
        LocalDate lastMonthStart = today.minusMonths(1);
        List<BudgetItem> lastMonthItems = budgetItemRepository
                .findByUserAndDateBetween(user, lastMonthStart, today);

        // Créer des projections pour les mois futurs
        List<BudgetItemDTO> projections = lastMonthItems.stream()
                .flatMap(item -> {
                    // Créer une projection pour chaque mois futur
                    return java.util.stream.IntStream.range(1, months + 1)
                            .mapToObj(month -> {
                                BudgetItemDTO projection = modelMapper.map(item, BudgetItemDTO.class);
                                projection.setId(null); // Ce sont des projections, pas de vrais éléments
                                projection.setDate(item.getDate().plusMonths(month));
                                projection.setDescription("[PROJECTION] " + item.getDescription());
                                return projection;
                            });
                })
                .collect(Collectors.toList());

        return projections;
    }
}