package investTracker.controllers;

import investTracker.dtos.trade.CreateTradeRequest;
import investTracker.dtos.trade.TradeResponse;
import investTracker.services.TradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @PostMapping
    public TradeResponse createTrade(@Valid @RequestBody CreateTradeRequest request) {
        return tradeService.createTrade(request);
    }
}
