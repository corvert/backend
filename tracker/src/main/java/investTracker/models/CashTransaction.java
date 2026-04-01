package investTracker.models;


import investTracker.models.enums.CashTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(
        name = "cash_transactions",
        indexes = {
                @Index(name = "idx_cash_tx_user_account_date", columnList = "user_id,account_id,executed_at"),
                @Index(name = "idx_cash_tx_user_account_currency", columnList = "user_id,account_id,currency")
        }
)
public class CashTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashTransactionType type;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name="executed_at", nullable = false)
    private LocalDate executedAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (currency != null) currency = currency.trim().toUpperCase();
        if (note != null) note = note.trim();
    }
}
