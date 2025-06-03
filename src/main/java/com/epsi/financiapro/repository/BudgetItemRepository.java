package com.epsi.financiapro.repository;

import com.epsi.financiapro.entity.BudgetItem;
import com.epsi.financiapro.entity.BudgetItem.BudgetType;
import com.epsi.financiapro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {
    List<BudgetItem> findByUser(User user);

    List<BudgetItem> findByUserAndType(User user, BudgetType type);

    List<BudgetItem> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<BudgetItem> findByUserAndTypeAndDateBetween(User user, BudgetType type, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(b.montant) FROM BudgetItem b WHERE b.user = :user AND b.type = :type")
    BigDecimal sumByUserAndType(@Param("user") User user, @Param("type") BudgetType type);
}