package investTracker.models;

import investTracker.models.enums.TradeSide;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(
        name = "trades",
        indexes = {
                @Index(name = "idx_trades_user_account_date", columnList = "user_id,account_id,executed_at"),
                @Index(name = "idx_trades_user_asset_date", columnList = "user_id,asset_id,executed_at")
        }
)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="account_id", nullable = false)
    private Long accountId;

    @Column(name="asset_id", nullable = false)
    private Long assetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeSide side;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDate executedAt;

    @Column(precision = 19, scale = 8)
    private BigDecimal fee;

    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (currency != null) currency = currency.trim().toUpperCase();
        if (note != null) note = note.trim();
    }
}
