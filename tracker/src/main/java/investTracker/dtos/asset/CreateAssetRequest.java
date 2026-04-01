package investTracker.dtos.asset;

import investTracker.models.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAssetRequest {
    @NotNull
    private AssetType type;

    private String symbol;
    private String isin;

    @NotBlank
    private String name;

    private String currency;
}
