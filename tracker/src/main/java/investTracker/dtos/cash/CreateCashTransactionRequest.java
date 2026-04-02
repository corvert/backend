package investTracker.dtos.cash;

import investTracker.models.enums.CashTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCashTransactionRequest {

    @NotNull
    private Long accountId;

    @NotNull
    private CashTransactionType type;

    @NotBlank
    private String currency;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate executedAt;

    private String note;
}
