package investTracker.services;

import investTracker.dtos.asset.AssetResponse;
import investTracker.dtos.asset.CreateAssetRequest;
import investTracker.models.Asset;
import investTracker.models.enums.AssetType;
import investTracker.repositories.AssetRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public AssetResponse create(@Valid CreateAssetRequest request) {
        String symbol = normalizeSymbol(request.getSymbol());
        if(request.getType() == AssetType.STOCK){

            if (symbol == null){
                throw new IllegalArgumentException("STOCK asset requires symbol");
            }
            if(assetRepository.findBySymbol(symbol).isPresent()){
                throw new IllegalArgumentException("Asset with symbol " + symbol + " already exists");
            }
        }
        Asset asset = new Asset();
        asset.setType(request.getType());
        asset.setSymbol(symbol);
        asset.setName(request.getName());
        asset.setCurrency(request.getCurrency());

        if(request.getType() == AssetType.P2P){
            asset.setCurrency("EUR");
        }
        Asset savedAsset = assetRepository.save(asset);
        return toAssetDto(savedAsset);
    }

    private String normalizeSymbol(String symbol) {
        if(symbol == null){
            return null;
        }
        String asset = symbol.trim().toUpperCase();
        return asset.isBlank() ? null : asset;
    }

    private AssetResponse toAssetDto(Asset asset) {
        return new AssetResponse(
            asset.getId(),
            asset.getType(),
            asset.getSymbol(),
            asset.getName(),
            asset.getCurrency()
        );
    }


}
