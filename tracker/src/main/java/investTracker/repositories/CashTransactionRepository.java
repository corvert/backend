package investTracker.repositories;

import investTracker.models.CashTransaction;
import investTracker.models.enums.CashTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CashTransactionRepository extends JpaRepository<CashTransaction, Long> {
    List<CashTransaction> findByUserIdAndAccountIdOrderByExecutedAtDesc(Long userId, Long accountId);

    List<CashTransaction> findByUserIdAndAccountIdAndExecutedAtBetweenOrderByExecutedAtDesc
            (Long userId, Long accountId, LocalDate from, LocalDate to);

    List<CashTransaction> findByUserIdAndAccountIdAndTypeInAndExecutedAtBetween(
            Long userId, Long accountId, List<CashTransactionType> types, LocalDate from, LocalDate to
    );
}
