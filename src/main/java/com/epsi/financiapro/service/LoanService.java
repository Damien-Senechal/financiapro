package com.epsi.financiapro.service;

import com.epsi.financiapro.dto.LoanRequestCreateDTO;
import com.epsi.financiapro.dto.LoanRequestDTO;
import com.epsi.financiapro.entity.LoanRequest;
import com.epsi.financiapro.entity.User;
import com.epsi.financiapro.repository.LoanRequestRepository;
import com.epsi.financiapro.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanService {

    private final LoanRequestRepository loanRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final ModelMapper modelMapper;

    public LoanService(LoanRequestRepository loanRequestRepository,
                       UserRepository userRepository,
                       UserService userService,
                       CurrentUserService currentUserService,
                       ModelMapper modelMapper) {
        this.loanRequestRepository = loanRequestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.currentUserService = currentUserService;
        this.modelMapper = modelMapper;
    }

    public LoanRequestDTO createLoanRequest(LoanRequestCreateDTO dto) {
        User borrower = currentUserService.getCurrentUser();

        // Vérifier que le prêteur existe
        User lender = userRepository.findById(dto.getLenderId())
                .orElseThrow(() -> new RuntimeException("Prêteur non trouvé"));

        // Vérifier que l'emprunteur et le prêteur sont différents
        if (borrower.getId().equals(lender.getId())) {
            throw new RuntimeException("Vous ne pouvez pas vous prêter de l'argent à vous-même");
        }

        // Créer la demande de prêt
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setBorrower(borrower);
        loanRequest.setLender(lender);
        loanRequest.setMontant(dto.getMontant());
        loanRequest.setInteret(dto.getInteret());
        loanRequest.setDuree(dto.getDuree());
        loanRequest.setStatut(LoanRequest.LoanStatus.PENDING);
        loanRequest.setDateCreation(LocalDateTime.now());

        loanRequest = loanRequestRepository.save(loanRequest);

        return modelMapper.map(loanRequest, LoanRequestDTO.class);
    }

    public List<LoanRequestDTO> getIncomingLoanRequests() {
        User lender = currentUserService.getCurrentUser();

        List<LoanRequest> requests = loanRequestRepository
                .findByLenderAndStatut(lender, LoanRequest.LoanStatus.PENDING);

        return requests.stream()
                .map(request -> modelMapper.map(request, LoanRequestDTO.class))
                .collect(Collectors.toList());
    }

    public LoanRequestDTO acceptLoanRequest(Long id) {
        User lender = currentUserService.getCurrentUser();

        LoanRequest request = loanRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande de prêt non trouvée"));

        // Vérifications
        if (!request.getLender().getId().equals(lender.getId())) {
            throw new RuntimeException("Vous n'êtes pas le prêteur de cette demande");
        }

        if (request.getStatut() != LoanRequest.LoanStatus.PENDING) {
            throw new RuntimeException("Cette demande a déjà été traitée");
        }

        // Vérifier le solde du prêteur
        BigDecimal lenderBalance = userService.getBudgetSummary().getSoldeGlobal();
        if (lenderBalance.compareTo(BigDecimal.valueOf(500)) < 0) {
            throw new RuntimeException("Solde insuffisant. Vous devez avoir au moins 500€ pour accepter un prêt");
        }

        // Accepter la demande
        request.setStatut(LoanRequest.LoanStatus.ACCEPTED);
        request.setDateAcceptation(LocalDateTime.now());

        request = loanRequestRepository.save(request);

        return modelMapper.map(request, LoanRequestDTO.class);
    }

    public LoanRequestDTO refuseLoanRequest(Long id) {
        User lender = currentUserService.getCurrentUser();

        LoanRequest request = loanRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande de prêt non trouvée"));

        // Vérifications
        if (!request.getLender().getId().equals(lender.getId())) {
            throw new RuntimeException("Vous n'êtes pas le prêteur de cette demande");
        }

        if (request.getStatut() != LoanRequest.LoanStatus.PENDING) {
            throw new RuntimeException("Cette demande a déjà été traitée");
        }

        // Refuser la demande
        request.setStatut(LoanRequest.LoanStatus.REFUSED);

        request = loanRequestRepository.save(request);

        return modelMapper.map(request, LoanRequestDTO.class);
    }

    public List<LoanRequestDTO> getLoanHistory() {
        User user = currentUserService.getCurrentUser();

        // Récupérer tous les prêts où l'utilisateur est impliqué
        List<LoanRequest> requests = loanRequestRepository
                .findByBorrowerOrLender(user, user);

        return requests.stream()
                .map(request -> modelMapper.map(request, LoanRequestDTO.class))
                .collect(Collectors.toList());
    }
}