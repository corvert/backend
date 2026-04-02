package investTracker.services;

import investTracker.dtos.trade.CreateTradeRequest;
import investTracker.dtos.trade.TradeResponse;
import investTracker.models.Asset;
import investTracker.models.CashTransaction;
import investTracker.models.Trade;
import investTracker.models.enums.CashTransactionType;
import investTracker.models.enums.TradeSide;
import investTracker.repositories.AccountRepository;
import investTracker.repositories.AssetRepository;
import investTracker.repositories.CashTransactionRepository;
import investTracker.repositories.TradeRepository;
import investTracker.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TradeService {
    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private CashTransactionRepository cashTransactionRepository;

    public TradeResponse createTrade(CreateTradeRequest request) {
        Long userId = authUtil.loggedInUserId();

        accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                .orElseThrow(() -> new RuntimeException("Account not found or does not belong to user"));

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        validate(request);

        String currency = asset.getCurrency();

        Trade trade = new Trade();
        trade.setUserId(userId);
        trade.setAccountId(request.getAccountId());
        trade.setAssetId(request.getAssetId());
        trade.setSide(request.getSide());
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getPrice());
        trade.setCurrency(currency);
        trade.setExecutedAt(request.getExecutedAt());
        trade.setFee(request.getFee());
        trade.setNote(request.getNote());

        Trade savedTrade = tradeRepository.save(trade);

        BigDecimal grossSum = request.getQuantity().multiply(request.getPrice());
        BigDecimal tradeCash = (request.getSide() == TradeSide.BUY) ? grossSum.negate() : grossSum;

        CashTransaction cashTrade = new CashTransaction();
        cashTrade.setUserId(userId);
        cashTrade.setAccountId(request.getAccountId());
        cashTrade.setType(CashTransactionType.TRADE);
        cashTrade.setCurrency(currency);
        cashTrade.setAmount(tradeCash);
        cashTrade.setExecutedAt(request.getExecutedAt());
        cashTrade.setNote("Trade #" + savedTrade.getId()+ ", " +  asset.getName()+ ", "+ savedTrade.getExecutedAt());
        cashTransactionRepository.save(cashTrade);

        if (request.getFee() != null && request.getFee().compareTo(BigDecimal.ZERO) > 0) {
            CashTransaction cashFee = new CashTransaction();
            cashFee.setUserId(userId);
            cashFee.setAccountId(request.getAccountId());
            cashFee.setType(CashTransactionType.FEE);
            cashFee.setCurrency(currency);
            cashFee.setAmount(request.getFee().negate());
            cashFee.setExecutedAt(request.getExecutedAt());
            cashFee.setNote("Fee for trade #" + savedTrade.getId());
            cashTransactionRepository.save(cashFee);
        }

        return toTradeDto(savedTrade);

    }

    private TradeResponse toTradeDto(Trade trade) {
        return new TradeResponse(
                trade.getId(),
                trade.getAccountId(),
                trade.getAssetId(),
                trade.getSide(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getCurrency(),
                trade.getExecutedAt(),
                trade.getFee(),
                trade.getNote()
        );
    }

    private void validate(CreateTradeRequest req) {
        if (req.getQuantity() == null || req.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (req.getFee() != null && req.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fee must be >= 0");
        }
        if (req.getExecutedAt() == null) {
            throw new IllegalArgumentException("ExecutedAt is required");
        }
    }
}
