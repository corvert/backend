package investTracker.dtos.cash;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CashBalanceResponse {
    private String currency;
    private BigDecimal balance;
}
