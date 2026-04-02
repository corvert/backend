package investTracker.repositories;

import investTracker.models.CashTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashTransactionRepository extends JpaRepository<CashTransaction, Long> {
    List<CashTransaction> findByUserIdAndAccountIdOrderByExecutedAtDesc(Long userId, Long accountId);
}
