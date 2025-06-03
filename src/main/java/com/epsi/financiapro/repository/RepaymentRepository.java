package com.epsi.financiapro.repository;

import com.epsi.financiapro.entity.LoanRequest;
import com.epsi.financiapro.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    List<Repayment> findByLoanRequest(LoanRequest loanRequest);
}