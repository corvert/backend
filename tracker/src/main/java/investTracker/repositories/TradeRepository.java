package investTracker.repositories;

import investTracker.models.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByUserIdAndAccountIdAndExecutedAtBetweenOrderByExecutedAtDesc(Long userId, Long accountId, LocalDate from, LocalDate to);

    List<Trade> findByUserIdAndAccountIdOrderByExecutedAtDesc(Long userId, Long accountId);
}
