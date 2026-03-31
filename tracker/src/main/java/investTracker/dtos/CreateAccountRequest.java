package investTracker.dtos;

import investTracker.models.enums.AccountKind;
import investTracker.models.enums.TradingMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank
    private String name;
    @NotBlank
    private AccountKind accountKind;
    @NotBlank
    private TradingMode tradingMode;
}
