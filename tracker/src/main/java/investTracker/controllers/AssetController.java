package investTracker.controllers;

import investTracker.dtos.asset.AssetResponse;
import investTracker.dtos.asset.CreateAssetRequest;
import investTracker.models.enums.AssetType;
import investTracker.services.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping
    public AssetResponse create(@Valid @RequestBody CreateAssetRequest request){
        return assetService.create(request);
    }

    @GetMapping
    public List<AssetResponse> assetList(@RequestParam(required = false)AssetType assetType){
        return assetService.assetList(assetType);
    }
}
