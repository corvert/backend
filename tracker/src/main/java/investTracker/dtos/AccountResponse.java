package investTracker.dtos;

import investTracker.models.enums.AccountKind;
import investTracker.models.enums.TradingMode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountName;
    private AccountKind accountKind;
    private TradingMode tradingMode;
    private LocalDateTime createdAt;
}
