package investTracker.dtos.trade;

import investTracker.models.enums.TradeSide;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTradeRequest {
    @NotNull
    private Long accountId;

    @NotNull
    private Long assetId;

    @NotNull
    private TradeSide side;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private BigDecimal price;

    @NotNull
    private LocalDate executedAt;

    private BigDecimal fee;
    private String note;
}
