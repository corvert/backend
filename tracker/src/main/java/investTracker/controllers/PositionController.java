package investTracker.controllers;

import investTracker.dtos.position.PositionResponse;
import investTracker.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping("/{accountId}/positions")
    public List<PositionResponse> getPositions(@PathVariable Long accountId) {
        return positionService.positionsForAccount(accountId);
    }
}
