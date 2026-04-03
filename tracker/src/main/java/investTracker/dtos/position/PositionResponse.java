package investTracker.dtos.position;

import investTracker.models.enums.PositionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PositionResponse {
    private Long assetId;
    private String assetName;
    private String assetSymbol;
    private String currency;
    private BigDecimal quantity;
    private PositionType positionType;
}
