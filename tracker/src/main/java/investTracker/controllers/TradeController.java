package investTracker.controllers;

import investTracker.dtos.trade.CreateTradeRequest;
import investTracker.dtos.trade.TradeResponse;
import investTracker.services.TradeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @Transactional
    public TradeResponse createTrade(@Valid @RequestBody CreateTradeRequest request) {
        return tradeService.createTrade(request);
    }

    @GetMapping
    public List<TradeResponse> tradeList(
            @RequestParam Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate to
            ){
        return tradeService.tradeList(accountId, from, to);
    }
}
