package investTracker.models;

import investTracker.models.enums.AccountKind;
import investTracker.models.enums.TradingMode;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String accountName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountKind accountKind;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradingMode tradingMode;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;


}
