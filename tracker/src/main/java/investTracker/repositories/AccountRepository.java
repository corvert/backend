package investTracker.repositories;


import investTracker.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUserIdAndAccountNameIgnoreCase(Long userId, String trim);

    List<Account> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Account> findByIdAndUserId(Long id, Long userId);

}
