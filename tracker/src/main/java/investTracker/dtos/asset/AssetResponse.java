package investTracker.dtos.asset;

import investTracker.models.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetResponse {
    private Long id;
    private AssetType type;
    private String symbol;
    private String name;
    private String currency;
}
