package com.epsi.financiapro.repository;

import com.epsi.financiapro.entity.LoanRequest;
import com.epsi.financiapro.entity.LoanRequest.LoanStatus;
import com.epsi.financiapro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    List<LoanRequest> findByBorrower(User borrower);

    List<LoanRequest> findByLender(User lender);

    List<LoanRequest> findByLenderAndStatut(User lender, LoanStatus statut);

    List<LoanRequest> findByBorrowerOrLender(User borrower, User lender);
}