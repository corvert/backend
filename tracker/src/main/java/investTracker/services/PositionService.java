package investTracker.services;

import investTracker.dtos.position.PositionResponse;
import investTracker.models.Asset;
import investTracker.models.Trade;
import investTracker.models.enums.PositionType;
import investTracker.models.enums.TradeSide;
import investTracker.repositories.AccountRepository;
import investTracker.repositories.AssetRepository;
import investTracker.repositories.TradeRepository;
import investTracker.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PositionService {
    @Autowired
    private AuthUtil authutil;
    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AssetRepository assetRepository;


    public List<PositionResponse> positionsForAccount(Long accountId) {
        Long userId = authutil.loggedInUserId();
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found or does not belong to user"));

        List<Trade> trades = tradeRepository.findByUserIdAndAccountIdOrderByExecutedAtDesc(userId, accountId);

        Map<Long, BigDecimal> assetQuantity = new HashMap<>();
        for (Trade trade : trades) {
            BigDecimal signedQty = (trade.getSide() == TradeSide.BUY) ? trade.getQuantity() : trade.getQuantity().negate();
            assetQuantity.merge(trade.getAssetId(), signedQty, BigDecimal::add);
        }

        Map<Long, Asset> assetsById = new HashMap<>();
        assetRepository.findAllById(assetQuantity.keySet())
                .forEach(a -> assetsById.put(a.getId(), a));

        List<PositionResponse> out = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> e : assetQuantity.entrySet()) {
            Long assetId = e.getKey();
            BigDecimal qty = e.getValue();

            Asset asset = assetsById.get(assetId);
            if (asset == null) {
                throw new IllegalArgumentException("Asset not found: " + assetId);
            }

            out.add(new PositionResponse(
                    assetId,
                    asset.getName(),
                    asset.getSymbol(),
                    asset.getCurrency(),
                    qty,
                    positionType(qty)
            ));
        }
        out.sort(Comparator.comparing(PositionResponse::getAssetName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return out;
    }

    private PositionType positionType(BigDecimal qty) {
        int cmp = qty.compareTo(BigDecimal.ZERO);
        if (cmp > 0) return PositionType.LONG;
        if (cmp < 0) return PositionType.SHORT;
        return PositionType.FLAT;
    }
}
