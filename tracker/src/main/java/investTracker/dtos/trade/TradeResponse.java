package investTracker.dtos.trade;

import investTracker.models.enums.TradeSide;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TradeResponse {
    private Long id;
    private Long accountId;
    private Long assetId;
    private TradeSide side;
    private BigDecimal quantity;
    private BigDecimal price;
    private String currency;
    private LocalDate executedAt;
    private BigDecimal fee;
    private String note;
}
