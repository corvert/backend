package investTracker.models;

import investTracker.models.enums.AssetType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(
        name = "assets",
        indexes = {
                @Index(name = "idx_assets_type", columnList = "type"),
                @Index(name = "idx_assets_symbol", columnList = "symbol"),
                @Index(name = "idx_assets_isin", columnList = "isin")
        }
)
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType type;

    @Column
    private String symbol;

    @Column
    private String isin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (symbol != null) {
            symbol = symbol.trim().toUpperCase();
            if (symbol.isBlank()) symbol = null;
        }
        if (isin != null) {
            isin = isin.trim().toUpperCase();
            if (isin.isBlank()) isin = null;
        }
        if (name != null) name = name.trim();
        if (currency != null) currency = currency.trim().toUpperCase();
    }
}
