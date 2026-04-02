package investTracker.services;

import investTracker.dtos.cash.CashTransactionResponse;
import investTracker.dtos.cash.CreateCashTransactionRequest;
import investTracker.models.CashTransaction;
import investTracker.models.enums.CashTransactionType;
import investTracker.repositories.AccountRepository;
import investTracker.repositories.CashTransactionRepository;
import investTracker.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CashTransactionService {
    @Autowired
    private CashTransactionRepository cashTransactionRepository;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AccountRepository accountRepository;

    public CashTransactionResponse create(CreateCashTransactionRequest request) {
        Long userId = authUtil.loggedInUserId();
        accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        CashTransactionType type = request.getType();
        if(type != CashTransactionType.DEPOSIT && type != CashTransactionType.WITHDRAW){
            throw new IllegalArgumentException("Only DEPOSIT and WITHDRAW are allowed in this endpoint");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        String currency = request.getCurrency().trim().toUpperCase();
        BigDecimal signedAmount = (type == CashTransactionType.DEPOSIT)
                ? request.getAmount() : request.getAmount().negate();

        CashTransaction transaction = new CashTransaction();
        transaction.setUserId(userId);
        transaction.setAccountId(request.getAccountId());
        transaction.setType(type);
        transaction.setCurrency(currency);
        transaction.setAmount(signedAmount);
        transaction.setExecutedAt(request.getExecutedAt());
        transaction.setNote(request.getNote());

        CashTransaction savedTransaction = cashTransactionRepository.save(transaction);
        return toTransactionDto(savedTransaction);
    }

    public List<CashTransactionResponse> transactionList(Long accountId) {
        Long userId = authUtil.loggedInUserId();
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return cashTransactionRepository.findByUserIdAndAccountIdOrderByExecutedAtDesc(userId, accountId)
                .stream().map(this::toTransactionDto).toList();
    }

    private CashTransactionResponse toTransactionDto(CashTransaction transaction) {
        return new CashTransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getType(),
                transaction.getCurrency(),
                transaction.getAmount(),
                transaction.getExecutedAt(),
                transaction.getNote()
        );
    }


}
