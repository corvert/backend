package investTracker.controllers;

import investTracker.dtos.asset.AssetResponse;
import investTracker.dtos.asset.CreateAssetRequest;
import investTracker.services.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping
    public AssetResponse create(@Valid @RequestBody CreateAssetRequest request){
        return assetService.create(request);
    }
}
