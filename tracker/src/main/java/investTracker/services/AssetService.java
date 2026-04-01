package investTracker.services;

import investTracker.dtos.asset.AssetResponse;
import investTracker.dtos.asset.CreateAssetRequest;
import investTracker.models.Asset;
import investTracker.models.enums.AssetType;
import investTracker.repositories.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    private static final Pattern ISIN_PATTERN = Pattern.compile("^[A-Z]{2}[A-Z0-9]{9}[0-9]$");

    public AssetResponse create(CreateAssetRequest request) {
        AssetType assetType = request.getType();
        if(assetType == null){
            throw new IllegalArgumentException("Asset type is required");
        }
        String symbol = normalizeString(request.getSymbol());
        String isin = normalizeString(request.getIsin());
        String name = request.getName() != null ? request.getName().trim() : null;
        String currency = request.getCurrency() != null ? request.getCurrency().trim().toUpperCase() : null;

        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Asset name is required");
        }
        if(assetType == AssetType.P2P){
            currency = "EUR";
        }else{
            if(currency == null || currency.isBlank()){
                throw new IllegalArgumentException("Currency is required for non-P2P assets");
            }
            if(symbol == null && isin == null){
                throw new IllegalArgumentException("Either symbol or ISIN is required for non-P2P assets");
            }
        }
        if (isin != null && !ISIN_PATTERN.matcher(isin).matches()) {
            throw new IllegalArgumentException("Invalid ISIN format");
        }
        if(symbol != null && assetRepository.findBySymbol(symbol).isPresent()){
            throw new IllegalArgumentException("Asset with symbol '" + symbol + "' already exists");
        }
        if(isin != null && assetRepository.findByIsin(isin).isPresent()) {
            throw new IllegalArgumentException("Asset with ISIN '" + isin + "' already exists");
        }

        Asset asset = new Asset();
        asset.setType(assetType);
        asset.setSymbol(symbol);
        asset.setIsin(isin);
        asset.setName(name);
        asset.setCurrency(currency);

        Asset savedAsset = assetRepository.save(asset);
        return toAssetDto(savedAsset);
    }

    public List<AssetResponse> assetList(AssetType assetType) {
        List<Asset> assets = (assetType == null) ? assetRepository.findAll()
                : assetRepository.findByTypeOrderByNameAsc(assetType);
        return assets.stream().map(this::toAssetDto).toList();
    }

    private String normalizeString(String string) {
        if(string == null){
            return null;
        }
        String asset = string.trim().toUpperCase();
        return asset.isBlank() ? null : asset;
    }

    private AssetResponse toAssetDto(Asset asset) {
        return new AssetResponse(
            asset.getId(),
            asset.getType(),
            asset.getSymbol(),
            asset.getIsin(),
            asset.getName(),
            asset.getCurrency()
        );
    }



}
