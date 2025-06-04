package com.epsi.financiapro.service;

import com.epsi.financiapro.dto.RepaymentCreateDTO;
import com.epsi.financiapro.dto.RepaymentDTO;
import com.epsi.financiapro.entity.LoanRequest;
import com.epsi.financiapro.entity.Repayment;
import com.epsi.financiapro.entity.User;
import com.epsi.financiapro.repository.LoanRequestRepository;
import com.epsi.financiapro.repository.RepaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRequestRepository loanRequestRepository;
    private final CurrentUserService currentUserService;
    private final ModelMapper modelMapper;

    public RepaymentService(RepaymentRepository repaymentRepository,
                            LoanRequestRepository loanRequestRepository,
                            CurrentUserService currentUserService,
                            ModelMapper modelMapper) {
        this.repaymentRepository = repaymentRepository;
        this.loanRequestRepository = loanRequestRepository;
        this.currentUserService = currentUserService;
        this.modelMapper = modelMapper;
    }

    public RepaymentDTO createRepayment(Long loanId, RepaymentCreateDTO dto) {
        User borrower = currentUserService.getCurrentUser();

        // Récupérer le prêt
        LoanRequest loan = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));

        // Vérifications
        if (!loan.getBorrower().getId().equals(borrower.getId())) {
            throw new RuntimeException("Vous n'êtes pas l'emprunteur de ce prêt");
        }

        if (loan.getStatut() != LoanRequest.LoanStatus.ACCEPTED) {
            throw new RuntimeException("Ce prêt n'est pas accepté");
        }

        // Vérifier qu'on ne rembourse pas plus que nécessaire
        if (dto.getMontant().compareTo(loan.getMontantRestant()) > 0) {
            throw new RuntimeException("Le montant du remboursement dépasse le montant restant dû ("
                    + loan.getMontantRestant() + "€)");
        }

        // Créer le remboursement
        Repayment repayment = new Repayment();
        repayment.setLoanRequest(loan);
        repayment.setMontant(dto.getMontant());
        repayment.setDate(LocalDate.now());
        repayment.setCommentaire(dto.getCommentaire());

        repayment = repaymentRepository.save(repayment);

        // Log si le prêt est complètement remboursé
        if (loan.getMontantRestant().compareTo(dto.getMontant()) == 0) {
            System.out.println("Le prêt #" + loan.getId() + " a été complètement remboursé!");
        }

        return modelMapper.map(repayment, RepaymentDTO.class);
    }

    public List<RepaymentDTO> getRepaymentsByLoan(Long loanId) {
        User user = currentUserService.getCurrentUser();

        // Récupérer le prêt
        LoanRequest loan = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Prêt non trouvé"));

        // Vérifier que l'utilisateur est impliqué dans le prêt
        if (!loan.getBorrower().getId().equals(user.getId()) &&
                !loan.getLender().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à voir ces remboursements");
        }

        List<Repayment> repayments = repaymentRepository.findByLoanRequest(loan);

        return repayments.stream()
                .map(repayment -> modelMapper.map(repayment, RepaymentDTO.class))
                .collect(Collectors.toList());
    }
}