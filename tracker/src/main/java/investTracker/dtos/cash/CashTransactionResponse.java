package investTracker.dtos.cash;

import investTracker.models.enums.CashTransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CashTransactionResponse {
    private Long id;
    private Long accountId;
    private CashTransactionType type;
    private String currency;
    private BigDecimal amount;
    private LocalDate executedAt;
    private String note;
}
