package investTracker.dtos.account;

import investTracker.models.enums.AccountKind;
import investTracker.models.enums.TradingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank
    private String name;
    @NotNull
    private AccountKind accountKind;
    @NotNull
    private TradingMode tradingMode;
}
